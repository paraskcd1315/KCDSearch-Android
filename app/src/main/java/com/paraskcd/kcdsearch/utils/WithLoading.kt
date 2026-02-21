package com.paraskcd.kcdsearch.utils

import kotlinx.coroutines.flow.MutableStateFlow

suspend fun <T> withLoading(
    isLoading: MutableStateFlow<Boolean>,
    error: MutableStateFlow<Throwable?>,
    block: suspend () -> Result<T>,
): Result<T> {
    isLoading.value = true
    error.value = null
    val result = block()
    isLoading.value = false
    result.onFailure { error.value = it }
    return result
}