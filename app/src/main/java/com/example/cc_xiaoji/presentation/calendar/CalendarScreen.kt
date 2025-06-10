package com.example.cc_xiaoji.presentation.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cc_xiaoji.presentation.components.QuickShiftSelector
import com.example.cc_xiaoji.presentation.viewmodel.CalendarViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/**
 * 排班日历主界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onNavigateToShiftManage: () -> Unit,
    onNavigateToScheduleEdit: (LocalDate) -> Unit,
    onNavigateToSchedulePattern: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    android.util.Log.d("CalendarScreen", "CalendarScreen Composable called")
    
    val currentYearMonth by viewModel.currentYearMonth.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val schedules by viewModel.schedules.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val statistics by viewModel.monthlyStatistics.collectAsState()
    val quickShifts by viewModel.quickShifts.collectAsState()
    val quickSelectDate by viewModel.quickSelectDate.collectAsState()
    val weekStartDay by viewModel.weekStartDay.collectAsState()
    
    android.util.Log.d("CalendarScreen", "CurrentYearMonth: $currentYearMonth, SchedulesCount: ${schedules.size}")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = currentYearMonth.format(
                            DateTimeFormatter.ofPattern("yyyy年MM月")
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateToPreviousMonth() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "上一月")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.navigateToToday() }) {
                        Icon(Icons.Default.Today, contentDescription = "今天")
                    }
                    IconButton(onClick = { viewModel.navigateToNextMonth() }) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "下一月")
                    }
                    IconButton(onClick = onNavigateToStatistics) {
                        Icon(Icons.Default.Analytics, contentDescription = "统计")
                    }
                    IconButton(onClick = onNavigateToSchedulePattern) {
                        Icon(Icons.Default.DateRange, contentDescription = "批量排班")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "设置")
                    }
                }
            )
        },
        floatingActionButton = {
            selectedDate?.let { date ->
                FloatingActionButton(
                    onClick = { onNavigateToScheduleEdit(date) }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "添加排班")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 统计信息卡片
            statistics?.let { stats ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatisticItem("工作天数", "${stats.workDays}天")
                        StatisticItem("休息天数", "${stats.restDays}天")
                        StatisticItem("总工时", "${stats.totalHours.toInt()}小时")
                    }
                }
            }
            
            // 日历视图
            CalendarView(
                yearMonth = currentYearMonth,
                selectedDate = selectedDate,
                schedules = schedules,
                weekStartDay = weekStartDay,
                onDateSelected = { date ->
                    viewModel.selectDate(date)
                },
                onDateLongClick = { date ->
                    viewModel.showQuickSelector(date)
                }
            )
        }
    }
    
    // 错误提示
    uiState.errorMessage?.let { message ->
        LaunchedEffect(message) {
            // TODO: 显示Snackbar
            viewModel.clearError()
        }
    }
    
    // 加载指示器
    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
    
    // 快速选择对话框
    quickSelectDate?.let { date ->
        val currentSchedule = schedules.find { it.date == date }
        QuickShiftSelector(
            isVisible = true,
            selectedDate = date,
            quickShifts = quickShifts,
            currentShift = currentSchedule?.shift,
            onShiftSelected = { shift ->
                viewModel.quickSetSchedule(date, shift)
            },
            onDismiss = {
                viewModel.hideQuickSelector()
            },
            onNavigateToFullSelector = {
                viewModel.hideQuickSelector()
                onNavigateToScheduleEdit(date)
            }
        )
    }
}

@Composable
private fun StatisticItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium
        )
    }
}