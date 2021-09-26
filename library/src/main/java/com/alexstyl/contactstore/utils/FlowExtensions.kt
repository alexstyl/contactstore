package com.alexstyl.contactstore.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart

internal fun Flow<Unit>.startImmediately(): Flow<Unit> {
    return this.onStart { emit(Unit) }
}
