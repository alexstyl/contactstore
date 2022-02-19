package com.alexstyl.contactstore

import android.provider.ContactsContract.CommonDataKinds.Email as EmailColumns
import android.provider.ContactsContract.CommonDataKinds.Event as EventColumns
import android.provider.ContactsContract.CommonDataKinds.GroupMembership as GroupColumns
import android.provider.ContactsContract.CommonDataKinds.Im as ImColumns
import android.provider.ContactsContract.CommonDataKinds.Note as NoteColumns
import android.provider.ContactsContract.CommonDataKinds.Organization as OrganizationColumns
import android.provider.ContactsContract.CommonDataKinds.Phone as PhoneColumns
import android.provider.ContactsContract.CommonDataKinds.Photo as PhotoColumns
import android.provider.ContactsContract.CommonDataKinds.Relation as RelationColumns
import android.provider.ContactsContract.CommonDataKinds.SipAddress as SipColumns
import android.provider.ContactsContract.CommonDataKinds.StructuredName as NameColumns
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal as PostalColumns
import android.provider.ContactsContract.CommonDataKinds.Website as WebsiteColumns
import android.content.ContentProviderOperation
import android.content.ContentProviderOperation.newInsert
import android.content.res.Resources
import android.os.Build
import android.provider.ContactsContract.Data
import android.provider.ContactsContract.FullNameStyle
import android.provider.ContactsContract.PhoneticNameStyle
import android.provider.ContactsContract.RawContacts


