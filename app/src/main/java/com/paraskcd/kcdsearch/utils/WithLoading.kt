package com.paraskcd.kcdsearch.utils

import kotlinx.coroutines.flow.MutableStateFlow

suspend fun withLoading(
    isLoading: MutableStateFlow<Boolean>,
    error: MutableStateFlow<Throwable?>,
    block: suspend () -> Unit,
) {
    isLoading.value = true
    error.value = null
    try {
        block()
    } catch (e: Throwable) {
        error.value = e
    } finally {
        isLoading.value = false
    }
}