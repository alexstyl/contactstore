package com.alexstyl.contactstore.sample

import com.alexstyl.contactstore.Contact

sealed class ContactListState {
    object Loading : ContactListState()
    object PermissionRequired : ContactListState()
    data class Loaded(val contacts: List<Contact>) : ContactListState()
}