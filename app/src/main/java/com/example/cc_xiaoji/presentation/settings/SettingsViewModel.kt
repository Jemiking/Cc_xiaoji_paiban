package com.example.cc_xiaoji.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * 设置界面的ViewModel
 */
@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {
    
    // UI状态
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    init {
        loadSettings()
    }
    
    /**
     * 更新通知开关
     */
    fun updateNotificationEnabled(enabled: Boolean) {
        _uiState.update { it.copy(notificationEnabled = enabled) }
        saveSettings()
    }
    
    /**
     * 更新自动备份开关
     */
    fun updateAutoBackupEnabled(enabled: Boolean) {
        _uiState.update { it.copy(autoBackupEnabled = enabled) }
        saveSettings()
    }
    
    /**
     * 执行备份
     */
    fun performBackup() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // TODO: 实现备份逻辑
                // 这里应该调用备份用例，将数据库文件复制到外部存储
                
                // 模拟备份成功
                val backupTime = LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                )
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        lastBackupTime = backupTime,
                        successMessage = "备份成功"
                    )
                }
                
                saveSettings()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "备份失败: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * 清除成功消息
     */
    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }
    
    /**
     * 清除错误消息
     */
    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }
    
    /**
     * 加载设置
     */
    private fun loadSettings() {
        // TODO: 从 SharedPreferences 或数据库加载设置
        // 暂时使用默认值
        _uiState.update {
            it.copy(
                notificationEnabled = true,
                notificationTime = "08:00",
                weekStartDay = "星期一",
                autoBackupEnabled = false,
                lastBackupTime = null,
                darkMode = DarkModeOption.SYSTEM,
                appVersion = "1.0"
            )
        }
    }
    
    /**
     * 保存设置
     */
    private fun saveSettings() {
        // TODO: 保存到 SharedPreferences 或数据库
        // 暂时不实现
    }
    
}

/**
 * 设置界面UI状态
 */
data class SettingsUiState(
    // 通知设置
    val notificationEnabled: Boolean = true,
    val notificationTime: String = "08:00",
    
    // 通用设置
    val weekStartDay: String = "星期一",
    
    // 数据管理
    val autoBackupEnabled: Boolean = false,
    val lastBackupTime: String? = null,
    
    // 外观
    val darkMode: DarkModeOption = DarkModeOption.SYSTEM,
    
    // 应用信息
    val appVersion: String = "1.0",
    
    // 状态
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)