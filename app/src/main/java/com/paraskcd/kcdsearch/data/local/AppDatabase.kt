package com.paraskcd.kcdsearch.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.paraskcd.kcdsearch.data.local.dao.RecentAppDao
import com.paraskcd.kcdsearch.data.local.dao.SearchCacheDao
import com.paraskcd.kcdsearch.data.local.dao.SearchQueryDao
import com.paraskcd.kcdsearch.data.local.entities.RecentAppEntity
import com.paraskcd.kcdsearch.data.local.entities.SearchCacheEntity
import com.paraskcd.kcdsearch.data.local.entities.SearchQueryEntity

@Database(
    entities = [
        SearchQueryEntity::class,
        SearchCacheEntity::class,
        RecentAppEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun searchQueryDao(): SearchQueryDao
    abstract fun searchCacheDao(): SearchCacheDao
    abstract fun recentAppsDao(): RecentAppDao
}