package com.example.cc_xiaoji

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

/**
 * 应用程序入口
 * 启用Hilt依赖注入
 */
@HiltAndroidApp
class ScheduleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.d("ScheduleApplication", "Application onCreate - Hilt initialized")
    }
}