internal class NewContactOperationsFactory(
    private val resources: Resources
) {
    fun addContactsOperation(
        account: InternetAccount?, contact: MutableContact
    ): List<ContentProviderOperation> {
        return mutableListOf<ContentProviderOperation?>().apply {
            with(contact) {
                add(insertNewRawAccountOperation(account, contact))
                add(insertNamesOperation(contact))
                imageData?.run { add(insertPhotoOperation(this)) }

                contact.phones.forEach { add(insertPhoneOperation(it)) }
                contact.mails.forEach { add(insertMailOperation(it)) }
                contact.webAddresses.forEach { add(insertWebOperation(it)) }
                contact.events.forEach { add(insertEventsOperation(it)) }
                contact.postalAddresses.forEach { add(insertPostalOperation(it)) }
                contact.note?.run { add(insertNoteOperation(this)) }
                contact.imAddresses.forEach { add(insertImOperation(it)) }
                contact.sipAddresses.forEach { add(insertSipOperation(it)) }
                contact.relations.forEach { add(insertRelationOperation(it)) }
                contact.groups.forEach { add(insertGroupOperation(it)) }

                if (hasOrganizationDetails(contact)) {
                    add(insertOrganization(contact))
                }
            }
        }
            .filterNotNull()
            .toList()
    }

    private fun hasOrganizationDetails(contact: MutableContact): Boolean {
        return (contact.organization.isNotBlank() || contact.jobTitle.isNotBlank())
    }

    private fun insertOrganization(contact: Contact): ContentProviderOperation {
        return newInsert(Data.CONTENT_URI)
            .withValueBackReference(Data.RAW_CONTACT_ID, NEW_CONTACT_INDEX)
            .withValue(Data.MIMETYPE, OrganizationColumns.CONTENT_ITEM_TYPE)
            .withValue(OrganizationColumns.TITLE, contact.jobTitle)
            .withValue(OrganizationColumns.COMPANY, contact.organization)
            .build()
    }

    private fun insertWebOperation(labeledValue: LabeledValue<WebAddress>): ContentProviderOperation {
        return newInsert(Data.CONTENT_URI)
            .withValueBackReference(Data.RAW_CONTACT_ID, NEW_CONTACT_INDEX)
            .withValue(Data.MIMETYPE, WebsiteColumns.CONTENT_ITEM_TYPE)
            .withValue(WebsiteColumns.URL, labeledValue.value.raw.toString())
            .withWebAddressLabel(labeledValue.label, resources)
            .build()
    }

    private fun insertImOperation(labeledValue: LabeledValue<ImAddress>): ContentProviderOperation {
        return newInsert(Data.CONTENT_URI)
            .withValueBackReference(Data.RAW_CONTACT_ID, NEW_CONTACT_INDEX)
            .withValue(Data.MIMETYPE, ImColumns.CONTENT_ITEM_TYPE)
            .withValue(ImColumns.DATA, labeledValue.value.raw)
            .withValue(ImColumns.CUSTOM_PROTOCOL, labeledValue.value.protocol)
            .withImLabel(labeledValue.label, resources)
            .build()
    }

    private fun insertSipOperation(labeledValue: LabeledValue<SipAddress>): ContentProviderOperation {
        return newInsert(Data.CONTENT_URI)
            .withValueBackReference(Data.RAW_CONTACT_ID, NEW_CONTACT_INDEX)
            .withValue(Data.MIMETYPE, SipColumns.CONTENT_ITEM_TYPE)
            .withValue(SipColumns.SIP_ADDRESS, labeledValue.value.raw)
            .withSipLabel(labeledValue.label, resources)
            .build()
    }

    private fun insertNoteOperation(note: Note): ContentProviderOperation {
        return newInsert(Data.CONTENT_URI)
            .withValueBackReference(Data.RAW_CONTACT_ID, NEW_CONTACT_INDEX)
            .withValue(Data.MIMETYPE, NoteColumns.CONTENT_ITEM_TYPE)
            .withValue(NoteColumns.NOTE, note.raw)
            .build()
    }

    private fun insertPhotoOperation(imageData: ImageData): ContentProviderOperation {
        return newInsert(Data.CONTENT_URI)
            .withValueBackReference(Data.RAW_CONTACT_ID, NEW_CONTACT_INDEX)
            .withValue(Data.MIMETYPE, PhotoColumns.CONTENT_ITEM_TYPE)
            .withValue(PhotoColumns.PHOTO, imageData.raw)
            .build()
    }

    private fun insertPhoneOperation(labeledValue: LabeledValue<PhoneNumber>): ContentProviderOperation {
        return newInsert(Data.CONTENT_URI)
            .withValueBackReference(Data.RAW_CONTACT_ID, NEW_CONTACT_INDEX)
            .withValue(Data.MIMETYPE, PhoneColumns.CONTENT_ITEM_TYPE)
            .withValue(PhoneColumns.NUMBER, labeledValue.value.raw)
            .withPhoneLabel(labeledValue.label, resources)
            .build()
    }

    private fun insertMailOperation(labeledValue: LabeledValue<MailAddress>): ContentProviderOperation {
        return newInsert(Data.CONTENT_URI)
            .withValueBackReference(Data.RAW_CONTACT_ID, NEW_CONTACT_INDEX)
            .withValue(Data.MIMETYPE, EmailColumns.CONTENT_ITEM_TYPE)
            .withValue(EmailColumns.ADDRESS, labeledValue.value.raw)
            .withMailLabel(labeledValue.label, resources)
            .build()
    }

    private fun insertEventsOperation(labeledValue: LabeledValue<EventDate>): ContentProviderOperation {
        return newInsert(Data.CONTENT_URI)
            .withValueBackReference(Data.RAW_CONTACT_ID, NEW_CONTACT_INDEX)
            .withValue(Data.MIMETYPE, EventColumns.CONTENT_ITEM_TYPE)
            .withValue(EmailColumns.ADDRESS, sqlString(labeledValue.value))
            .withEventLabel(labeledValue.label, resources)
            .build()
    }

    private fun insertPostalOperation(labeledValue: LabeledValue<PostalAddress>): ContentProviderOperation {
        return newInsert(Data.CONTENT_URI)
            .withValueBackReference(Data.RAW_CONTACT_ID, NEW_CONTACT_INDEX)
            .withValue(Data.MIMETYPE, PostalColumns.CONTENT_ITEM_TYPE)
            .withValue(PostalColumns.CITY, labeledValue.value.city)
            .withValue(PostalColumns.COUNTRY, labeledValue.value.country)
            .withValue(PostalColumns.NEIGHBORHOOD, labeledValue.value.neighborhood)
            .withValue(PostalColumns.POBOX, labeledValue.value.poBox)
            .withValue(PostalColumns.POSTCODE, labeledValue.value.postCode)
            .withValue(PostalColumns.REGION, labeledValue.value.region)
            .withValue(PostalColumns.STREET, labeledValue.value.street)
            .withPostalAddressLabel(labeledValue.label, resources)
            .build()
    }

    private fun insertRelationOperation(labeledValue: LabeledValue<Relation>): ContentProviderOperation {
        return newInsert(Data.CONTENT_URI)
            .withValueBackReference(Data.RAW_CONTACT_ID, NEW_CONTACT_INDEX)
            .withValue(Data.MIMETYPE, RelationColumns.CONTENT_ITEM_TYPE)
            .withValue(RelationColumns.NAME, labeledValue.value.name)
            .withRelationLabel(labeledValue.label, resources)
            .build()
    }

    private fun insertGroupOperation(group: GroupMembership): ContentProviderOperation {
        return newInsert(Data.CONTENT_URI)
            .withValueBackReference(Data.RAW_CONTACT_ID, NEW_CONTACT_INDEX)
            .withValue(Data.MIMETYPE, GroupColumns.CONTENT_ITEM_TYPE)
            .withValue(GroupColumns.GROUP_ROW_ID, group.groupId)
            .build()
    }

    private fun insertNewRawAccountOperation(
        account: InternetAccount?, contact: MutableContact
    ): ContentProviderOperation {
        return newInsert(RawContacts.CONTENT_URI)
            .withValue(RawContacts.ACCOUNT_TYPE, account?.type)
            .withValue(RawContacts.ACCOUNT_NAME, account?.name)
            .withValue(Data.STARRED, boolToString(contact.isStarred))
            .build()
    }

    private fun boolToString(bool: Boolean): String {
        return if (bool) {
            "1"
        } else {
            "0"
        }
    }

    private fun insertNamesOperation(contact: Contact): ContentProviderOperation {
        return newInsert(Data.CONTENT_URI)
            .withValueBackReference(Data.RAW_CONTACT_ID, NEW_CONTACT_INDEX)
            .withValue(Data.MIMETYPE, NameColumns.CONTENT_ITEM_TYPE)
            .withValue(NameColumns.GIVEN_NAME, contact.firstName)
            .withValue(NameColumns.FAMILY_NAME, contact.lastName)
            .withValue(NameColumns.MIDDLE_NAME, contact.middleName)
            .withValue(NameColumns.SUFFIX, contact.suffix)
            .withValue(NameColumns.PREFIX, contact.prefix)
            .let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    it.withValue(NameColumns.FULL_NAME_STYLE, FullNameStyle.UNDEFINED)
                } else {
                    it
                }
            }
            .withValue(NameColumns.PHONETIC_GIVEN_NAME, contact.phoneticFirstName)
            .withValue(NameColumns.PHONETIC_FAMILY_NAME, contact.phoneticLastName)
            .withValue(NameColumns.PHONETIC_MIDDLE_NAME, contact.phoneticMiddleName)
            .withValue(NameColumns.PHONETIC_NAME_STYLE, PhoneticNameStyle.UNDEFINED)
            .build()
    }

    private companion object {
        const val NEW_CONTACT_INDEX = 0
    }
}
