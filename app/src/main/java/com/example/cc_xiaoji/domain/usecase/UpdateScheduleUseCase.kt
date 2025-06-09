package com.example.cc_xiaoji.domain.usecase

import com.example.cc_xiaoji.domain.model.Schedule
import com.example.cc_xiaoji.domain.repository.ScheduleRepository
import javax.inject.Inject

/**
 * 更新排班用例
 */
class UpdateScheduleUseCase @Inject constructor(
    private val repository: ScheduleRepository
) {
    /**
     * 更新排班信息
     */
    suspend operator fun invoke(schedule: Schedule) {
        repository.updateSchedule(schedule)
    }
}