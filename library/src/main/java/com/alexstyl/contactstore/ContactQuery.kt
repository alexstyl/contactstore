package com.alexstyl.contactstore

import android.os.Build
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds
import android.provider.ContactsContract.RawContacts

/*
 * Code converted from Java and slightly modified from the contacts AOSP app.
 *
 * See AOSP's ContactLoader.ContactQuery
 */
internal object ContactQuery {
    private val COLUMNS_INTERNAL = arrayOf(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ContactsContract.Contacts.NAME_RAW_CONTACT_ID
        } else {
            ContactsContract.Contacts._ID
        },
        ContactsContract.Contacts.DISPLAY_NAME_SOURCE,
        ContactsContract.Contacts.LOOKUP_KEY,
        ContactsContract.Contacts.DISPLAY_NAME,
        ContactsContract.Contacts.DISPLAY_NAME_ALTERNATIVE,
        ContactsContract.Contacts.PHONETIC_NAME,
        ContactsContract.Contacts.PHOTO_ID,
        ContactsContract.Contacts.STARRED,
        ContactsContract.Contacts.CONTACT_PRESENCE,
        ContactsContract.Contacts.CONTACT_STATUS,
        ContactsContract.Contacts.CONTACT_STATUS_TIMESTAMP,
        ContactsContract.Contacts.CONTACT_STATUS_RES_PACKAGE,
        ContactsContract.Contacts.CONTACT_STATUS_LABEL,
        ContactsContract.Contacts.Entity.CONTACT_ID,
        ContactsContract.Contacts.Entity.RAW_CONTACT_ID,
        RawContacts.ACCOUNT_NAME,
        RawContacts.ACCOUNT_TYPE,
        RawContacts.DATA_SET,
        RawContacts.DIRTY,
        RawContacts.VERSION,
        RawContacts.SOURCE_ID,
        RawContacts.SYNC1,
        RawContacts.SYNC2,
        RawContacts.SYNC3,
        RawContacts.SYNC4,
        RawContacts.DELETED,
        ContactsContract.Contacts.Entity.DATA_ID,
        ContactsContract.Data.DATA1,
        ContactsContract.Data.DATA2,
        ContactsContract.Data.DATA3,
        ContactsContract.Data.DATA4,
        ContactsContract.Data.DATA5,
        ContactsContract.Data.DATA6,
        ContactsContract.Data.DATA7,
        ContactsContract.Data.DATA8,
        ContactsContract.Data.DATA9,
        ContactsContract.Data.DATA10,
        ContactsContract.Data.DATA11,
        ContactsContract.Data.DATA12,
        ContactsContract.Data.DATA13,
        ContactsContract.Data.DATA14,
        ContactsContract.Data.DATA15,
        ContactsContract.Data.SYNC1,
        ContactsContract.Data.SYNC2,
        ContactsContract.Data.SYNC3,
        ContactsContract.Data.SYNC4,
        ContactsContract.Data.DATA_VERSION,
        ContactsContract.Data.IS_PRIMARY,
        ContactsContract.Data.IS_SUPER_PRIMARY,
        ContactsContract.Data.MIMETYPE,
        CommonDataKinds.GroupMembership.GROUP_SOURCE_ID,
        ContactsContract.Data.PRESENCE,
        ContactsContract.Data.CHAT_CAPABILITY,
        ContactsContract.Data.STATUS,
        ContactsContract.Data.STATUS_RES_PACKAGE,
        ContactsContract.Data.STATUS_ICON,
        ContactsContract.Data.STATUS_LABEL,
        ContactsContract.Data.STATUS_TIMESTAMP,
        ContactsContract.Contacts.PHOTO_URI,
        ContactsContract.Contacts.SEND_TO_VOICEMAIL,
        ContactsContract.Contacts.CUSTOM_RINGTONE,
        ContactsContract.Contacts.IS_USER_PROFILE
    )
    val COLUMNS: Array<String>
    const val NAME_RAW_CONTACT_ID_IF_AVAILABLE = 0
    const val DISPLAY_NAME_SOURCE = 1
    const val LOOKUP_KEY = 2
    const val DISPLAY_NAME = 3
    const val ALT_DISPLAY_NAME = 4
    const val PHONETIC_NAME = 5
    const val PHOTO_ID = 6
    const val STARRED = 7
    const val CONTACT_PRESENCE = 8
    const val CONTACT_STATUS = 9
    const val CONTACT_STATUS_TIMESTAMP = 10
    const val CONTACT_STATUS_RES_PACKAGE = 11
    const val CONTACT_STATUS_LABEL = 12
    const val CONTACT_ID = 13
    const val RAW_CONTACT_ID = 14
    const val ACCOUNT_NAME = 15
    const val ACCOUNT_TYPE = 16
    const val DATA_SET = 17
    const val DIRTY = 18
    const val VERSION = 19
    const val SOURCE_ID = 20
    const val SYNC1 = 21
    const val SYNC2 = 22
    const val SYNC3 = 23
    const val SYNC4 = 24
    const val DELETED = 25
    const val DATA_ID = 26
    const val DATA1 = 27
    const val DATA2 = 28
    const val DATA3 = 29
    const val DATA4 = 30
    const val DATA5 = 31
    const val DATA6 = 32
    const val DATA7 = 33
    const val DATA8 = 34
    const val DATA9 = 35
    const val DATA10 = 36
    const val DATA11 = 37
    const val DATA12 = 38
    const val DATA13 = 39
    const val DATA14 = 40
    const val DATA15 = 41
    const val DATA_SYNC1 = 42
    const val DATA_SYNC2 = 43
    const val DATA_SYNC3 = 44
    const val DATA_SYNC4 = 45
    const val DATA_VERSION = 46
    const val IS_PRIMARY = 47
    const val IS_SUPERPRIMARY = 48
    const val MIMETYPE = 49
    const val GROUP_SOURCE_ID = 50
    const val PRESENCE = 51
    const val CHAT_CAPABILITY = 52
    const val STATUS = 53
    const val STATUS_RES_PACKAGE = 54
    const val STATUS_ICON = 55
    const val STATUS_LABEL = 56
    const val STATUS_TIMESTAMP = 57
    const val PHOTO_URI = 58
    const val SEND_TO_VOICEMAIL = 59
    const val CUSTOM_RINGTONE = 60
    const val IS_USER_PROFILE = 61
    const val CARRIER_PRESENCE = 62

    init {
        val projectionList = COLUMNS_INTERNAL.toMutableList()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            projectionList.add(ContactsContract.Data.CARRIER_PRESENCE)
        }
        COLUMNS = projectionList.toTypedArray()
    }
}