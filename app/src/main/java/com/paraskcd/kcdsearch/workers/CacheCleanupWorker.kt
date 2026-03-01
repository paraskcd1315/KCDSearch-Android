package com.paraskcd.kcdsearch.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.paraskcd.kcdsearch.data.api.logging.CacheDeletionLogger
import com.paraskcd.kcdsearch.data.repositories.SearchCacheRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CacheCleanupWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted params: WorkerParameters,
    private val searchCacheRepository: SearchCacheRepository,
    private val cacheDeletionLogger: CacheDeletionLogger,
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result = runCatching {
        searchCacheRepository.deleteExpiredCache()
        Result.success()
    }.getOrElse { error ->
        cacheDeletionLogger.logFailure("cache_cleanup_worker", error.message ?: error.toString())
        Result.failure()
    }
}