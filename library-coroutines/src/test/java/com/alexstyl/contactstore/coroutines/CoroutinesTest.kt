package com.alexstyl.contactstore.coroutines

import app.cash.turbine.test
import com.alexstyl.contactstore.FetchRequest
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

internal class CoroutinesTest {

    @Test
    fun `flow emits when internal flow emits`() = runBlocking {
        val flow = MutableSharedFlow<Int>()

        FetchRequest(flow)
            .asFlow()
            .test {
                flow.emit(0)
                val value = awaitItem()
                assertThat(value).isEqualTo(0)

                flow.emit(1)
                val value2 = awaitItem()
                assertThat(value2).isEqualTo(1)

                expectNoEvents()
            }
    }
}
