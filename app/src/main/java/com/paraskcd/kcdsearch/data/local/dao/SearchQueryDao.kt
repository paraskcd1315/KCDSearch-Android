package com.paraskcd.kcdsearch.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.paraskcd.kcdsearch.data.local.entities.SearchQueryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchQueryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: SearchQueryEntity)

    @Query("SELECT * FROM search_queries ORDER BY updatedAt DESC")
    fun getAllByUpdatedDesc(): Flow<List<SearchQueryEntity>>

    @Query("SELECT * FROM search_queries ORDER BY updatedAt DESC LIMIT :limit")
    suspend fun getRecent(limit: Int = 20): List<SearchQueryEntity>

    @Query("DELETE FROM search_queries WHERE `query` = :query")
    suspend fun deleteByQuery(query: String)
}