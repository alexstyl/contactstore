package com.alexstyl.contactstore

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

public class FetchRequest<T>(
    private val flow: Flow<T>
) {
    private val scope = CoroutineScope(Dispatchers.IO + Job())

    /**
     * Returns the values of the given request in a blocking manner.
     */
    public fun blockingGet(): T {
        return runBlocking { flow.first() }
    }

    /**
     * Accepts the given collector and emits values into it.
     *
     * The collector will continue receiving new values once a change is detected (i.e. an other app adds a new contact or a Content Provider syncs a new account) and never stops.
     *
     * Make sure to call [FetchJob.cancel] to clear the job and prevent any memory leaks.
     */
    public fun collect(collector: (T) -> Unit): FetchJob {
        val job = scope.launch {
            flow.collect {
                collector(it)
            }
        }
        return FetchJob(job)
    }
}


public class FetchJob(
    private val coroutineJob: Job,
) {
    public fun cancel() {
        coroutineJob.cancel()
    }
}