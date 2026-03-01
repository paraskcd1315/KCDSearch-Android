package com.paraskcd.kcdsearch.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_apps")
data class RecentAppEntity(
    @PrimaryKey val packageName: String,
    val label: String,
    val useCount: Int = 1,
    val lastUsedAt: Long = System.currentTimeMillis()
)
