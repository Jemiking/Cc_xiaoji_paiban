package com.example.cc_xiaoji.presentation.pattern

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cc_xiaoji.domain.model.SchedulePattern
import com.example.cc_xiaoji.domain.model.Shift
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/**
 * 排班模式界面
 * 支持多种排班模式：单次、周循环、轮班、自定义
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedulePatternScreen(
    onNavigateBack: () -> Unit,
    viewModel: SchedulePatternViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val shifts by viewModel.shifts.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("批量排班") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.createSchedules()
                        },
                        enabled = uiState.canCreate
                    ) {
                        Text("创建")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 日期范围选择
            item {
                DateRangeSection(
                    startDate = uiState.startDate,
                    endDate = uiState.endDate,
                    onStartDateChange = viewModel::updateStartDate,
                    onEndDateChange = viewModel::updateEndDate
                )
            }
            
            // 排班模式选择
            item {
                PatternTypeSection(
                    selectedType = uiState.patternType,
                    onTypeChange = viewModel::updatePatternType
                )
            }
            
            // 根据不同模式显示不同的配置界面
            when (uiState.patternType) {
                PatternType.SINGLE -> {
                    item {
                        SinglePatternSection(
                            shifts = shifts,
                            selectedShift = uiState.selectedShift,
                            onShiftSelect = viewModel::selectShift
                        )
                    }
                }
                PatternType.WEEKLY -> {
                    item {
                        WeeklyPatternSection(
                            shifts = shifts,
                            weekPattern = uiState.weekPattern,
                            onPatternChange = viewModel::updateWeekPattern
                        )
                    }
                }
                PatternType.ROTATION -> {
                    item {
                        RotationPatternSection(
                            shifts = shifts,
                            selectedShifts = uiState.rotationShifts,
                            restDays = uiState.restDays,
                            onShiftsChange = viewModel::updateRotationShifts,
                            onRestDaysChange = viewModel::updateRestDays
                        )
                    }
                }
                PatternType.CUSTOM -> {
                    item {
                        Text(
                            "自定义模式暂未实现",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
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
    if (uiState.isSuccess) {
        LaunchedEffect(Unit) {
            onNavigateBack()
        }
    }
    
    // 显示错误消息
    uiState.errorMessage?.let { error: String ->
        LaunchedEffect(error) {
            // 这里可以显示 Snackbar
        }
    }
}

/**
 * 日期范围选择部分
 */
@Composable
private fun DateRangeSection(
    startDate: LocalDate,
    endDate: LocalDate,
    onStartDateChange: (LocalDate) -> Unit,
    onEndDateChange: (LocalDate) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "日期范围",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DateSelector(
                    label = "开始日期",
                    date = startDate,
                    onDateChange = onStartDateChange,
                    modifier = Modifier.weight(1f)
                )
                
                Text("至", style = MaterialTheme.typography.bodyMedium)
                
                DateSelector(
                    label = "结束日期",
                    date = endDate,
                    onDateChange = onEndDateChange,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * 日期选择器（简化版，实际项目中应使用日期选择对话框）
 */
@Composable
private fun DateSelector(
    label: String,
    date: LocalDate,
    onDateChange: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier,
        onClick = {
            // TODO: 显示日期选择对话框
        }
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * 排班模式选择部分
 */
@Composable
private fun PatternTypeSection(
    selectedType: PatternType,
    onTypeChange: (PatternType) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "排班模式",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            PatternType.values().forEach { type ->
                FilterChip(
                    selected = selectedType == type,
                    onClick = { onTypeChange(type) },
                    label = { Text(type.displayName) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * 单次排班配置
 */
@Composable
private fun SinglePatternSection(
    shifts: List<Shift>,
    selectedShift: Shift?,
    onShiftSelect: (Shift) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "选择班次",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            shifts.forEach { shift ->
                ShiftSelectionItem(
                    shift = shift,
                    isSelected = selectedShift?.id == shift.id,
                    onSelect = { onShiftSelect(shift) }
                )
            }
        }
    }
}

/**
 * 周循环排班配置
 */
@Composable
private fun WeeklyPatternSection(
    shifts: List<Shift>,
    weekPattern: Map<DayOfWeek, Long?>,
    onPatternChange: (DayOfWeek, Long?) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "周循环设置",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            DayOfWeek.values().forEach { dayOfWeek ->
                WeekDayShiftSelector(
                    dayOfWeek = dayOfWeek,
                    shifts = shifts,
                    selectedShiftId = weekPattern[dayOfWeek],
                    onShiftSelect = { shiftId ->
                        onPatternChange(dayOfWeek, shiftId)
                    }
                )
            }
        }
    }
}

/**
 * 轮班模式配置
 */
@Composable
private fun RotationPatternSection(
    shifts: List<Shift>,
    selectedShifts: List<Long>,
    restDays: Int,
    onShiftsChange: (List<Long>) -> Unit,
    onRestDaysChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "轮班设置",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                "选择轮班顺序",
                style = MaterialTheme.typography.bodyMedium
            )
            
            shifts.forEach { shift ->
                val isSelected = selectedShifts.contains(shift.id)
                val position = if (isSelected) selectedShifts.indexOf(shift.id) + 1 else null
                
                ShiftSelectionItem(
                    shift = shift,
                    isSelected = isSelected,
                    position = position,
                    onSelect = {
                        val newList = if (isSelected) {
                            selectedShifts - shift.id
                        } else {
                            selectedShifts + shift.id
                        }
                        onShiftsChange(newList)
                    }
                )
            }
            
            Divider()
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("轮班后休息天数")
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { if (restDays > 0) onRestDaysChange(restDays - 1) }
                    ) {
                        Text("-")
                    }
                    Text(restDays.toString())
                    IconButton(
                        onClick = { onRestDaysChange(restDays + 1) }
                    ) {
                        Text("+")
                    }
                }
            }
        }
    }
}

/**
 * 班次选择项
 */
@Composable
private fun ShiftSelectionItem(
    shift: Shift,
    isSelected: Boolean,
    position: Int? = null,
    onSelect: () -> Unit
) {
    OutlinedCard(
        onClick = onSelect,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            Color(shift.color),
                            shape = MaterialTheme.shapes.small
                        )
                )
                
                Column {
                    Text(
                        shift.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        "${shift.startTime} - ${shift.endTime}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            if (isSelected) {
                if (position != null) {
                    Badge {
                        Text(position.toString())
                    }
                } else {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "已选择",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

/**
 * 星期几班次选择器
 */
@Composable
private fun WeekDayShiftSelector(
    dayOfWeek: DayOfWeek,
    shifts: List<Shift>,
    selectedShiftId: Long?,
    onShiftSelect: (Long?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedShift = shifts.find { it.id == selectedShiftId }
    
    OutlinedCard(
        onClick = { expanded = true },
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                dayOfWeek.getDisplayName(TextStyle.FULL, Locale.CHINA),
                style = MaterialTheme.typography.bodyMedium
            )
            
            if (selectedShift != null) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(
                                Color(selectedShift.color),
                                shape = MaterialTheme.shapes.small
                            )
                    )
                    Text(
                        selectedShift.name,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                Text(
                    "休息",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("休息") },
                onClick = {
                    onShiftSelect(null)
                    expanded = false
                }
            )
            
            shifts.forEach { shift ->
                DropdownMenuItem(
                    text = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(
                                        Color(shift.color),
                                        shape = MaterialTheme.shapes.small
                                    )
                            )
                            Text(shift.name)
                        }
                    },
                    onClick = {
                        onShiftSelect(shift.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

