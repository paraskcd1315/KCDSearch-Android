package com.paraskcd.kcdsearch.di

import android.content.Context
import androidx.room.Room
import com.paraskcd.kcdsearch.data.local.AppDatabase
import com.paraskcd.kcdsearch.data.local.dao.RecentAppDao
import com.paraskcd.kcdsearch.data.local.dao.SearchCacheDao
import com.paraskcd.kcdsearch.data.local.dao.SearchQueryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "kcdsearch.db")
            .fallbackToDestructiveMigration(true)
            .build()

    @Provides
    @Singleton
    fun providesSearchQueryDao(db: AppDatabase): SearchQueryDao = db.searchQueryDao()

    @Provides
    @Singleton
    fun providesSearchCacheDao(db: AppDatabase): SearchCacheDao = db.searchCacheDao()

    @Provides
    @Singleton
    fun providesRecentAppsDao(db: AppDatabase): RecentAppDao = db.recentAppsDao()
}