package com.example.cc_xiaoji.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.example.cc_xiaoji.data.local.converter.DateConverter
import com.example.cc_xiaoji.data.local.dao.ScheduleDao
import com.example.cc_xiaoji.data.local.dao.ShiftDao
import com.example.cc_xiaoji.data.local.entity.ScheduleEntity
import com.example.cc_xiaoji.data.local.entity.ShiftEntity

/**
 * 排班模块数据库
 * 管理班次和排班数据的本地存储
 */
@Database(
    entities = [
        ShiftEntity::class,
        ScheduleEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(DateConverter::class)
abstract class ScheduleDatabase : RoomDatabase() {
    
    abstract fun shiftDao(): ShiftDao
    abstract fun scheduleDao(): ScheduleDao
    
    companion object {
        private const val DATABASE_NAME = "schedule_database"
        
        @Volatile
        private var INSTANCE: ScheduleDatabase? = null
        
        fun getInstance(context: Context): ScheduleDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ScheduleDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}