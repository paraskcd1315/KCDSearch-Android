package com.paraskcd.kcdsearch.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.paraskcd.kcdsearch.data.local.dao.DeletionLogDao
import com.paraskcd.kcdsearch.data.local.entities.DeletionLogEntity

@Database(
    entities = [DeletionLogEntity::class],
    version = 2,
    exportSchema = false
)
abstract class DebugDatabase : RoomDatabase() {
    abstract fun deletionLogDao(): DeletionLogDao
}