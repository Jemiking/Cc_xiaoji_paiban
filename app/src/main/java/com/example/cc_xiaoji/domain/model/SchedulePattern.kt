package com.example.cc_xiaoji.domain.model

import java.time.DayOfWeek
import java.time.LocalDate

/**
 * 排班模式
 * 用于定义循环排班规则
 */
sealed class SchedulePattern {
    
    /**
     * 单次排班
     */
    data class Single(
        val date: LocalDate,
        val shiftId: Long
    ) : SchedulePattern()
    
    /**
     * 按周循环排班
     */
    data class Weekly(
        val startDate: LocalDate,
        val endDate: LocalDate,
        val weekPattern: Map<DayOfWeek, Long> // DayOfWeek -> ShiftId
    ) : SchedulePattern()
    
    /**
     * 固定循环排班（如三班倒）
     */
    data class Rotation(
        val startDate: LocalDate,
        val endDate: LocalDate,
        val shiftIds: List<Long>, // 班次ID列表，按顺序循环
        val restDays: Int = 0 // 每个循环后的休息天数
    ) : SchedulePattern()
    
    /**
     * 自定义模式
     */
    data class Custom(
        val startDate: LocalDate,
        val endDate: LocalDate,
        val pattern: List<Long?> // 每天对应的班次ID，null表示休息
    ) : SchedulePattern()
}

/**
 * 排班统计信息
 */
data class ScheduleStatistics(
    val totalDays: Int,
    val workDays: Int,
    val restDays: Int,
    val shiftDistribution: Map<String, Int>, // 班次名称 -> 天数
    val totalHours: Double
)