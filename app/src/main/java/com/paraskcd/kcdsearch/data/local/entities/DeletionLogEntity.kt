package com.paraskcd.kcdsearch.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deletion_logs")
data class DeletionLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String,
    val recordCount: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val message: String? = null
)
