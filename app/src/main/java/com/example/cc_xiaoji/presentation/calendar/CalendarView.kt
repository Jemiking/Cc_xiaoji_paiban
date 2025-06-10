package com.example.cc_xiaoji.presentation.calendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cc_xiaoji.domain.model.Schedule
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

/**
 * 日历视图组件
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarView(
    yearMonth: YearMonth,
    selectedDate: LocalDate?,
    schedules: List<Schedule>,
    onDateSelected: (LocalDate) -> Unit,
    onDateLongClick: (LocalDate) -> Unit = {},
    weekStartDay: DayOfWeek = DayOfWeek.MONDAY,
    modifier: Modifier = Modifier
) {
    android.util.Log.d("CalendarView", "Rendering calendar for: $yearMonth, schedules count: ${schedules.size}")
    
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfMonth = yearMonth.atDay(1)
    
    // 计算第一天相对于一周起始日的偏移量
    val firstDayOffset = when (weekStartDay) {
        DayOfWeek.SUNDAY -> firstDayOfMonth.dayOfWeek.value % 7 // 周日开始：周日=0
        DayOfWeek.MONDAY -> (firstDayOfMonth.dayOfWeek.value - 1) % 7 // 周一开始：周一=0
        else -> (firstDayOfMonth.dayOfWeek.value - 1) % 7 // 默认周一开始
    }
    
    // 创建日历网格数据
    val calendarDays = remember(yearMonth, weekStartDay) {
        val days = mutableListOf<LocalDate?>()
        // 添加月初的空白天数
        repeat(firstDayOffset) {
            days.add(null)
        }
        // 添加当月所有天数
        for (day in 1..daysInMonth) {
            days.add(yearMonth.atDay(day))
        }
        days
    }
    
    // 创建日期到排班的映射
    val scheduleMap = remember(schedules) {
        schedules.associateBy { it.date }
    }
    
    Column(modifier = modifier) {
        // 星期标题行
        WeekDayHeader(weekStartDay = weekStartDay)
        
        // 日历网格
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            contentPadding = PaddingValues(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(calendarDays) { date ->
                if (date != null) {
                    DayCell(
                        date = date,
                        schedule = scheduleMap[date],
                        isSelected = date == selectedDate,
                        isToday = date == LocalDate.now(),
                        onClick = { onDateSelected(date) },
                        onLongClick = { onDateLongClick(date) }
                    )
                } else {
                    Box(modifier = Modifier.aspectRatio(1f))
                }
            }
        }
    }
}

/**
 * 星期标题行
 */
@Composable
private fun WeekDayHeader(weekStartDay: DayOfWeek = DayOfWeek.MONDAY) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        val weekDays = when (weekStartDay) {
            DayOfWeek.SUNDAY -> listOf("日", "一", "二", "三", "四", "五", "六")
            DayOfWeek.MONDAY -> listOf("一", "二", "三", "四", "五", "六", "日")
            else -> listOf("一", "二", "三", "四", "五", "六", "日") // 默认周一开始
        }
        
        weekDays.forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = when (day) {
                    "日", "六" -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

/**
 * 日期单元格
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DayCell(
    date: LocalDate,
    schedule: Schedule?,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isSelected -> MaterialTheme.colorScheme.primaryContainer
                isToday -> MaterialTheme.colorScheme.secondaryContainer
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 日期数字
                Text(
                    text = date.dayOfMonth.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                    color = when {
                        isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                        isToday -> MaterialTheme.colorScheme.onSecondaryContainer
                        date.dayOfWeek in listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY) -> 
                            MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
                
                // 班次信息
                schedule?.let { sch ->
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .size(width = 40.dp, height = 16.dp)
                            .background(
                                color = Color(sch.shift.color),
                                shape = MaterialTheme.shapes.small
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = sch.shift.name.take(2),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 10.sp
                            ),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}