package com.alexstyl.contactstore

import android.provider.ContactsContract.CommonDataKinds.Email as EmailColumns
import android.provider.ContactsContract.CommonDataKinds.Event as EventColumns
import android.provider.ContactsContract.CommonDataKinds.GroupMembership as GroupsColumns
import android.provider.ContactsContract.CommonDataKinds.Im as ImColumns
import android.provider.ContactsContract.CommonDataKinds.Note as NoteColumns
import android.provider.ContactsContract.CommonDataKinds.Organization as OrganizationColumns
import android.provider.ContactsContract.CommonDataKinds.Phone as PhoneColumns
import android.provider.ContactsContract.CommonDataKinds.Photo as PhotoColumns
import android.provider.ContactsContract.CommonDataKinds.StructuredName as NameColumns
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal as PostalColumns
import android.provider.ContactsContract.CommonDataKinds.Website as WebAddressColumns
import android.provider.ContactsContract.CommonDataKinds.Relation as RelationColumns
import android.provider.ContactsContract.CommonDataKinds.SipAddress as SipColumns
import android.content.ContentProviderOperation
import android.content.ContentProviderOperation.newDelete
import android.content.ContentProviderOperation.newInsert
import android.content.ContentProviderOperation.newUpdate
import android.content.ContentResolver
import android.content.res.Resources
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Contactables
import android.provider.ContactsContract.Data
import android.provider.ContactsContract.RawContacts
import com.alexstyl.contactstore.ContactColumn.Events
import com.alexstyl.contactstore.ContactColumn.GroupMemberships
import com.alexstyl.contactstore.ContactColumn.ImAddresses
import com.alexstyl.contactstore.ContactColumn.Image
import com.alexstyl.contactstore.ContactColumn.Mails
import com.alexstyl.contactstore.ContactColumn.Names
import com.alexstyl.contactstore.ContactColumn.Note
import com.alexstyl.contactstore.ContactColumn.Organization
import com.alexstyl.contactstore.ContactColumn.Phones
import com.alexstyl.contactstore.ContactColumn.PostalAddresses
import com.alexstyl.contactstore.ContactColumn.Relations
import com.alexstyl.contactstore.ContactColumn.SipAddresses
import com.alexstyl.contactstore.ContactColumn.WebAddresses
import com.alexstyl.contactstore.utils.get
import com.alexstyl.contactstore.utils.runQuery
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

