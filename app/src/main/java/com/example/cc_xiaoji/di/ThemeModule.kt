package com.example.cc_xiaoji.di

import android.content.Context
import com.example.cc_xiaoji.presentation.theme.ThemeManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt模块 - 提供主题相关依赖
 */
@Module
@InstallIn(SingletonComponent::class)
object ThemeModule {
    
    /**
     * 提供主题管理器实例
     */
    @Provides
    @Singleton
    fun provideThemeManager(
        @ApplicationContext context: Context
    ): ThemeManager {
        return ThemeManager(context)
    }
}