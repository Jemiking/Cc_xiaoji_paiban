package com.example.cc_xiaoji.di

import android.content.Context
import androidx.room.Room
import com.example.cc_xiaoji.data.local.database.ScheduleDatabase
import com.example.cc_xiaoji.data.local.dao.ExportHistoryDao
import com.example.cc_xiaoji.data.local.dao.ScheduleDao
import com.example.cc_xiaoji.data.local.dao.ShiftDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt模块 - 提供数据库相关依赖
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    /**
     * 提供数据库实例
     */
    @Provides
    @Singleton
    fun provideScheduleDatabase(
        @ApplicationContext context: Context
    ): ScheduleDatabase {
        android.util.Log.d("DatabaseModule", "Creating ScheduleDatabase")
        return ScheduleDatabase.getInstance(context).also {
            android.util.Log.d("DatabaseModule", "ScheduleDatabase created")
        }
    }
    
    /**
     * 提供班次DAO
     */
    @Provides
    @Singleton
    fun provideShiftDao(database: ScheduleDatabase): ShiftDao {
        return database.shiftDao()
    }
    
    /**
     * 提供排班DAO
     */
    @Provides
    @Singleton
    fun provideScheduleDao(database: ScheduleDatabase): ScheduleDao {
        return database.scheduleDao()
    }
    
    /**
     * 提供导出历史DAO
     */
    @Provides
    @Singleton
    fun provideExportHistoryDao(database: ScheduleDatabase): ExportHistoryDao {
        return database.exportHistoryDao()
    }
}