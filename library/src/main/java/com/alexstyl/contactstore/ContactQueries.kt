package com.alexstyl.contactstore

import android.provider.ContactsContract.CommonDataKinds.Email as EmailColumns
import android.provider.ContactsContract.CommonDataKinds.Event as EventColumns
import android.provider.ContactsContract.CommonDataKinds.GroupMembership as GroupColumns
import android.provider.ContactsContract.CommonDataKinds.Im as ImColumns
import android.provider.ContactsContract.CommonDataKinds.Nickname as NicknameColumns
import android.provider.ContactsContract.CommonDataKinds.Note as NoteColumns
import android.provider.ContactsContract.CommonDataKinds.Organization as OrganizationColumns
import android.provider.ContactsContract.CommonDataKinds.Phone as PhoneColumns
import android.provider.ContactsContract.CommonDataKinds.Photo as PhotoColumns
import android.provider.ContactsContract.CommonDataKinds.Relation as RelationColumns
import android.provider.ContactsContract.CommonDataKinds.SipAddress as SipColumns
import android.provider.ContactsContract.CommonDataKinds.StructuredName as NameColumns
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal as PostalColumns
import android.provider.ContactsContract.CommonDataKinds.Website as WebColumns
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.res.Resources
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.BaseTypes
import android.provider.ContactsContract.Contacts
import android.provider.ContactsContract.Data
import android.provider.ContactsContract.FullNameStyle
import android.provider.ContactsContract.PhoneticNameStyle
import android.provider.ContactsContract.RawContacts
import com.alexstyl.contactstore.ContactColumn.*
import com.alexstyl.contactstore.ContactPredicate.ContactLookup
import com.alexstyl.contactstore.ContactPredicate.MailLookup
import com.alexstyl.contactstore.ContactPredicate.NameLookup
import com.alexstyl.contactstore.ContactPredicate.PhoneLookup
import com.alexstyl.contactstore.utils.DateParser
import com.alexstyl.contactstore.utils.iterate
import com.alexstyl.contactstore.utils.mapEachRow
import com.alexstyl.contactstore.utils.runQuery
import com.alexstyl.contactstore.utils.runQueryFlow
import com.alexstyl.contactstore.utils.valueIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.InputStream

