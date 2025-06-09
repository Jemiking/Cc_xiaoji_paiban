package com.example.cc_xiaoji.domain.usecase

import com.example.cc_xiaoji.domain.model.Shift
import com.example.cc_xiaoji.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 获取所有激活的班次用例
 */
class GetActiveShiftsUseCase @Inject constructor(
    private val repository: ScheduleRepository
) {
    /**
     * 获取所有激活的班次列表
     */
    operator fun invoke(): Flow<List<Shift>> {
        return repository.getActiveShifts()
    }
}