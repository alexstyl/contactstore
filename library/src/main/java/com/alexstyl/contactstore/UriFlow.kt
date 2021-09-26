package com.alexstyl.contactstore

import android.content.ContentResolver
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

internal fun ContentResolver.uriFlow(uri: Uri): Flow<Unit> {
    return callbackFlow {
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                trySendBlocking(Unit)
            }
        }
        registerContentObserver(uri, true, observer)
        awaitClose {
            unregisterContentObserver(observer)
        }
    }
}
