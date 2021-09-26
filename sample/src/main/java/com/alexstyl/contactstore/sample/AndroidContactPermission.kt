package com.alexstyl.contactstore.sample

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class AndroidContactPermission(
    private val application: Application
) : ContactPermission {
    override val canReadContacts: Boolean
        get() = when (val state =
            ContextCompat.checkSelfPermission(application, Manifest.permission.READ_CONTACTS)) {
            PackageManager.PERMISSION_GRANTED -> true
            PackageManager.PERMISSION_DENIED -> false
            else -> error("Illegal permission state: $state")
        }
}
