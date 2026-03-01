package com.paraskcd.kcdsearch.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.paraskcd.kcdsearch.data.local.entities.DeletionLogEntity

@Dao
interface DeletionLogDao {
    @Insert
    suspend fun insert(entity: DeletionLogEntity)

    @Query("DELETE FROM deletion_logs WHERE timestamp < :expireTime")
    suspend fun deleteOlderThan(expireTime: Long)

    @Query("SELECT * FROM deletion_logs ORDER BY timestamp DESC")
    suspend fun getAll(): List<DeletionLogEntity>
}