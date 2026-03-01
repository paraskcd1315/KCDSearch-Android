package com.paraskcd.kcdsearch.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_queries")
data class SearchQueryEntity(
    @PrimaryKey val query: String,
    val updatedAt: Long = System.currentTimeMillis()
)
