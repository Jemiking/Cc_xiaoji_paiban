package com.example.cc_xiaoji

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * 应用程序入口
 * 启用Hilt依赖注入
 * 配置WorkManager与Hilt集成
 */
@HiltAndroidApp
class ScheduleApplication : Application(), Configuration.Provider {
    
    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    
    override fun onCreate() {
        super.onCreate()
        Log.d("ScheduleApplication", "Application onCreate - Hilt initialized")
    }
    
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}