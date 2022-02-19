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
import android.provider.ContactsContract.Contacts.Entity
import android.provider.ContactsContract.FullNameStyle
import android.provider.ContactsContract.PhoneticNameStyle
import com.alexstyl.contactstore.ContactColumn.CustomDataItems
import com.alexstyl.contactstore.ContactColumn.Events
import com.alexstyl.contactstore.ContactColumn.GroupMemberships
import com.alexstyl.contactstore.ContactColumn.ImAddresses
import com.alexstyl.contactstore.ContactColumn.Image
import com.alexstyl.contactstore.ContactColumn.Mails
import com.alexstyl.contactstore.ContactColumn.Names
import com.alexstyl.contactstore.ContactColumn.Nickname
import com.alexstyl.contactstore.ContactColumn.Organization
import com.alexstyl.contactstore.ContactColumn.Phones
import com.alexstyl.contactstore.ContactColumn.PostalAddresses
import com.alexstyl.contactstore.ContactColumn.Relations
import com.alexstyl.contactstore.ContactColumn.SipAddresses
import com.alexstyl.contactstore.ContactColumn.WebAddresses
import com.alexstyl.contactstore.ContactPredicate.ContactLookup
import com.alexstyl.contactstore.ContactPredicate.MailLookup
import com.alexstyl.contactstore.ContactPredicate.NameLookup
import com.alexstyl.contactstore.ContactPredicate.PhoneLookup
import com.alexstyl.contactstore.utils.DateParser
import com.alexstyl.contactstore.utils.mapEachRow
import com.alexstyl.contactstore.utils.runQueryFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.InputStream

