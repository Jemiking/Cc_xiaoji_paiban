package com.example.cc_xiaoji.presentation.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import java.time.LocalDate
import java.time.ZoneId

/**
 * 日期选择器对话框
 * @param showDialog 是否显示对话框
 * @param initialDate 初始日期
 * @param onDateSelected 日期选择回调
 * @param onDismiss 关闭对话框回调
 * @param dateValidator 日期验证器，返回false的日期将被禁用
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    showDialog: Boolean,
    initialDate: LocalDate? = null,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
    dateValidator: ((LocalDate) -> Boolean)? = null
) {
    if (showDialog) {
        // 获取初始日期的毫秒数
        val initialDateMillis = initialDate?.let { date ->
            date.atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        }
        
        // 创建日期选择器状态
        val datePickerState = if (dateValidator != null) {
            rememberDatePickerState(
                initialSelectedDateMillis = initialDateMillis,
                selectableDates = object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        val date = java.time.Instant.ofEpochMilli(utcTimeMillis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        return dateValidator(date)
                    }
                }
            )
        } else {
            rememberDatePickerState(
                initialSelectedDateMillis = initialDateMillis
            )
        }
        
        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = java.time.Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            onDateSelected(selectedDate)
                        }
                        onDismiss()
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("取消")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                modifier = Modifier
            )
        }
    }
}

/**
 * 创建日期范围选择器对话框
 * @param showDialog 是否显示对话框
 * @param initialStartDate 初始开始日期
 * @param initialEndDate 初始结束日期
 * @param onDateRangeSelected 日期范围选择回调
 * @param onDismiss 关闭对话框回调
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerDialog(
    showDialog: Boolean,
    initialStartDate: LocalDate? = null,
    initialEndDate: LocalDate? = null,
    onDateRangeSelected: (LocalDate, LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        var isSelectingStartDate by remember { mutableStateOf(true) }
        var startDate by remember { mutableStateOf(initialStartDate) }
        var endDate by remember { mutableStateOf(initialEndDate) }
        
        // 根据当前选择状态创建日期选择器
        val currentDate = if (isSelectingStartDate) startDate else endDate
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = currentDate?.let { date ->
                date.atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
            }
        )
        
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(if (isSelectingStartDate) "选择开始日期" else "选择结束日期")
            },
            text = {
                DatePicker(
                    state = datePickerState,
                    modifier = Modifier
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = java.time.Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            
                            if (isSelectingStartDate) {
                                startDate = selectedDate
                                isSelectingStartDate = false
                            } else {
                                endDate = selectedDate
                                if (startDate != null && endDate != null) {
                                    // 确保开始日期不晚于结束日期
                                    if (startDate!! <= endDate!!) {
                                        onDateRangeSelected(startDate!!, endDate!!)
                                        onDismiss()
                                    } else {
                                        // 交换日期
                                        onDateRangeSelected(endDate!!, startDate!!)
                                        onDismiss()
                                    }
                                }
                            }
                        }
                    }
                ) {
                    Text(if (isSelectingStartDate) "下一步" else "确定")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("取消")
                }
            }
        )
    }
}