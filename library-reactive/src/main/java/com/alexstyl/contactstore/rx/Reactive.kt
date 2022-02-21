package com.alexstyl.contactstore.rx

import com.alexstyl.contactstore.ContactStore
import com.alexstyl.contactstore.FetchRequest
import com.alexstyl.contactstore.SaveRequest
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

/**
 * Creates a [Flowable] out of the given [FetchRequest].
 *
 * The FetchRequest's job will be cancelled as soon as the Flowable is cancelled.
 */
public fun <T : Any> FetchRequest<T>.asFlowable(): Flowable<T> {
    return Flowable.create({ emitter ->
        val job = this@asFlowable.collect {
            emitter.onNext(it)
        }
        emitter.setCancellable {
            job.cancel()
        }
    }, BackpressureStrategy.LATEST)
}

/**
 * Completable version of [ContactStore.execute]
 */
public fun ContactStore.executeCompletable(builder: SaveRequest.() -> Unit): Completable {
    return Completable.fromCallable {
        execute(builder)
    }
}