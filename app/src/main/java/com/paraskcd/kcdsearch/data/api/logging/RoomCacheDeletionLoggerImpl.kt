package com.paraskcd.kcdsearch.data.api.logging

import com.paraskcd.kcdsearch.data.local.dao.DeletionLogDao
import com.paraskcd.kcdsearch.data.local.entities.DeletionLogEntity
import javax.inject.Singleton

@Singleton
class RoomCacheDeletionLoggerImpl(
    private val deletionLogDao: DeletionLogDao
) : CacheDeletionLogger {
    override suspend fun logDeletion(type: String, recordCount: Int) {
        deletionLogDao.insert(
            DeletionLogEntity(
                type = type,
                recordCount = recordCount,
            )
        )
    }

    override suspend fun logFailure(type: String, message: String) {
        deletionLogDao.insert(
            DeletionLogEntity(
                type = type,
                recordCount = 0,
                message = message,
            )
        )
    }

    override suspend fun deleteOldLogs() {
        val oneMonthAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)
        deletionLogDao.deleteOlderThan(oneMonthAgo)
    }
}