package com.paraskcd.kcdsearch.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.paraskcd.kcdsearch.data.local.entities.RecentAppEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentAppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: RecentAppEntity)

    @Query("UPDATE recent_apps SET useCount = useCount + 1, lastUsedAt = :timestamp WHERE packageName = :packageName")
    suspend fun incrementUse(packageName: String, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT * FROM recent_apps ORDER BY useCount DESC, lastUsedAt DESC LIMIT :limit")
    suspend fun getMostUsed(limit: Int = 10): List<RecentAppEntity>

    @Query("SELECT * FROM recent_apps ORDER BY useCount DESC, lastUsedAt DESC")
    fun getAllByUsage(): Flow<List<RecentAppEntity>>

    @Query("SELECT * FROM recent_apps WHERE packageName = :packageName")
    suspend fun getByPackage(packageName: String): RecentAppEntity?

    @Transaction
    suspend fun recordAppLaunch(packageName: String, label: String) {
        val existing = getByPackage(packageName)
        if (existing != null) {
            upsert(existing.copy(useCount = existing.useCount + 1, lastUsedAt = System.currentTimeMillis()))
        } else {
            upsert(RecentAppEntity(packageName = packageName, label = label))
        }
    }
}
