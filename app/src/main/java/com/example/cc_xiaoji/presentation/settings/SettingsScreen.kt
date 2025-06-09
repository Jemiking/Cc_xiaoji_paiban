package com.example.cc_xiaoji.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * 设置界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToShiftManage: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            // 通用设置
            item {
                SettingsCategoryHeader("通用")
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.Schedule,
                    title = "班次管理",
                    subtitle = "添加、编辑班次信息",
                    onClick = onNavigateToShiftManage
                )
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.Notifications,
                    title = "排班提醒",
                    subtitle = if (uiState.notificationEnabled) "已开启" else "已关闭",
                    onClick = { }
                ) {
                    Switch(
                        checked = uiState.notificationEnabled,
                        onCheckedChange = viewModel::updateNotificationEnabled
                    )
                }
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.AccessTime,
                    title = "提醒时间",
                    subtitle = uiState.notificationTime,
                    enabled = uiState.notificationEnabled,
                    onClick = {
                        // TODO: 显示时间选择器
                    }
                )
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.Weekend,
                    title = "每周起始日",
                    subtitle = uiState.weekStartDay,
                    onClick = {
                        // TODO: 显示星期选择对话框
                    }
                )
            }
            
            // 数据管理
            item {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                SettingsCategoryHeader("数据管理")
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.CloudUpload,
                    title = "自动备份",
                    subtitle = if (uiState.autoBackupEnabled) "已开启" else "已关闭",
                    onClick = { }
                ) {
                    Switch(
                        checked = uiState.autoBackupEnabled,
                        onCheckedChange = viewModel::updateAutoBackupEnabled
                    )
                }
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.Backup,
                    title = "立即备份",
                    subtitle = uiState.lastBackupTime?.let { "上次备份: $it" } ?: "从未备份",
                    onClick = {
                        viewModel.performBackup()
                    }
                )
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.Restore,
                    title = "恢复数据",
                    subtitle = "从备份文件恢复数据",
                    onClick = {
                        // TODO: 显示文件选择器
                    }
                )
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.Delete,
                    title = "清除所有数据",
                    subtitle = "删除所有班次和排班数据",
                    onClick = {
                        // TODO: 显示确认对话框
                    }
                )
            }
            
            // 外观
            item {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                SettingsCategoryHeader("外观")
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.DarkMode,
                    title = "深色模式",
                    subtitle = when (uiState.darkMode) {
                        DarkModeOption.SYSTEM -> "跟随系统"
                        DarkModeOption.LIGHT -> "关闭"
                        DarkModeOption.DARK -> "开启"
                    },
                    onClick = {
                        // TODO: 显示深色模式选择对话框
                    }
                )
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.ColorLens,
                    title = "主题颜色",
                    subtitle = "自定义应用主题颜色",
                    onClick = {
                        // TODO: 显示颜色选择器
                    }
                )
            }
            
            // 关于
            item {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                SettingsCategoryHeader("关于")
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "关于 CC小记排班",
                    subtitle = "版本 ${uiState.appVersion}",
                    onClick = onNavigateToAbout
                )
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.Share,
                    title = "分享应用",
                    subtitle = "推荐给朋友",
                    onClick = {
                        // TODO: 分享应用
                    }
                )
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.Star,
                    title = "评价应用",
                    subtitle = "在应用商店评价",
                    onClick = {
                        // TODO: 跳转到应用商店
                    }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
    
    // 显示加载状态
    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
    
    // 显示成功消息
    uiState.successMessage?.let { message ->
        LaunchedEffect(message) {
            // TODO: 显示 Snackbar
            viewModel.clearSuccessMessage()
        }
    }
    
    // 显示错误消息
    uiState.errorMessage?.let { message ->
        AlertDialog(
            onDismissRequest = { viewModel.clearErrorMessage() },
            title = { Text("错误") },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearErrorMessage() }) {
                    Text("确定")
                }
            }
        )
    }
}

/**
 * 设置分类标题
 */
@Composable
private fun SettingsCategoryHeader(
    title: String
) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

/**
 * 设置项
 */
@Composable
private fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String? = null,
    enabled: Boolean = true,
    onClick: () -> Unit,
    trailing: @Composable (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (enabled) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                },
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (enabled) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    }
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (enabled) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        }
                    )
                }
            }
            
            if (trailing != null) {
                Spacer(modifier = Modifier.width(8.dp))
                trailing()
            } else if (!onClick.equals({})) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

/**
 * 深色模式选项
 */
enum class DarkModeOption {
    SYSTEM,
    LIGHT,
    DARK
}