package com.example.cc_xiaoji.domain.usecase

import com.example.cc_xiaoji.domain.repository.ScheduleRepository
import javax.inject.Inject

/**
 * 删除排班用例
 */
class DeleteScheduleUseCase @Inject constructor(
    private val repository: ScheduleRepository
) {
    /**
     * 删除排班
     */
    suspend operator fun invoke(scheduleId: Long) {
        repository.deleteSchedule(scheduleId)
    }
}