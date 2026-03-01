package com.paraskcd.kcdsearch.di

import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.room.Room
import com.paraskcd.kcdsearch.data.api.logging.CacheDeletionLogger
import com.paraskcd.kcdsearch.data.api.logging.NoOpCacheDeletionLoggerImpl
import com.paraskcd.kcdsearch.data.api.logging.RoomCacheDeletionLoggerImpl
import com.paraskcd.kcdsearch.data.local.DebugDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DebugLogModule {
    @Provides
    @Singleton
    fun provideCacheDeletionLogger(
        @ApplicationContext context: Context,
        noOp: NoOpCacheDeletionLoggerImpl,
    ): CacheDeletionLogger {
        val isDebug = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        return if (isDebug) {
            val db = Room.databaseBuilder(
                context,
                DebugDatabase::class.java,
                "kcdsearch_debug.db"
            )
                .fallbackToDestructiveMigration(true)
                .build()
            RoomCacheDeletionLoggerImpl(db.deletionLogDao())
        } else {
            noOp
        }
    }
}