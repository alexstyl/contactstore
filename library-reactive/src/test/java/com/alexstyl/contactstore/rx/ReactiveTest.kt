package com.alexstyl.contactstore.rx

import com.alexstyl.contactstore.FetchRequest
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx3.asCoroutineDispatcher
import org.junit.Test

internal class ReactiveTest {
    @Test
    fun `flowable emits when internal flow emits`(): Unit = runBlocking {
        val flow = MutableSharedFlow<Int>()
        val trampoline = Schedulers.trampoline()
        FetchRequest(flow, trampoline.asCoroutineDispatcher())
            .asFlowable()
            .observeOn(trampoline)
            .test()
            .run {
                flow.emit(0)
                assertValue(0)

                flow.emit(1)
                assertValues(0, 1)
            }
    }
}