internal class ContactQueries(
    private val contentResolver: ContentResolver,
    private val resources: Resources,
    private val dateParser: DateParser,
    private val accountInfoResolver: AccountInfoResolver
) {
    private val linkedAccountMimeTypes by lazy {
        accountInfoResolver
            .fetchLinkedAccountMimeTypes()
            .associateBy { it.mimetype }
    }

    fun queryContacts(
        predicate: ContactPredicate?,
        columnsToFetch: List<ContactColumn>,
        displayNameStyle: DisplayNameStyle
    ): Flow<List<Contact>> {
        return queryContacts(predicate, displayNameStyle)
            .map { contacts ->
                if (columnsToFetch.isEmpty()) {
                    contacts
                } else {
                    fetchAdditionalColumns(contacts, columnsToFetch)
                }
            }
    }

    private fun queryContacts(
        predicate: ContactPredicate?,
        displayNameStyle: DisplayNameStyle
    ): Flow<List<PartialContact>> {
        return when (predicate) {
            null -> queryAllContacts(displayNameStyle)
            is ContactLookup -> lookupFromPredicate(predicate, displayNameStyle)
            is MailLookup -> lookupFromMail(predicate.mailAddress, displayNameStyle)
            is PhoneLookup -> lookupFromPhone(predicate.phoneNumber, displayNameStyle)
            is NameLookup -> lookupFromName(predicate.partOfName, displayNameStyle)
        }
    }

    private fun lookupFromName(
        name: String,
        displayNameStyle: DisplayNameStyle
    ): Flow<List<PartialContact>> {
        return contentResolver.runQueryFlow(
            contentUri = Contacts.CONTENT_FILTER_URI.buildUpon()
                .appendEncodedPath(name)
                .build(),
            projection = ContactsQuery.projection(displayNameStyle),
            sortOrder = ContactsQuery.sortOrder(displayNameStyle),
        ).map { cursor ->
            cursor.mapEachRow {
                PartialContact(
                    contactId = ContactsQuery.getContactId(it),
                    lookupKey = ContactsQuery.getLookupKey(it),
                    displayName = ContactsQuery.getDisplayName(it),
                    isStarred = ContactsQuery.getIsStarred(it),
                    columns = emptyList()
                )
            }
        }
    }

    private fun lookupFromPredicate(
        predicate: ContactLookup,
        displayNameStyle: DisplayNameStyle
    ): Flow<List<PartialContact>> {
        return contentResolver.runQueryFlow(
            contentUri = Contacts.CONTENT_URI,
            projection = ContactsQuery.projection(displayNameStyle),
            selection = buildColumnsToFetchSelection(predicate),
            sortOrder = ContactsQuery.sortOrder(displayNameStyle)
        ).map { cursor ->
            cursor.mapEachRow {
                PartialContact(
                    contactId = ContactsQuery.getContactId(it),
                    lookupKey = ContactsQuery.getLookupKey(it),
                    displayName = ContactsQuery.getDisplayName(it),
                    isStarred = ContactsQuery.getIsStarred(it),
                    columns = emptyList()
                )
            }
        }
    }

    private fun lookupFromMail(
        mailAddress: MailAddress,
        displayNameStyle: DisplayNameStyle
    ): Flow<List<PartialContact>> {
        return contentResolver.runQueryFlow(
            contentUri = EmailColumns.CONTENT_FILTER_URI.buildUpon()
                .appendEncodedPath(mailAddress.raw)
                .build(),
            projection = FilterQuery.projection(displayNameStyle),
            sortOrder = FilterQuery.sortOrder(displayNameStyle)
        ).map { cursor ->
            cursor.mapEachRow {
                PartialContact(
                    contactId = FilterQuery.getContactId(it),
                    displayName = FilterQuery.getDisplayName(it),
                    isStarred = FilterQuery.getIsStarred(it),
                    lookupKey = FilterQuery.getLookupKey(it),
                    columns = emptyList()
                )
            }
        }
    }

    private fun buildColumnsToFetchSelection(predicate: ContactLookup): String {
        return buildString {
            predicate.inContactIds?.let { contactIds ->
                append("${Contacts._ID} IN ${valueIn(contactIds)}")
            }
            predicate.isFavorite?.let { isTrue ->
                if (isNotEmpty()) {
                    append(" AND")
                }
                append(" ${Contacts.STARRED} = ${isTrue.toBoolInt()}")
            }
        }
    }

    private fun lookupFromPhone(
        phoneNumber: PhoneNumber,
        displayNameStyle: DisplayNameStyle
    ): Flow<List<PartialContact>> {
        return contentResolver.runQueryFlow(
            contentUri = ContactsContract.PhoneLookup.CONTENT_FILTER_URI.buildUpon()
                .appendEncodedPath(phoneNumber.raw)
                .build(),
            projection = arrayOf(
                PHONE_LOOKUP_CONTACT_ID,
                if (displayNameStyle == DisplayNameStyle.Primary) {
                    ContactsContract.PhoneLookup.DISPLAY_NAME_PRIMARY
                } else {
                    ContactsContract.PhoneLookup.DISPLAY_NAME_ALTERNATIVE
                },
                ContactsContract.PhoneLookup.STARRED,
                ContactsContract.PhoneLookup.LOOKUP_KEY
            ),
            sortOrder = if (displayNameStyle == DisplayNameStyle.Primary) {
                ContactsContract.PhoneLookup.DISPLAY_NAME_PRIMARY
            } else {
                ContactsContract.PhoneLookup.DISPLAY_NAME_ALTERNATIVE
            }
        ).map { cursor ->
            cursor.mapEachRow {
                PartialContact(
                    contactId = it.getLong(0),
                    displayName = it.getString(1),
                    isStarred = it.getInt(2) == 1,
                    lookupKey = it.getString(3)?.let { raw -> LookupKey(raw) },
                    columns = emptyList()
                )
            }
        }

    }

    private fun queryAllContacts(
        displayNameStyle: DisplayNameStyle
    ): Flow<List<PartialContact>> {
        return contentResolver.runQueryFlow(
            contentUri = Contacts.CONTENT_URI,
            projection = ContactsQuery.projection(displayNameStyle),
            sortOrder = ContactsQuery.sortOrder(displayNameStyle)
        ).map { cursor ->
            cursor.mapEachRow {
                PartialContact(
                    contactId = ContactsQuery.getContactId(it),
                    lookupKey = ContactsQuery.getLookupKey(it),
                    displayName = ContactsQuery.getDisplayName(it),
                    isStarred = ContactsQuery.getIsStarred(it),
                    columns = emptyList()
                )
            }
        }
    }

    private fun fetchAdditionalColumns(
        forContacts: List<PartialContact>,
        columnsToFetch: List<ContactColumn>
    ): List<Contact> {
        return forContacts.map { contact ->
            val rawContacts = fetchRawContacts(contact)

            val contactId = contact.contactId
            var firstName: String? = null
            var middleName: String? = null
            var lastName: String? = null
            var prefix: String? = null
            var suffix: String? = null
            var fullNameStyle: Int = FullNameStyle.UNDEFINED
            var nickname: String? = null
            var phoneticFirstName: String? = null
            var phoneticMiddleName: String? = null
            var phoneticLastName: String? = null
            var phoneticNameStyle: Int = PhoneticNameStyle.UNDEFINED
            var imageData: ImageData? = null
            val phones = mutableSetOf<LabeledValue<PhoneNumber>>()
            val mails = mutableSetOf<LabeledValue<MailAddress>>()
            val webAddresses = mutableSetOf<LabeledValue<WebAddress>>()
            val sipAddresses = mutableSetOf<LabeledValue<SipAddress>>()
            val events = mutableSetOf<LabeledValue<EventDate>>()
            val imAddresses = mutableSetOf<LabeledValue<ImAddress>>()
            val relations = mutableSetOf<LabeledValue<Relation>>()
            val postalAddresses = mutableSetOf<LabeledValue<PostalAddress>>()
            var organization: String? = null
            var jobTitle: String? = null
            var note: Note? = null
            val groupIds = mutableListOf<GroupMembership>()
            val linkedAccountValues = mutableListOf<LinkedAccountValue>()

            rawContacts.forEach { rawContact ->
                val accountType: String? =
                    rawContact.rawContactContentValues.getAsString(Contacts.Entity.ACCOUNT_TYPE)
                val accountName: String? =
                    rawContact.rawContactContentValues.getAsString(Contacts.Entity.ACCOUNT_NAME)

                val accountInfo = accountType?.let {
                    AccountInfo(accountName, accountType)
                }
                rawContact.dataItems.forEach { item ->
                    when (val mimetype = item[Contacts.Data.MIMETYPE]) {
                        NicknameColumns.CONTENT_ITEM_TYPE -> {
                            nickname = item.getAsString(NicknameColumns.NAME)
                        }
                        GroupColumns.CONTENT_ITEM_TYPE -> {
                            val groupId = item.getAsLong(GroupColumns.GROUP_ROW_ID)
                            if (groupId != null) {
                                groupIds.add(GroupMembership(groupId))
                            }
                        }
                        NameColumns.CONTENT_ITEM_TYPE -> {
                            firstName = item.getAsString(NameColumns.GIVEN_NAME)
                            middleName = item.getAsString(NameColumns.MIDDLE_NAME)
                            lastName = item.getAsString(NameColumns.FAMILY_NAME)
                            prefix = item.getAsString(NameColumns.PREFIX)
                            suffix = item.getAsString(NameColumns.SUFFIX)
                            fullNameStyle = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                item.getAsInteger(NameColumns.FULL_NAME_STYLE)
                                    ?: FullNameStyle.UNDEFINED
                            } else {
                                FullNameStyle.UNDEFINED
                            }
                            phoneticFirstName = item.getAsString(NameColumns.PHONETIC_GIVEN_NAME)
                            phoneticMiddleName = item.getAsString(NameColumns.PHONETIC_MIDDLE_NAME)
                            phoneticLastName = item.getAsString(NameColumns.PHONETIC_FAMILY_NAME)
                            phoneticNameStyle = item.getAsInteger(NameColumns.PHONETIC_NAME_STYLE)
                                ?: PhoneticNameStyle.UNDEFINED
                        }
                        PhotoColumns.CONTENT_ITEM_TYPE -> {
                            imageData = loadContactPhoto(contactId)
                        }
                        PhoneColumns.CONTENT_ITEM_TYPE -> {
                            val phoneNumberString = item.getAsString(PhoneColumns.NUMBER)
                            val id = item.getAsLong(PhoneColumns._ID)
                            if (phoneNumberString.isNotBlank() && id != null) {
                                val value = PhoneNumber(phoneNumberString)
                                val phoneEntry = LabeledValue(value, phoneLabelFrom(item), id)
                                phones.add(phoneEntry)
                            }
                        }
                        EmailColumns.CONTENT_ITEM_TYPE -> {
                            val mailAddressString = item.getAsString(EmailColumns.ADDRESS)
                            val id = item.getAsLong(EmailColumns._ID)
                            if (mailAddressString.isNotBlank() && id != null) {
                                val mailAddress = MailAddress(mailAddressString)
                                mails.add(
                                    LabeledValue(
                                        mailAddress,
                                        mailLabelFrom(item),
                                        id
                                    )
                                )
                            }
                        }
                        WebColumns.CONTENT_ITEM_TYPE -> {
                            val webAddressString = item.getAsString(WebColumns.URL)
                            val id = item.getAsLong(WebColumns._ID)
                            if (webAddressString.isNotBlank() && id != null) {
                                val mailAddress = WebAddress(Uri.parse(webAddressString))
                                webAddresses.add(
                                    LabeledValue(mailAddress, webLabelFrom(item), id)
                                )
                            }
                        }
                        NoteColumns.CONTENT_ITEM_TYPE -> {
                            val noteString = item.getAsString(NoteColumns.NOTE)
                            if (noteString.isNotBlank()) {
                                note = Note(noteString)
                            }
                        }
                        EventColumns.CONTENT_ITEM_TYPE -> {
                            val parsedDate = dateParser.parse(item.getAsString(EventColumns.START_DATE))
                            val id = item.getAsLong(EventColumns._ID)
                            if (parsedDate != null && id != null) {
                                val entry = LabeledValue(parsedDate, eventLabelFrom(item), id)
                                events.add(entry)
                            }
                        }
                        SipColumns.CONTENT_ITEM_TYPE -> {
                            val address = item.getAsString(SipColumns.SIP_ADDRESS)
                            val id = item.getAsLong(SipColumns._ID)
                            if (address.isNotBlank() && id != null) {
                                val value = LabeledValue(SipAddress(address), sipLabel(item), id)
                                sipAddresses.add(value)
                            }
                        }
                        PostalColumns.CONTENT_ITEM_TYPE -> {
                            val formattedAddress = item.getAsString(PostalColumns.FORMATTED_ADDRESS)
                            val id = item.getAsLong(PostalColumns._ID)
                            if (formattedAddress.isNotBlank() && id != null) {
                                val street = item.getAsString(PostalColumns.STREET).trim()
                                val poBox = item.getAsString(PostalColumns.POBOX).trim()
                                val neighborhood = item.getAsString(PostalColumns.NEIGHBORHOOD).trim()
                                val city = item.getAsString(PostalColumns.CITY).trim()
                                val region = item.getAsString(PostalColumns.REGION).trim()
                                val postCode = item.getAsString(PostalColumns.POSTCODE).trim()
                                val country = item.getAsString(PostalColumns.COUNTRY).trim()
                                val value = PostalAddress(
                                    street = street,
                                    poBox = poBox,
                                    neighborhood = neighborhood,
                                    city = city,
                                    region = region,
                                    postCode = postCode,
                                    country = country,
                                )
                                val postalAddressEntry = LabeledValue(
                                    value, postalAddressLabelFrom(item), id
                                )
                                postalAddresses.add(postalAddressEntry)
                            }
                        }
                        OrganizationColumns.CONTENT_ITEM_TYPE -> {
                            organization = item.getAsString(OrganizationColumns.COMPANY)
                            jobTitle = item.getAsString(OrganizationColumns.TITLE)
                        }
                        ImColumns.CONTENT_ITEM_TYPE -> {
                            val imAddressString = item.getAsString(ImColumns.DATA)
                            val id = item.getAsLong(ImColumns._ID)
                            if (imAddressString.isNotBlank() && id != null) {
                                val protocol = getImProtocol(item)
                                val imAddress = ImAddress(raw = imAddressString, protocol = protocol)
                                val label = imLabelFrom(item)
                                imAddresses.add(
                                    LabeledValue(imAddress, label, id)
                                )
                            }
                        }
                        RelationColumns.CONTENT_ITEM_TYPE -> {
                            val name = item.getAsString(RelationColumns.NAME)
                            val id = item.getAsLong(RelationColumns._ID)
                            if (name.isNotBlank() && id != null) {
                                val label = relationLabel(item)
                                relations.add(LabeledValue(Relation(name), label, id))
                            }
                        }
                        else -> {
                            val mimeType = linkedAccountMimeTypes[mimetype]
                            if (mimeType != null) {
                                val id = item.getAsLong(Contacts.Data._ID)
                                if (id != null) {
                                    val value = LinkedAccountValue(
                                        id = id,
                                        accountType = accountInfo!!.type,
                                        summary = item.getAsString(mimeType.summaryColumn),
                                        detail = item.getAsString(mimeType.detailColumn),
                                        icon = mimeType.icon,
                                        mimeType = mimeType.mimetype,
                                        account = accountInfo
                                    )
                                    linkedAccountValues.add(value)
                                }
                            }
                        }
                    }
                }
            }

            PartialContact(
                contactId = contactId,
                lookupKey = contact.lookupKey,
                columns = columnsToFetch,
                isStarred = contact.isStarred,
                displayName = contact.displayName,
                firstName = firstName,
                lastName = lastName,
                imageData = imageData,
                organization = organization,
                jobTitle = jobTitle,
                webAddresses = webAddresses.toList(),
                phones = phones.toList(),
                postalAddresses = postalAddresses.toList(),
                mails = mails.toList(),
                events = events.toList(),
                note = note,
                prefix = prefix,
                middleName = middleName,
                suffix = suffix,
                phoneticLastName = phoneticLastName,
                phoneticFirstName = phoneticFirstName,
                fullNameStyle = fullNameStyle,
                nickname = nickname,
                phoneticMiddleName = phoneticMiddleName,
                phoneticNameStyle = phoneticNameStyle,
                sipAddresses = sipAddresses.toList(),
                groups = groupIds.toList(),
                linkedAccountValues = linkedAccountValues.toList(),
                imAddresses = imAddresses.toList(),
                relations = relations.toList()
            )
        }
    }

    private fun fetchRawContacts(contact: PartialContact): MutableList<RawContact> {
        var rawContact: RawContact? = null
        var currentRawContactId: Long = -1
        val rawContacts = mutableListOf<RawContact>()

        contentResolver.runQuery(
            contentUri = entityUri(contact),
            projection = ContactQuery.COLUMNS,
            sortOrder = Contacts.Entity.RAW_CONTACT_ID
        ).iterate { cursor ->
            val rawContactId = cursor.getLong(ContactQuery.RAW_CONTACT_ID)
            if (currentRawContactId != rawContactId) {
                currentRawContactId = rawContactId
                rawContact = RawContact(loadRawContactValues(cursor))
                rawContacts.add(rawContact!!)
            }
            if (!cursor.isNull(ContactQuery.DATA_ID)) {
                val data: ContentValues = loadDataValues(cursor)
                rawContact!!.addDataItemValues(data)
            }
        }
        return rawContacts
    }

    private fun entityUri(forContact: PartialContact): Uri {
        val contactId = forContact.contactId
        val contactUri = ensureIsContactUri(
            contentResolver,
            uri = Contacts.getLookupUri(contactId, forContact.lookupKey?.value)
        )
        return Uri.withAppendedPath(contactUri, Contacts.Entity.CONTENT_DIRECTORY)
    }

    private fun loadDataValues(cursor: Cursor): ContentValues {
        val cv = ContentValues()
        cv.put(Data._ID, cursor.getLong(ContactQuery.DATA_ID))
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA1)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA2)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA3)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA4)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA5)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA6)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA7)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA8)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA9)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA10)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA11)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA12)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA13)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA14)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA15)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA_SYNC1)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA_SYNC2)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA_SYNC3)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA_SYNC4)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA_VERSION)
        cursorColumnToContentValues(cursor, cv, ContactQuery.IS_PRIMARY)
        cursorColumnToContentValues(cursor, cv, ContactQuery.IS_SUPERPRIMARY)
        cursorColumnToContentValues(cursor, cv, ContactQuery.MIMETYPE)
        cursorColumnToContentValues(cursor, cv, ContactQuery.GROUP_SOURCE_ID)
        cursorColumnToContentValues(cursor, cv, ContactQuery.CHAT_CAPABILITY)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cursorColumnToContentValues(cursor, cv, ContactQuery.CARRIER_PRESENCE)
        }
        return cv
    }

    private fun loadRawContactValues(cursor: Cursor): ContentValues {
        val cv = ContentValues()
        cv.put(RawContacts._ID, cursor.getLong(ContactQuery.RAW_CONTACT_ID))
        cursorColumnToContentValues(cursor, cv, ContactQuery.ACCOUNT_NAME)
        cursorColumnToContentValues(cursor, cv, ContactQuery.ACCOUNT_TYPE)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DATA_SET)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DIRTY)
        cursorColumnToContentValues(cursor, cv, ContactQuery.VERSION)
        cursorColumnToContentValues(cursor, cv, ContactQuery.SOURCE_ID)
        cursorColumnToContentValues(cursor, cv, ContactQuery.SYNC1)
        cursorColumnToContentValues(cursor, cv, ContactQuery.SYNC2)
        cursorColumnToContentValues(cursor, cv, ContactQuery.SYNC3)
        cursorColumnToContentValues(cursor, cv, ContactQuery.SYNC4)
        cursorColumnToContentValues(cursor, cv, ContactQuery.DELETED)
        cursorColumnToContentValues(cursor, cv, ContactQuery.CONTACT_ID)
        cursorColumnToContentValues(cursor, cv, ContactQuery.STARRED)
        return cv
    }

    private fun cursorColumnToContentValues(
        cursor: Cursor, values: ContentValues, index: Int
    ) {
        when (cursor.getType(index)) {
            Cursor.FIELD_TYPE_NULL -> {}
            Cursor.FIELD_TYPE_INTEGER -> values.put(
                ContactQuery.COLUMNS[index],
                cursor.getLong(index)
            )
            Cursor.FIELD_TYPE_STRING -> values.put(
                ContactQuery.COLUMNS[index],
                cursor.getString(index)
            )
            Cursor.FIELD_TYPE_BLOB -> values.put(ContactQuery.COLUMNS[index], cursor.getBlob(index))
            else -> throw IllegalStateException("Invalid or unhandled data type")
        }
    }

    private fun sipLabel(values: ContentValues): Label {
        return when (values.getAsInteger(SipColumns.TYPE)) {
            SipColumns.TYPE_HOME -> Label.LocationHome
            SipColumns.TYPE_OTHER -> Label.Other
            SipColumns.TYPE_WORK -> Label.LocationWork
            SipColumns.TYPE_CUSTOM -> Label.Custom(values.getAsString(SipColumns.LABEL))
            else -> Label.Other
        }
    }

    private fun relationLabel(contentValues: ContentValues): Label {
        return when (contentValues.getAsInteger(RelationColumns.TYPE)) {
            RelationColumns.TYPE_ASSISTANT -> Label.PhoneNumberAssistant
            RelationColumns.TYPE_BROTHER -> Label.RelationBrother
            RelationColumns.TYPE_CHILD -> Label.RelationChild
            RelationColumns.TYPE_DOMESTIC_PARTNER -> Label.RelationDomesticPartner
            RelationColumns.TYPE_FATHER -> Label.RelationFather
            RelationColumns.TYPE_FRIEND -> Label.RelationFriend
            RelationColumns.TYPE_MANAGER -> Label.RelationManager
            RelationColumns.TYPE_MOTHER -> Label.RelationMother
            RelationColumns.TYPE_PARENT -> Label.RelationParent
            RelationColumns.TYPE_PARTNER -> Label.RelationPartner
            RelationColumns.TYPE_REFERRED_BY -> Label.RelationReferredBy
            RelationColumns.TYPE_RELATIVE -> Label.RelationRelative
            RelationColumns.TYPE_SISTER -> Label.RelationSister
            RelationColumns.TYPE_SPOUSE -> Label.RelationSpouse
            RelationColumns.TYPE_CUSTOM -> Label.Custom(
                contentValues.getAsString(RelationColumns.LABEL)
            )
            else -> Label.Other
        }
    }

    private fun getImProtocol(contentValues: ContentValues): String {
        // starting from Android 31, type will always be PROTOCOL_CUSTOM according to docs
        // the else covers legacy versions
        return when (val type = contentValues.getAsInteger(ImColumns.PROTOCOL)) {
            null, ImColumns.PROTOCOL_CUSTOM -> contentValues.getAsString(ImColumns.CUSTOM_PROTOCOL)
            else -> resources.getString(ImColumns.getProtocolLabelResource(type))
        }
    }

    private fun loadContactPhoto(id: Long): ImageData? {
        val uri: Uri = ContentUris.withAppendedId(Contacts.CONTENT_URI, id)
        val inputStream: InputStream? =
            Contacts.openContactPhotoInputStream(contentResolver, uri, true)
        return inputStream
            ?.toByteArray()
            ?.let { ImageData(it) }
    }

    private fun InputStream.toByteArray(): ByteArray {
        return buffered().use { it.readBytes() }
    }

    private val standardMimeTypes = listOf(
        PhoneColumns.CONTENT_ITEM_TYPE,
        EmailColumns.CONTENT_ITEM_TYPE,
        NoteColumns.CONTENT_ITEM_TYPE,
        EventColumns.CONTENT_ITEM_TYPE,
        PostalColumns.CONTENT_ITEM_TYPE,
        PhotoColumns.CONTENT_ITEM_TYPE,
        NameColumns.CONTENT_ITEM_TYPE,
        WebColumns.CONTENT_ITEM_TYPE,
        OrganizationColumns.CONTENT_ITEM_TYPE,
        NicknameColumns.CONTENT_ITEM_TYPE,
        GroupColumns.CONTENT_ITEM_TYPE,
        ImColumns.CONTENT_ITEM_TYPE,
        RelationColumns.CONTENT_ITEM_TYPE,
        SipColumns.CONTENT_ITEM_TYPE,
    )

    private fun buildSelectionArgs(columnsToFetch: List<ContactColumn>): Array<String> {
        val linkedAccountColumns = columnsToFetch.filterIsInstance<LinkedAccountValues>()
        val standardColumns = columnsToFetch - linkedAccountColumns
        return standardColumns.map { column ->
            when (column) {
                Phones -> PhoneColumns.CONTENT_ITEM_TYPE
                Mails -> EmailColumns.CONTENT_ITEM_TYPE
                Note -> NoteColumns.CONTENT_ITEM_TYPE
                Events -> EventColumns.CONTENT_ITEM_TYPE
                PostalAddresses -> PostalColumns.CONTENT_ITEM_TYPE
                Image -> PhotoColumns.CONTENT_ITEM_TYPE
                Names -> NameColumns.CONTENT_ITEM_TYPE
                WebAddresses -> WebColumns.CONTENT_ITEM_TYPE
                Organization -> OrganizationColumns.CONTENT_ITEM_TYPE
                Nickname -> NicknameColumns.CONTENT_ITEM_TYPE
                GroupMemberships -> GroupColumns.CONTENT_ITEM_TYPE
                ImAddresses -> ImColumns.CONTENT_ITEM_TYPE
                Relations -> RelationColumns.CONTENT_ITEM_TYPE
                SipAddresses -> SipColumns.CONTENT_ITEM_TYPE
                is LinkedAccountValues ->
                    error("Tried to map a LinkedAccountColumn as standard column")
            }
        }.toTypedArray() + linkedAccountColumns.map { it.accountType }.toTypedArray()
    }

    @Throws(IllegalArgumentException::class)
    fun ensureIsContactUri(resolver: ContentResolver, uri: Uri): Uri? {
        val authority = uri.authority

        // Current Style Uri?
        if (ContactsContract.AUTHORITY == authority) {
            val type = resolver.getType(uri)
            // Contact-Uri? Good, return it
            if (Contacts.CONTENT_ITEM_TYPE == type) {
                return uri
            }

            // RawContact-Uri? Transform it to ContactUri
            if (RawContacts.CONTENT_ITEM_TYPE == type) {
                val rawContactId = ContentUris.parseId(uri)
                return RawContacts.getContactLookupUri(
                    resolver,
                    ContentUris.withAppendedId(RawContacts.CONTENT_URI, rawContactId)
                )
            }
            throw IllegalArgumentException("uri format is unknown")
        }

        // Legacy Style? Convert to RawContact
        val OBSOLETE_AUTHORITY = android.provider.Contacts.AUTHORITY
        if (OBSOLETE_AUTHORITY == authority) {
            // Legacy Format. Convert to RawContact-Uri and then lookup the contact
            val rawContactId = ContentUris.parseId(uri)
            return RawContacts.getContactLookupUri(
                resolver,
                ContentUris.withAppendedId(RawContacts.CONTENT_URI, rawContactId)
            )
        }
        throw IllegalArgumentException("uri authority is unknown")
    }

    private fun buildColumnsToFetchSelection(
        forContactId: Long,
        columnsToFetch: List<ContactColumn>
    ): String {
        val columnsQuery = buildString {
            val linkedAccountColumns = columnsToFetch.filterIsInstance<LinkedAccountValues>()
            val standardColumns = columnsToFetch - linkedAccountColumns

            if (standardColumns.isNotEmpty()) {
                append(
                    " ${Data.MIMETYPE} IN ${
                        valueIn(List(standardColumns.size) { "?" })
                    }"
                )
            }
            if (linkedAccountColumns.isNotEmpty()) {
                if (isNotBlank()) {
                    append(" OR ")
                }
                append(
                    " ${RawContacts.ACCOUNT_TYPE} IN ${
                        valueIn(List(linkedAccountColumns.size) { "?" })
                    }"
                )
            }
        }

        return "${Data.CONTACT_ID} = $forContactId AND ($columnsQuery)"
    }

    private fun postalAddressLabelFrom(contentValues: ContentValues): Label {
        return when (
            contentValues.getAsInteger(PostalColumns.TYPE) ?:PostalColumns.TYPE_CUSTOM) {
            BaseTypes.TYPE_CUSTOM -> Label.Custom(contentValues.getAsString(PostalColumns.LABEL))
            PostalColumns.TYPE_HOME -> Label.LocationHome
            PostalColumns.TYPE_WORK -> Label.LocationWork
            PostalColumns.TYPE_OTHER -> Label.Other
            else -> Label.Other
        }
    }

    private fun eventLabelFrom(values: ContentValues): Label {
        return when (values.getAsInteger(EventColumns.TYPE) ?: EventColumns.TYPE_CUSTOM) {
            BaseTypes.TYPE_CUSTOM -> Label.Custom(values.getAsString(EventColumns.LABEL))
            EventColumns.TYPE_ANNIVERSARY -> Label.DateAnniversary
            EventColumns.TYPE_BIRTHDAY -> Label.DateBirthday
            EventColumns.TYPE_OTHER -> Label.Other
            else -> Label.Other
        }
    }

    private fun mailLabelFrom(values: ContentValues): Label {
        return when (values.getAsString(EmailColumns.TYPE).ifBlank { "${EmailColumns.TYPE_OTHER}" }
            .toIntOrNull()) {
            BaseTypes.TYPE_CUSTOM -> Label.Custom(values.getAsString(EmailColumns.LABEL))
            EmailColumns.TYPE_HOME -> Label.LocationHome
            EmailColumns.TYPE_WORK -> Label.LocationWork
            EmailColumns.TYPE_OTHER -> Label.Other
            else -> Label.Other
        }
    }

    private fun webLabelFrom(values: ContentValues): Label {
        return when (values.getAsInteger(WebColumns.TYPE) ?: WebColumns.TYPE_OTHER) {
            BaseTypes.TYPE_CUSTOM -> Label.Custom(values.getAsString(WebColumns.LABEL))
            WebColumns.TYPE_HOME -> Label.LocationHome
            WebColumns.TYPE_HOMEPAGE -> Label.WebsiteHomePage
            WebColumns.TYPE_BLOG -> Label.WebsiteBlog
            WebColumns.TYPE_FTP -> Label.WebsiteFtp
            WebColumns.TYPE_PROFILE -> Label.WebsiteProfile
            WebColumns.TYPE_WORK -> Label.LocationWork
            else -> Label.Other
        }
    }

    private fun imLabelFrom(cursor: ContentValues): Label {
        return when (cursor.getAsInteger(ImColumns.TYPE)?: ImColumns.TYPE_OTHER) {
            BaseTypes.TYPE_CUSTOM -> Label.Custom(cursor.getAsString(ImColumns.LABEL))
            ImColumns.TYPE_HOME -> Label.LocationHome
            ImColumns.TYPE_WORK -> Label.LocationWork
            else -> Label.Other
        }
    }

    private fun phoneLabelFrom(cursor: ContentValues): Label {
        return when (cursor.getAsString(PhoneColumns.TYPE).ifBlank { "${PhoneColumns.TYPE_OTHER}" }
            .toIntOrNull()) {
            BaseTypes.TYPE_CUSTOM -> Label.Custom(cursor.getAsString(PhoneColumns.LABEL))
            PhoneColumns.TYPE_HOME -> Label.LocationHome
            PhoneColumns.TYPE_MOBILE -> Label.PhoneNumberMobile
            PhoneColumns.TYPE_WORK -> Label.LocationWork
            PhoneColumns.TYPE_FAX_WORK -> Label.PhoneNumberFaxWork
            PhoneColumns.TYPE_FAX_HOME -> Label.PhoneNumberFaxHome
            PhoneColumns.TYPE_PAGER -> Label.PhoneNumberPager
            PhoneColumns.TYPE_OTHER -> Label.Other
            PhoneColumns.TYPE_CALLBACK -> Label.PhoneNumberCallback
            PhoneColumns.TYPE_CAR -> Label.PhoneNumberCar
            PhoneColumns.TYPE_COMPANY_MAIN -> Label.PhoneNumberCompanyMain
            PhoneColumns.TYPE_ISDN -> Label.PhoneNumberIsdn
            PhoneColumns.TYPE_MAIN -> Label.Main
            PhoneColumns.TYPE_OTHER_FAX -> Label.PhoneNumberOtherFax
            PhoneColumns.TYPE_RADIO -> Label.PhoneNumberRadio
            PhoneColumns.TYPE_TELEX -> Label.PhoneNumberTelex
            PhoneColumns.TYPE_TTY_TDD -> Label.PhoneNumberTtyTdd
            PhoneColumns.TYPE_WORK_MOBILE -> Label.PhoneNumberWorkMobile
            PhoneColumns.TYPE_WORK_PAGER -> Label.PhoneNumberWorkPager
            PhoneColumns.TYPE_ASSISTANT -> Label.PhoneNumberAssistant
            PhoneColumns.TYPE_MMS -> Label.PhoneNumberMms
            else -> Label.Other
        }
    }

    private object ContactsQuery {
        fun projection(displayNameStyle: DisplayNameStyle): Array<String> {
            return when (displayNameStyle) {
                DisplayNameStyle.Primary -> PROJECTION
                DisplayNameStyle.Alternative -> PROJECTION_ALT
            }
        }

        private val PROJECTION = arrayOf(
            Contacts._ID,
            Contacts.DISPLAY_NAME_PRIMARY,
            Contacts.STARRED,
            Contacts.LOOKUP_KEY
        )

        private val PROJECTION_ALT = arrayOf(
            Contacts._ID,
            Contacts.DISPLAY_NAME_ALTERNATIVE,
            Contacts.STARRED,
            Contacts.LOOKUP_KEY
        )

        private const val SORT_ORDER = Contacts.SORT_KEY_PRIMARY
        private const val SORT_ORDER_ALT = Contacts.SORT_KEY_ALTERNATIVE

        fun getContactId(it: Cursor): Long {
            return it.getLong(0)
        }

        fun getDisplayName(cursor: Cursor): String? {
            val indexPrimary = cursor.getColumnIndex(Contacts.DISPLAY_NAME_PRIMARY)
            return if (indexPrimary == -1) {
                val indexAlternative = cursor.getColumnIndex(Contacts.DISPLAY_NAME_ALTERNATIVE)
                cursor.getString(indexAlternative)
            } else {
                cursor.getString(indexPrimary)
            }
        }

        fun getIsStarred(it: Cursor): Boolean {
            return it.getInt(2) == 1
        }

        fun getLookupKey(it: Cursor): LookupKey? {
            return it.getString(3)?.let { raw ->
                LookupKey(raw)
            }
        }

        fun sortOrder(displayNameStyle: DisplayNameStyle): String {
            return when (displayNameStyle) {
                DisplayNameStyle.Primary -> SORT_ORDER
                DisplayNameStyle.Alternative -> SORT_ORDER_ALT
            }
        }
    }

    private object FilterQuery {
        fun projection(displayNameStyle: DisplayNameStyle): Array<String> {
            return when (displayNameStyle) {
                DisplayNameStyle.Primary -> PROJECTION
                DisplayNameStyle.Alternative -> PROJECTION_ALT
            }
        }

        private val PROJECTION = arrayOf(
            EmailColumns.CONTACT_ID,
            EmailColumns.DISPLAY_NAME_PRIMARY,
            EmailColumns.STARRED,
            EmailColumns.LOOKUP_KEY
        )

        private val PROJECTION_ALT = arrayOf(
            EmailColumns.CONTACT_ID,
            EmailColumns.DISPLAY_NAME_ALTERNATIVE,
            EmailColumns.STARRED,
            EmailColumns.LOOKUP_KEY
        )

        private const val SORT_ORDER = EmailColumns.SORT_KEY_PRIMARY
        private const val SORT_ORDER_ALT = EmailColumns.SORT_KEY_ALTERNATIVE

        fun getContactId(it: Cursor): Long {
            return it.getLong(0)
        }

        fun getDisplayName(cursor: Cursor): String? {
            val indexPrimary = cursor.getColumnIndex(EmailColumns.DISPLAY_NAME_PRIMARY)
            return if (indexPrimary == -1) {
                val indexAlternative = cursor.getColumnIndex(EmailColumns.DISPLAY_NAME_ALTERNATIVE)
                cursor.getString(indexAlternative)
            } else {
                cursor.getString(indexPrimary)
            }
        }

        fun getIsStarred(it: Cursor): Boolean {
            return it.getInt(2) == 1
        }

        fun getLookupKey(it: Cursor): LookupKey? {
            return it.getString(3)?.let { raw ->
                LookupKey(raw)
            }
        }

        fun sortOrder(displayNameStyle: DisplayNameStyle): String {
            return when (displayNameStyle) {
                DisplayNameStyle.Primary -> SORT_ORDER
                DisplayNameStyle.Alternative -> SORT_ORDER_ALT
            }
        }
    }

    private companion object {
        val PHONE_LOOKUP_CONTACT_ID = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ContactsContract.PhoneLookup.CONTACT_ID
        } else {
            ContactsContract.PhoneLookup._ID
        }
    }
}

internal fun Boolean.toBoolInt(): String {
    return if (this) {
        "1"
    } else {
        "0"
    }
}