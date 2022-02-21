package com.alexstyl.contactstore.coroutines

import com.alexstyl.contactstore.ContactStore
import com.alexstyl.contactstore.FetchRequest
import com.alexstyl.contactstore.SaveRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext

/**
 * Creates a [Flow] out of the given [FetchRequest].
 *
 * The FetchRequest's job will be cancelled as soon as the Flow is closed.
 */
public fun <T> FetchRequest<T>.asFlow(): Flow<T> {
    return callbackFlow {
        val job = this@asFlow.collect {
            trySend(it)
        }
        awaitClose {
            job.cancel()
        }
    }
}

/**
 * Suspend version of [ContactStore.execute] which will run on the [Dispatchers.IO] dispatcher.
 */
public suspend fun ContactStore.executeSuspend(
    request: SaveRequest.() -> Unit
): Unit = withContext(Dispatchers.IO) {
    execute(request)
}
