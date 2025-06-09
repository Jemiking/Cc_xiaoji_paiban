package com.example.cc_xiaoji.domain.usecase

import com.example.cc_xiaoji.domain.model.ScheduleStatistics
import com.example.cc_xiaoji.domain.repository.ScheduleRepository
import java.time.LocalDate
import javax.inject.Inject

/**
 * 获取排班统计用例
 */
class GetStatisticsUseCase @Inject constructor(
    private val repository: ScheduleRepository
) {
    /**
     * 获取指定日期范围的统计信息
     */
    suspend operator fun invoke(startDate: LocalDate, endDate: LocalDate): ScheduleStatistics {
        return repository.getStatistics(startDate, endDate)
    }
}