internal class ExistingContactOperationsFactory(
    private val contentResolver: ContentResolver,
    private val resources: Resources,
    private val contactQueries: ContactQueries
) {
    fun updateOperation(contact: MutableContact): List<ContentProviderOperation> {
        return runBlocking { updateSuspend(contact) }
    }

    private suspend fun updateSuspend(contact: MutableContact): List<ContentProviderOperation> {
        val existingContact = contactQueries.queryContacts(
            predicate = ContactPredicate.ContactLookup(inContactIds = listOf(contact.contactId)),
            columnsToFetch = contact.columns,
            displayNameStyle = DisplayNameStyle.Primary
        ).first().firstOrNull() ?: return emptyList()
        return updatesNames(contact) +
                replacePhoto(contact) +
                replaceOrganization(contact) +
                replaceStar(contact) +
                noteOperationsFor(contact) +
                updatePhoneNumbers(newContact = contact, oldContact = existingContact) +
                updateMails(newContact = contact, oldContact = existingContact) +
                updateEvents(newContact = contact, oldContact = existingContact) +
                updateGroupMembership(newContact = contact, oldContact = existingContact) +
                updatePostalAddresses(newContact = contact, oldContact = existingContact) +
                updateImAddresses(newContact = contact, oldContact = existingContact) +
                updateSipAddresses(newContact = contact, oldContact = existingContact) +
                updateRelations(newContact = contact, oldContact = existingContact) +
                updateWebAddresses(newContact = contact, oldContact = existingContact)
    }

    private fun updateGroupMembership(
        newContact: MutableContact,
        oldContact: Contact
    ): List<ContentProviderOperation> {
        if (newContact.containsColumn(GroupMemberships).not()) return emptyList()

        val forContactId = newContact.contactId
        val added = valuesAdded(old = oldContact.groups, new = newContact.groups)
        val deleted = valuesDeleted(old = oldContact.groups, new = newContact.groups)

        val rawContactId = findRawContactId(newContact.contactId)
        return added.map { value ->
            newInsert(Data.CONTENT_URI)
                .withValue(Data.MIMETYPE, GroupsColumns.CONTENT_ITEM_TYPE)
                .withValue(Data.RAW_CONTACT_ID, rawContactId)
                .withValue(GroupsColumns.GROUP_ROW_ID, value.groupId)
                .build()
        } + deleted.map { value ->
            newDelete(Data.CONTENT_URI)
                .withSelection(
                    "${GroupsColumns.CONTACT_ID} = $forContactId" +
                            " AND ${GroupsColumns.MIMETYPE} = ?" +
                            " AND ${GroupsColumns.GROUP_ROW_ID} = ${value.groupId}",
                    arrayOf(GroupsColumns.CONTENT_ITEM_TYPE)
                )
                .build()
        }
    }

    private fun replaceOrganization(contact: MutableContact): List<ContentProviderOperation> {
        if (contact.containsColumn(Organization).not()) return emptyList()
        val contactId = requireNotNull(contact.contactId)
        val rawId = findRawContactId(contactId)
        val deletePrevious = newDelete(Data.CONTENT_URI)
            .withSelection(
                "${OrganizationColumns.CONTACT_ID} = $contactId AND ${OrganizationColumns.MIMETYPE} = ?",
                arrayOf(OrganizationColumns.CONTENT_ITEM_TYPE)
            )
            .build()
        val addNew = if (contact.jobTitle.isNullOrBlank().not() ||
            contact.organization.isNullOrBlank().not()
        ) {
            newInsert(Data.CONTENT_URI)
                .withValue(OrganizationColumns.RAW_CONTACT_ID, rawId)
                .withValue(OrganizationColumns.MIMETYPE, OrganizationColumns.CONTENT_ITEM_TYPE)
                .withValue(OrganizationColumns.COMPANY, contact.organization)
                .withValue(OrganizationColumns.TITLE, contact.jobTitle)
                .build()
        } else {
            null
        }
        return listOfNotNull(deletePrevious, addNew)
    }

    private fun noteOperationsFor(contact: MutableContact): List<ContentProviderOperation> {
        if (contact.containsColumn(Note).not()) return emptyList()
        val rawContactId = findRawContactId(contact.contactId)
        val deleteOldNotes = newDelete(Data.CONTENT_URI)
            .withSelection(
                "${NoteColumns.RAW_CONTACT_ID} == $rawContactId" +
                        " AND ${NoteColumns.MIMETYPE} = ?",
                arrayOf(NoteColumns.CONTENT_ITEM_TYPE)
            )
            .build()
        val addNewNote = contact.note?.let {
            newInsert(Data.CONTENT_URI)
                .withValue(Data.RAW_CONTACT_ID, rawContactId)
                .withValue(Data.MIMETYPE, NoteColumns.CONTENT_ITEM_TYPE)
                .withValue(NoteColumns.NOTE, it.raw)
                .build()
        }
        return listOfNotNull(deleteOldNotes, addNewNote)
    }

    private fun replaceStar(contact: MutableContact): List<ContentProviderOperation> {
        return listOf(
            newUpdate(ContactsContract.Contacts.CONTENT_URI)
                .withSelection("${ContactsContract.Contacts._ID} = ${contact.contactId}", null)
                .withValue(
                    ContactsContract.Contacts.STARRED, if (contact.isStarred) {
                        "1"
                    } else {
                        "0"
                    }
                )
                .build()
        )
    }

    private fun updatesNames(contact: MutableContact): List<ContentProviderOperation> {
        if (contact.containsColumn(Names).not()) return emptyList()
        val contactId = requireNotNull(contact.contactId)
        val rawContactId = findRawContactId(contactId)
        return listOf(
            newUpdate(Data.CONTENT_URI)
                .withSelection(
                    "${NameColumns.CONTACT_ID} = $contactId AND ${NameColumns.MIMETYPE} = ?",
                    arrayOf(NameColumns.CONTENT_ITEM_TYPE)
                )
                .withValue(NameColumns.RAW_CONTACT_ID, rawContactId)
                .withValue(NameColumns.MIMETYPE, NameColumns.CONTENT_ITEM_TYPE)
                .withValue(NameColumns.GIVEN_NAME, contact.firstName)
                .withValue(NameColumns.FAMILY_NAME, contact.lastName)
                .withValue(NameColumns.MIDDLE_NAME, contact.middleName)
                .withValue(NameColumns.PREFIX, contact.prefix)
                .withValue(NameColumns.SUFFIX, contact.suffix)
                .build()
        )
    }

    private fun replacePhoto(contact: MutableContact): List<ContentProviderOperation> {
        if (contact.containsColumn(Image).not()) return emptyList()
        val contactId = requireNotNull(contact.contactId)
        val rawId = findRawContactId(contactId)
        val imageData = contact.imageData
        val deletePrevious = newDelete(Data.CONTENT_URI)
            .withSelection(
                "${PhotoColumns.CONTACT_ID} = $contactId AND ${PhotoColumns.MIMETYPE} = ?",
                arrayOf(PhotoColumns.CONTENT_ITEM_TYPE)
            )
            .build()
        val addNew = imageData?.let {
            newInsert(Data.CONTENT_URI)
                .withValue(PhotoColumns.RAW_CONTACT_ID, rawId)
                .withValue(PhotoColumns.MIMETYPE, PhotoColumns.CONTENT_ITEM_TYPE)
                .withValue(PhotoColumns.PHOTO, imageData.raw)
                .build()
        }
        return listOfNotNull(deletePrevious, addNew)
    }

    private fun updatePhoneNumbers(
        newContact: Contact,
        oldContact: Contact
    ): List<ContentProviderOperation> {
        if (newContact.containsColumn(Phones).not()) return emptyList()

        return buildOperations(
            forContactId = newContact.contactId,
            mimeType = PhoneColumns.CONTENT_ITEM_TYPE,
            oldValues = oldContact.phones,
            newValues = newContact.phones
        ) { value ->
            withValue(PhoneColumns.NUMBER, value.value.raw)
                .withPhoneLabel(value.label)
        }
    }

    private fun newUpdate(
        forContact: Long,
        forMimeType: String,
        forId: Long,
        operationBuilder: (ContentProviderOperation.Builder) -> Unit
    ): ContentProviderOperation {
        return newUpdate(Data.CONTENT_URI)
            .withSelection(
                "${PhoneColumns.CONTACT_ID} = $forContact" +
                        " AND ${PhoneColumns.MIMETYPE} = ?" +
                        " AND ${PhoneColumns._ID} = $forId",
                arrayOf(forMimeType)
            )
            .apply(operationBuilder)
            .build()
    }

    private fun updateMails(
        newContact: MutableContact,
        oldContact: Contact
    ): List<ContentProviderOperation> {
        if (newContact.containsColumn(Mails).not()) return emptyList()

        return buildOperations(
            forContactId = newContact.contactId,
            mimeType = EmailColumns.CONTENT_ITEM_TYPE,
            oldValues = oldContact.mails,
            newValues = newContact.mails,
        ) { value ->
            withValue(EmailColumns.ADDRESS, value.value.raw)
                .withMailLabel(value.label)
        }
    }

    private fun updateEvents(
        newContact: MutableContact,
        oldContact: Contact
    ): List<ContentProviderOperation> {
        if (newContact.containsColumn(Events).not()) return emptyList()
        return buildOperations(
            forContactId = newContact.contactId,
            mimeType = EventColumns.CONTENT_ITEM_TYPE,
            oldValues = oldContact.events,
            newValues = newContact.events
        ) { labeledValue ->
            withValue(EventColumns.START_DATE, sqlString(forDate = labeledValue.value))
                .withEventLabel(labeledValue.label)
        }
    }

    private fun updatePostalAddresses(
        newContact: MutableContact,
        oldContact: Contact
    ): List<ContentProviderOperation> {
        if (newContact.containsColumn(PostalAddresses).not()) {
            return emptyList()
        }
        return buildOperations(
            forContactId = newContact.contactId,
            oldValues = oldContact.postalAddresses,
            newValues = newContact.postalAddresses,
            mimeType = PostalColumns.CONTENT_ITEM_TYPE,
        ) { labeledValue ->
            withValue(PostalColumns.CITY, labeledValue.value.city)
                .withValue(PostalColumns.COUNTRY, labeledValue.value.country)
                .withValue(PostalColumns.NEIGHBORHOOD, labeledValue.value.neighborhood)
                .withValue(PostalColumns.POBOX, labeledValue.value.poBox)
                .withValue(PostalColumns.POSTCODE, labeledValue.value.postCode)
                .withValue(PostalColumns.REGION, labeledValue.value.region)
                .withValue(PostalColumns.STREET, labeledValue.value.street)
                .withPostalAddressLabel(labeledValue.label)
        }
    }

    private fun updateImAddresses(
        newContact: MutableContact,
        oldContact: Contact
    ): List<ContentProviderOperation> {
        if (newContact.containsColumn(ImAddresses).not()) {
            return emptyList()
        }
        return buildOperations(
            forContactId = newContact.contactId,
            oldValues = oldContact.imAddresses,
            newValues = newContact.imAddresses,
            mimeType = ImColumns.CONTENT_ITEM_TYPE,
        ) { labeledValue ->
            withValue(ImColumns.DATA, labeledValue.value.raw)
                .withValue(ImColumns.PROTOCOL, ImColumns.PROTOCOL_CUSTOM)
                .withValue(ImColumns.CUSTOM_PROTOCOL, labeledValue.value.protocol)
                .withImLabel(labeledValue.label)
        }
    }
    
    private fun updateSipAddresses(
        newContact: MutableContact,
        oldContact: Contact
    ): List<ContentProviderOperation> {
        if (newContact.containsColumn(SipAddresses).not()) {
            return emptyList()
        }
        return buildOperations(
            forContactId = newContact.contactId,
            oldValues = oldContact.sipAddresses,
            newValues = newContact.sipAddresses,
            mimeType = SipColumns.CONTENT_ITEM_TYPE,
        ) { labeledValue ->
            withValue(SipColumns.SIP_ADDRESS, labeledValue.value.raw)
                .withSipLabel(labeledValue.label)
        }
    }

    private fun updateRelations(
        newContact: MutableContact,
        oldContact: Contact
    ): List<ContentProviderOperation> {
        if (newContact.containsColumn(Relations).not()) {
            return emptyList()
        }
        return buildOperations(
            forContactId = newContact.contactId,
            oldValues = oldContact.relations,
            newValues = newContact.relations,
            mimeType = RelationColumns.CONTENT_ITEM_TYPE,
        ) { labeledValue ->
            withValue(RelationColumns.NAME, labeledValue.value.name)
                .withRelationLabel(labeledValue.label)
        }
    }

    private fun <T : Any> buildOperations(
        forContactId: Long,
        mimeType: String,
        oldValues: List<LabeledValue<T>>,
        newValues: List<LabeledValue<T>>,
        operationBuilder: ContentProviderOperation.Builder.(LabeledValue<T>) -> Unit
    ): List<ContentProviderOperation> {
        val added = valuesAdded(old = oldValues, new = newValues)
        val updated = valuesUpdated(old = oldValues, new = newValues)
        val deleted = valuesDeleted(old = oldValues, new = newValues)

        val rawContactId = findRawContactId(forContactId)
        return added.map { value ->
            newInsert(Data.CONTENT_URI)
                .withValue(Data.MIMETYPE, mimeType)
                .withValue(Data.RAW_CONTACT_ID, rawContactId)
                .apply { this.operationBuilder(value) }
                .build()
        } + updated.map { value ->
            newUpdate(
                forContact = forContactId,
                forMimeType = mimeType,
                forId = value.requireId()
            ) { builder ->
                builder.withValue(Data.RAW_CONTACT_ID, rawContactId)
                    .apply { this.operationBuilder(value) }
            }
        } + deleted.map { value ->
            newDelete(Data.CONTENT_URI)
                .withSelection(
                    "${Data.CONTACT_ID} = $forContactId" +
                            " AND ${Data.MIMETYPE} = ?" +
                            " AND ${Data._ID} = ${value.requireId()}",
                    arrayOf(mimeType)
                )
                .build()
        }
    }

    private fun updateWebAddresses(
        newContact: MutableContact,
        oldContact: Contact
    ): List<ContentProviderOperation> {
        if (newContact.containsColumn(WebAddresses).not()) return emptyList()
        return buildOperations(
            forContactId = newContact.contactId,
            mimeType = WebAddressColumns.CONTENT_ITEM_TYPE,
            oldValues = oldContact.webAddresses,
            newValues = newContact.webAddresses
        ) { labeledValue ->
            withValue(WebAddressColumns.URL, labeledValue.value.raw.toString())
                .withWebAddressLabel(labeledValue.label)
        }
    }

    private fun findRawContactId(contactID: Long): Long {
        return contentResolver.runQuery(
            contentUri = RawContacts.CONTENT_URI,
            projection = arrayOf(RawContacts._ID),
            selection = "${RawContacts.CONTACT_ID} = $contactID"
        )?.use {
            it.moveToFirst()
            it[RawContacts._ID].toLongOrNull()
        } ?: -1L
    }

    fun deleteContactOperation(contactWithContactId: Long): List<ContentProviderOperation> {
        return listOf(deleteContact(contactWithContactId))
    }

    private fun deleteContact(contactId: Long): ContentProviderOperation {
        val rawContactId = findRawContactId(contactId)
        return newDelete(RawContacts.CONTENT_URI)
            .withSelection("${RawContacts._ID} = $rawContactId", null)
            .build()
    }

    private fun ContentProviderOperation.Builder.withPhoneLabel(
        label: Label
    ): ContentProviderOperation.Builder {
        return when (label) {
            Label.PhoneNumberMobile -> withValue(Contactables.TYPE, PhoneColumns.TYPE_MOBILE)
            Label.LocationHome -> withValue(Contactables.TYPE, PhoneColumns.TYPE_HOME)
            Label.LocationWork -> withValue(Contactables.TYPE, PhoneColumns.TYPE_WORK)
            Label.PhoneNumberPager -> withValue(Contactables.TYPE, PhoneColumns.TYPE_PAGER)
            Label.PhoneNumberCar -> withValue(Contactables.TYPE, PhoneColumns.TYPE_CAR)
            Label.PhoneNumberFaxWork -> withValue(Contactables.TYPE, PhoneColumns.TYPE_FAX_WORK)
            Label.PhoneNumberFaxHome -> withValue(Contactables.TYPE, PhoneColumns.TYPE_FAX_HOME)
            Label.PhoneNumberCallback -> withValue(Contactables.TYPE, PhoneColumns.TYPE_CALLBACK)
            Label.PhoneNumberCompanyMain -> withValue(
                Contactables.TYPE,
                PhoneColumns.TYPE_COMPANY_MAIN
            )
            Label.PhoneNumberIsdn -> withValue(Contactables.TYPE, PhoneColumns.TYPE_ISDN)
            Label.Main -> withValue(Contactables.TYPE, PhoneColumns.TYPE_MAIN)
            Label.PhoneNumberOtherFax -> withValue(Contactables.TYPE, PhoneColumns.TYPE_OTHER_FAX)
            Label.PhoneNumberRadio -> withValue(Contactables.TYPE, PhoneColumns.TYPE_RADIO)
            Label.PhoneNumberTelex -> withValue(Contactables.TYPE, PhoneColumns.TYPE_TELEX)
            Label.PhoneNumberTtyTdd -> withValue(Contactables.TYPE, PhoneColumns.TYPE_TTY_TDD)
            Label.PhoneNumberWorkPager -> withValue(Contactables.TYPE, PhoneColumns.TYPE_WORK_PAGER)
            Label.PhoneNumberWorkMobile -> withValue(
                Contactables.TYPE,
                PhoneColumns.TYPE_WORK_MOBILE
            )
            Label.PhoneNumberAssistant -> withValue(Contactables.TYPE, PhoneColumns.TYPE_ASSISTANT)
            Label.PhoneNumberMms -> withValue(Contactables.TYPE, PhoneColumns.TYPE_MMS)
            Label.Other -> withValue(Contactables.TYPE, PhoneColumns.TYPE_OTHER)
            Label.DateBirthday -> {
                val typeResource = EventColumns.getTypeResource(EventColumns.TYPE_BIRTHDAY)
                withValue(Contactables.TYPE, Contactables.TYPE_CUSTOM)
                    .withValue(Contactables.LABEL, resources.getString(typeResource))
            }
            Label.DateAnniversary -> {
                val typeResource = EventColumns.getTypeResource(EventColumns.TYPE_ANNIVERSARY)
                withValue(Contactables.TYPE, Contactables.TYPE_CUSTOM)
                    .withValue(Contactables.LABEL, resources.getString(typeResource))
            }
            is Label.Custom -> {
                withValue(Contactables.TYPE, Contactables.TYPE_CUSTOM)
                    .withValue(Contactables.LABEL, label.label)
            }
            else -> error("Unsupported phone label $label")
        }
    }

    private fun ContentProviderOperation.Builder.withMailLabel(
        label: Label
    ): ContentProviderOperation.Builder {
        return when (label) {
            Label.LocationHome -> withValue(Contactables.TYPE, EmailColumns.TYPE_HOME)
            Label.LocationWork -> withValue(Contactables.TYPE, EmailColumns.TYPE_WORK)
            Label.Other -> withValue(Contactables.TYPE, EmailColumns.TYPE_OTHER)
            Label.PhoneNumberMobile -> withValue(Contactables.TYPE, EmailColumns.TYPE_MOBILE)
            is Label.Custom -> {
                withValue(Contactables.TYPE, Contactables.TYPE_CUSTOM)
                    .withValue(Contactables.LABEL, label.label)
            }
            else -> error("Unsupported Mail Label $label")
        }
    }

    private fun ContentProviderOperation.Builder.withPostalAddressLabel(
        label: Label
    ): ContentProviderOperation.Builder {
        return when (label) {
            Label.LocationHome -> withValue(Contactables.TYPE, PostalColumns.TYPE_HOME)
            Label.LocationWork -> withValue(Contactables.TYPE, PostalColumns.TYPE_WORK)
            Label.Other -> withValue(Contactables.TYPE, PostalColumns.TYPE_OTHER)
            is Label.Custom -> {
                withValue(Contactables.TYPE, Contactables.TYPE_CUSTOM)
                    .withValue(Contactables.LABEL, label.label)
            }
            else -> error("Unsupported Postal Label $label")
        }
    }

    private fun ContentProviderOperation.Builder.withImLabel(
        label: Label
    ): ContentProviderOperation.Builder {
        return when (label) {
            Label.LocationHome -> withValue(Contactables.TYPE, ImColumns.TYPE_HOME)
            Label.LocationWork -> withValue(Contactables.TYPE, ImColumns.TYPE_WORK)
            Label.Other -> withValue(Contactables.TYPE, ImColumns.TYPE_OTHER)
            is Label.Custom -> {
                withValue(Contactables.TYPE, Contactables.TYPE_CUSTOM)
                    .withValue(Contactables.LABEL, label.label)
            }
            else -> error("Unsupported Im Label $label")
        }
    }

    private fun ContentProviderOperation.Builder.withWebAddressLabel(
        label: Label
    ): ContentProviderOperation.Builder {
        return when (label) {
            Label.WebsiteProfile -> withValue(Contactables.TYPE, WebAddressColumns.TYPE_PROFILE)
            Label.LocationWork -> withValue(Contactables.TYPE, WebAddressColumns.TYPE_WORK)
            Label.WebsiteHomePage -> withValue(Contactables.TYPE, WebAddressColumns.TYPE_HOMEPAGE)
            Label.WebsiteFtp -> withValue(Contactables.TYPE, WebAddressColumns.TYPE_FTP)
            Label.WebsiteBlog -> withValue(Contactables.TYPE, WebAddressColumns.TYPE_BLOG)
            Label.Other -> withValue(Contactables.TYPE, WebAddressColumns.TYPE_OTHER)
            is Label.Custom -> {
                withValue(Contactables.TYPE, Contactables.TYPE_CUSTOM)
                    .withValue(Contactables.LABEL, label.label)
            }
            else -> error("Unsupported Web Label $label")
        }
    }

    private fun ContentProviderOperation.Builder.withEventLabel(
        label: Label
    ): ContentProviderOperation.Builder {
        return when (label) {
            Label.DateAnniversary -> withValue(Contactables.TYPE, EventColumns.TYPE_ANNIVERSARY)
            Label.DateBirthday -> withValue(Contactables.TYPE, EventColumns.TYPE_BIRTHDAY)
            Label.Other -> withValue(Contactables.TYPE, EventColumns.TYPE_OTHER)
            is Label.Custom -> {
                withValue(Contactables.TYPE, Contactables.TYPE_CUSTOM)
                    .withValue(Contactables.LABEL, label.label)
            }
            else -> error("Unsupported Event Label $label")
        }
    }
}
