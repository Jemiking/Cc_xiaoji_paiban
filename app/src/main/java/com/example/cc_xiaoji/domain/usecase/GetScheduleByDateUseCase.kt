package com.example.cc_xiaoji.domain.usecase

import com.example.cc_xiaoji.domain.model.Schedule
import com.example.cc_xiaoji.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

/**
 * 根据日期获取排班用例
 */
class GetScheduleByDateUseCase @Inject constructor(
    private val repository: ScheduleRepository
) {
    /**
     * 获取指定日期的排班信息
     */
    operator fun invoke(date: LocalDate): Flow<Schedule?> {
        return repository.getScheduleByDate(date)
    }
}