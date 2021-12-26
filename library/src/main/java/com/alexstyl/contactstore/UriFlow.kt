package com.alexstyl.contactstore

import android.content.ContentResolver
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

internal fun ContentResolver.uriFlow(uri: Uri): Flow<Unit> {
    return callbackFlow {
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                if (!channel.isClosedForSend) {
                    Log.d("~!", "onChange URI FLOW triggered: ${uri}")
                    trySend(Unit)
                }
            }
        }
        registerContentObserver(uri, true, observer)
        awaitClose {
            unregisterContentObserver(observer)
        }
    }
}
