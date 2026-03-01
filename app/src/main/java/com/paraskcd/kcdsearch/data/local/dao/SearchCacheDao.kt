package com.paraskcd.kcdsearch.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.paraskcd.kcdsearch.data.local.entities.SearchCacheEntity

@Dao
interface SearchCacheDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SearchCacheEntity)

    @Query("SELECT * FROM search_cache WHERE cacheKey = :key AND cachedAt > :minValidTime")
    suspend fun getValid(key: String, minValidTime: Long): SearchCacheEntity?

    @Query("DELETE FROM search_cache WHERE cachedAt < :expireTime")
    suspend fun deleteExpired(expireTime: Long)

    @Query("DELETE FROM search_cache WHERE cacheType = :type AND cachedAt < :expireTime")
    suspend fun deleteExpiredByType(type: String, expireTime: Long)
}