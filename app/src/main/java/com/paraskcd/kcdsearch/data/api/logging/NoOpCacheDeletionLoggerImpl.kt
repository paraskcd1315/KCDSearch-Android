package com.paraskcd.kcdsearch.data.api.logging

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoOpCacheDeletionLoggerImpl @Inject constructor() : CacheDeletionLogger {
    override suspend fun logDeletion(type: String, recordCount: Int) {}
    override suspend fun logFailure(type: String, message: String) {}
    override suspend fun deleteOldLogs() {}
}