internal class ContactQueries(
    private val contentResolver: ContentResolver,
    private val resources: Resources,
    private val dateParser: DateParser,
    private val rawContactQueries: RawContactQueries,
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
            is ContactLookup -> lookupContact(predicate.contactId, displayNameStyle)
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
                    displayName = ContactsQuery.getDisplayName(it).orEmpty(),
                    isStarred = ContactsQuery.getIsStarred(it),
                    columns = emptyList()
                )
            }
        }
    }

    private fun lookupContact(
        contactId: Long,
        displayNameStyle: DisplayNameStyle
    ): Flow<List<PartialContact>> {
        return contentResolver.runQueryFlow(
            contentUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId),
            projection = ContactsQuery.projection(displayNameStyle),
            sortOrder = ContactsQuery.sortOrder(displayNameStyle)
        ).map { cursor ->
            cursor.mapEachRow {
                PartialContact(
                    contactId = ContactsQuery.getContactId(it),
                    lookupKey = ContactsQuery.getLookupKey(it),
                    displayName = ContactsQuery.getDisplayName(it).orEmpty(),
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
                    displayName = FilterQuery.getDisplayName(it).orEmpty(),
                    isStarred = FilterQuery.getIsStarred(it),
                    lookupKey = FilterQuery.getLookupKey(it),
                    columns = emptyList()
                )
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
                    displayName = ContactsQuery.getDisplayName(it).orEmpty(),
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
            val rawContacts = rawContactQueries.fetchRawContacts(contact)

            val contactId = contact.contactId
            var firstName = ""
            var middleName = ""
            var lastName = ""
            var prefix = ""
            var suffix = ""
            var fullNameStyle: Int = FullNameStyle.UNDEFINED
            var nickname = ""
            var phoneticFirstName = ""
            var phoneticMiddleName = ""
            var phoneticLastName = ""
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
            var organization = ""
            var jobTitle = ""
            var note: Note? = null
            val groupIds = mutableListOf<GroupMembership>()
            val customDataItems = mutableListOf<CustomDataItem>()

            rawContacts.forEach { rawContact ->
                val accountType = rawContact
                    .rawContactContentValues.getAsString(Entity.ACCOUNT_TYPE)
                val accountName = rawContact
                    .rawContactContentValues.getAsString(Entity.ACCOUNT_NAME)

                val account = accountType?.let {
                    InternetAccount(accountName, accountType)
                }
                rawContact.dataItems.forEach { item ->
                    val mimetype = item[Contacts.Data.MIMETYPE]
                    when {
                        columnsToFetch.contains(Nickname) && mimetype == NicknameColumns.CONTENT_ITEM_TYPE -> {
                            nickname = item.getAsString(NicknameColumns.NAME)
                        }
                        columnsToFetch.contains(GroupMemberships) && mimetype == GroupColumns.CONTENT_ITEM_TYPE -> {
                            val groupId = item.getAsLong(GroupColumns.GROUP_ROW_ID)
                            if (groupId != null) {
                                groupIds.add(GroupMembership(groupId))
                            }
                        }
                        columnsToFetch.contains(Names) && mimetype == NameColumns.CONTENT_ITEM_TYPE -> {
                            firstName = item.getAsString(NameColumns.GIVEN_NAME)
                            middleName = item.getAsString(NameColumns.MIDDLE_NAME)
                            lastName = item.getAsString(NameColumns.FAMILY_NAME)
                            prefix = item.getAsString(NameColumns.PREFIX)
                            suffix = item.getAsString(NameColumns.SUFFIX)
                            fullNameStyle =
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
                        columnsToFetch.contains(Image) && mimetype == PhotoColumns.CONTENT_ITEM_TYPE -> {
                            imageData = loadContactPhoto(contactId)
                        }
                        columnsToFetch.contains(Phones) && mimetype == PhoneColumns.CONTENT_ITEM_TYPE -> {
                            val phoneNumberString = item.getAsString(PhoneColumns.NUMBER)
                            val id = item.getAsLong(PhoneColumns._ID)
                            if (phoneNumberString.orEmpty().isNotBlank() && id != null) {
                                val value = PhoneNumber(phoneNumberString)
                                val phoneEntry =
                                    LabeledValue(value, phoneLabelFrom(item), id, account)
                                phones.add(phoneEntry)
                            }
                        }
                        columnsToFetch.contains(Mails) && mimetype == EmailColumns.CONTENT_ITEM_TYPE -> {
                            val mailAddressString = item.getAsString(EmailColumns.ADDRESS)
                            val id = item.getAsLong(EmailColumns._ID)
                            if (mailAddressString.orEmpty().isNotBlank() && id != null) {
                                val mailAddress = MailAddress(mailAddressString)
                                mails.add(
                                    LabeledValue(
                                        mailAddress,
                                        mailLabelFrom(item),
                                        id,
                                        account
                                    )
                                )
                            }
                        }
                        columnsToFetch.contains(WebAddresses) && mimetype == WebColumns.CONTENT_ITEM_TYPE -> {
                            val webAddressString = item.getAsString(WebColumns.URL)
                            val id = item.getAsLong(WebColumns._ID)
                            if (webAddressString.orEmpty().isNotBlank() && id != null) {
                                val mailAddress = WebAddress(Uri.parse(webAddressString))
                                webAddresses.add(
                                    LabeledValue(mailAddress, webLabelFrom(item), id, account)
                                )
                            }
                        }
                        columnsToFetch.contains(ContactColumn.Note) && mimetype == NoteColumns.CONTENT_ITEM_TYPE -> {
                            val noteString = item.getAsString(NoteColumns.NOTE)
                            if (noteString.orEmpty().isNotBlank()) {
                                note = Note(noteString)
                            }
                        }
                        columnsToFetch.contains(Events) && mimetype == EventColumns.CONTENT_ITEM_TYPE -> {
                            val parsedDate =
                                dateParser.parse(item.getAsString(EventColumns.START_DATE))
                            val id = item.getAsLong(EventColumns._ID)
                            if (parsedDate != null && id != null) {
                                val entry =
                                    LabeledValue(parsedDate, eventLabelFrom(item), id, account)
                                events.add(entry)
                            }
                        }
                        columnsToFetch.contains(SipAddresses) && mimetype == SipColumns.CONTENT_ITEM_TYPE -> {
                            val address = item.getAsString(SipColumns.SIP_ADDRESS)
                            val id = item.getAsLong(SipColumns._ID)
                            if (address.orEmpty().isNotBlank() && id != null) {
                                val value =
                                    LabeledValue(SipAddress(address), sipLabel(item), id, account)
                                sipAddresses.add(value)
                            }
                        }
                        columnsToFetch.contains(PostalAddresses) && mimetype == PostalColumns.CONTENT_ITEM_TYPE -> {
                            val formattedAddress = item.getAsString(PostalColumns.FORMATTED_ADDRESS)
                            val id = item.getAsLong(PostalColumns._ID)
                            if (formattedAddress.orEmpty().isNotBlank() && id != null) {
                                val street = item.getAsString(PostalColumns.STREET).orEmpty().trim()
                                val poBox = item.getAsString(PostalColumns.POBOX).orEmpty().trim()
                                val neighborhood =
                                    item.getAsString(PostalColumns.NEIGHBORHOOD).orEmpty().trim()
                                val city = item.getAsString(PostalColumns.CITY).orEmpty().trim()
                                val region = item.getAsString(PostalColumns.REGION).orEmpty().trim()
                                val postCode =
                                    item.getAsString(PostalColumns.POSTCODE).orEmpty().trim()
                                val country =
                                    item.getAsString(PostalColumns.COUNTRY).orEmpty().trim()
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
                                    value, postalAddressLabelFrom(item), id, account
                                )
                                postalAddresses.add(postalAddressEntry)
                            }
                        }
                        columnsToFetch.contains(Organization) && mimetype == OrganizationColumns.CONTENT_ITEM_TYPE -> {
                            organization = item.getAsString(OrganizationColumns.COMPANY)
                            jobTitle = item.getAsString(OrganizationColumns.TITLE)
                        }
                        columnsToFetch.contains(ImAddresses) && mimetype == ImColumns.CONTENT_ITEM_TYPE -> {
                            val imAddressString = item.getAsString(ImColumns.DATA)
                            val id = item.getAsLong(ImColumns._ID)
                            if (imAddressString.orEmpty().isNotBlank() && id != null) {
                                val protocol = getImProtocol(item)
                                val imAddress =
                                    ImAddress(raw = imAddressString, protocol = protocol)
                                val label = imLabelFrom(item)
                                imAddresses.add(
                                    LabeledValue(imAddress, label, id, account)
                                )
                            }
                        }
                        columnsToFetch.contains(Relations) && mimetype == RelationColumns.CONTENT_ITEM_TYPE -> {
                            val name = item.getAsString(RelationColumns.NAME)
                            val id = item.getAsLong(RelationColumns._ID)
                            if (name.orEmpty().isNotBlank() && id != null) {
                                val label = relationLabel(item)
                                relations.add(LabeledValue(Relation(name), label, id, account))
                            }
                        }
                        columnsToFetch.contains(CustomDataItems) -> {
                            val mimeType = linkedAccountMimeTypes[mimetype]
                            if (mimeType != null) {
                                val id = item.getAsLong(Contacts.Data._ID)
                                if (id != null) {
                                    val value = CustomDataItem(
                                        id = id,
                                        accountType = account!!.type,
                                        summary = item.getAsString(mimeType.summaryColumn),
                                        detail = item.getAsString(mimeType.detailColumn),
                                        icon = mimeType.icon,
                                        mimeType = mimeType.mimetype,
                                        account = account
                                    )
                                    customDataItems.add(value)
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
                customDataItems = customDataItems.toList(),
                imAddresses = imAddresses.toList(),
                relations = relations.toList()
            )
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

    private fun postalAddressLabelFrom(contentValues: ContentValues): Label {
        return when (
            contentValues.getAsInteger(PostalColumns.TYPE) ?: PostalColumns.TYPE_CUSTOM) {
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
        return when (cursor.getAsInteger(ImColumns.TYPE) ?: ImColumns.TYPE_OTHER) {
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