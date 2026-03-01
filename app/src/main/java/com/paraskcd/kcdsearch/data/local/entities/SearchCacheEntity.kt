package com.paraskcd.kcdsearch.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_cache")
data class SearchCacheEntity(
    @PrimaryKey val cacheKey: String,
    val query: String,
    val jsonData: String,
    val cachedAt: Long = System.currentTimeMillis(),
    val cacheType: String
) {
    companion object {
        const val TYPE_SEARCH = "search"
        const val TYPE_SUGGESTIONS = "suggestions"
    }
}
