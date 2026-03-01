package com.paraskcd.kcdsearch.data.repositories

import com.paraskcd.kcdsearch.data.local.dao.RecentAppDao
import com.paraskcd.kcdsearch.data.local.entities.RecentAppEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecentAppsRepository @Inject constructor(
    private val recentAppDao: RecentAppDao
) {
    fun getMostUsedApps(limit: Int = 20): Flow<List<RecentAppEntity>> =
        recentAppDao.getAllByUsage().map { it.take(limit) }

    suspend fun recordAppLaunch(packageName: String, label: String) {
        val existing = recentAppDao.getByPackage(packageName)
        if (existing != null) {
            recentAppDao.upsert(
                existing.copy(
                    useCount = existing.useCount + 1,
                    lastUsedAt = System.currentTimeMillis()
                )
            )
        } else {
            recentAppDao.upsert(RecentAppEntity(packageName = packageName, label = label))
        }
    }

    suspend fun getMostUsedPackageNames(limit: Int = 20): List<String> =
        recentAppDao.getMostUsed(limit).map { it.packageName }
}