package com.paraskcd.kcdsearch.data.api.logging

interface CacheDeletionLogger {
    suspend fun logDeletion(type: String, recordCount: Int)
    suspend fun logFailure(type: String, message: String)
    suspend fun deleteOldLogs()
}