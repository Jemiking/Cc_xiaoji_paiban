package com.example.cc_xiaoji.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context
import com.example.cc_xiaoji.data.local.converter.DateConverter
import com.example.cc_xiaoji.data.local.dao.ExportHistoryDao
import com.example.cc_xiaoji.data.local.dao.ScheduleDao
import com.example.cc_xiaoji.data.local.dao.ShiftDao
import com.example.cc_xiaoji.data.local.entity.ExportHistoryEntity
import com.example.cc_xiaoji.data.local.entity.ScheduleEntity
import com.example.cc_xiaoji.data.local.entity.ShiftEntity

/**
 * 排班模块数据库
 * 管理班次和排班数据的本地存储
 */
@Database(
    entities = [
        ShiftEntity::class,
        ScheduleEntity::class,
        ExportHistoryEntity::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(DateConverter::class)
abstract class ScheduleDatabase : RoomDatabase() {
    
    abstract fun shiftDao(): ShiftDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun exportHistoryDao(): ExportHistoryDao
    
    companion object {
        private const val DATABASE_NAME = "schedule_database"
        
        @Volatile
        private var INSTANCE: ScheduleDatabase? = null
        
        /**
         * 从版本1迁移到版本2
         * 添加导出历史表
         */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `export_history` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `file_name` TEXT NOT NULL,
                        `file_path` TEXT NOT NULL,
                        `format` TEXT NOT NULL,
                        `start_date` INTEGER NOT NULL,
                        `end_date` INTEGER NOT NULL,
                        `export_time` INTEGER NOT NULL,
                        `file_size` INTEGER,
                        `record_count` INTEGER,
                        `include_statistics` INTEGER NOT NULL,
                        `include_actual_time` INTEGER NOT NULL,
                        `is_deleted` INTEGER NOT NULL,
                        `created_at` INTEGER NOT NULL
                    )
                """.trimIndent())
            }
        }
        
        fun getInstance(context: Context): ScheduleDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ScheduleDatabase::class.java,
                    DATABASE_NAME
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}