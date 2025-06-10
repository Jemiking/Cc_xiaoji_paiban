package com.example.cc_xiaoji.presentation.pattern

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cc_xiaoji.domain.model.SchedulePattern
import com.example.cc_xiaoji.domain.model.Shift
import com.example.cc_xiaoji.domain.usecase.CreateScheduleUseCase
import com.example.cc_xiaoji.domain.usecase.GetActiveShiftsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

/**
 * 排班模式类型
 */
enum class PatternType(val displayName: String) {
    SINGLE("单次排班"),
    WEEKLY("周循环"),
    ROTATION("轮班"),
    CUSTOM("自定义")
}

/**
 * 排班模式界面的ViewModel
 */
@HiltViewModel
class SchedulePatternViewModel @Inject constructor(
    private val getActiveShiftsUseCase: GetActiveShiftsUseCase,
    private val createScheduleUseCase: CreateScheduleUseCase
) : ViewModel() {
    
    // 班次列表
    val shifts: StateFlow<List<Shift>> = getActiveShiftsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // UI状态
    private val _uiState = MutableStateFlow(SchedulePatternUiState())
    val uiState: StateFlow<SchedulePatternUiState> = _uiState.asStateFlow()
    
    /**
     * 更新开始日期
     */
    fun updateStartDate(date: LocalDate) {
        _uiState.update { state ->
            state.copy(
                startDate = date,
                endDate = if (date.isAfter(state.endDate)) date else state.endDate
            )
        }
        
        // 如果是自定义模式，重新初始化
        if (_uiState.value.patternType == PatternType.CUSTOM) {
            initializeCustomPattern()
        }
    }
    
    /**
     * 更新结束日期
     */
    fun updateEndDate(date: LocalDate) {
        _uiState.update { state ->
            state.copy(
                endDate = if (date.isBefore(state.startDate)) state.startDate else date
            )
        }
        
        // 如果是自定义模式，重新初始化
        if (_uiState.value.patternType == PatternType.CUSTOM) {
            initializeCustomPattern()
        }
    }
    
    /**
     * 更新排班模式类型
     */
    fun updatePatternType(type: PatternType) {
        _uiState.update { it.copy(patternType = type) }
        
        // 当切换到自定义模式时，初始化模式数据
        if (type == PatternType.CUSTOM && _uiState.value.customPattern.isEmpty()) {
            initializeCustomPattern()
        }
    }
    
    /**
     * 选择班次（单次模式）
     */
    fun selectShift(shift: Shift) {
        _uiState.update { it.copy(selectedShift = shift) }
    }
    
    /**
     * 更新周模式
     */
    fun updateWeekPattern(dayOfWeek: DayOfWeek, shiftId: Long?) {
        _uiState.update { state ->
            state.copy(
                weekPattern = state.weekPattern + (dayOfWeek to shiftId)
            )
        }
    }
    
    /**
     * 更新轮班班次
     */
    fun updateRotationShifts(shiftIds: List<Long>) {
        _uiState.update { it.copy(rotationShifts = shiftIds) }
    }
    
    /**
     * 更新休息天数
     */
    fun updateRestDays(days: Int) {
        _uiState.update { it.copy(restDays = days.coerceAtLeast(0)) }
    }
    
    /**
     * 初始化自定义模式
     */
    fun initializeCustomPattern() {
        val state = _uiState.value
        val days = java.time.temporal.ChronoUnit.DAYS.between(state.startDate, state.endDate).toInt() + 1
        _uiState.update { 
            it.copy(customPattern = List(days) { null })
        }
    }
    
    /**
     * 更新自定义模式中某一天的班次
     */
    fun updateCustomPattern(index: Int, shiftId: Long?) {
        _uiState.update { state ->
            val newPattern = state.customPattern.toMutableList()
            if (index in newPattern.indices) {
                newPattern[index] = shiftId
            }
            state.copy(customPattern = newPattern)
        }
    }
    
    /**
     * 清空自定义模式
     */
    fun clearCustomPattern() {
        _uiState.update { 
            it.copy(customPattern = emptyList())
        }
    }
    
    /**
     * 创建排班
     */
    fun createSchedules() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            try {
                val state = _uiState.value
                val pattern = when (state.patternType) {
                    PatternType.SINGLE -> {
                        val shiftId = state.selectedShift?.id
                            ?: throw IllegalStateException("请选择班次")
                        
                        // 为日期范围内的每一天创建相同的排班
                        var currentDate = state.startDate
                        while (!currentDate.isAfter(state.endDate)) {
                            SchedulePattern.Single(
                                date = currentDate,
                                shiftId = shiftId
                            ).let { createScheduleUseCase.createSchedulesByPattern(it) }
                            currentDate = currentDate.plusDays(1)
                        }
                        null // 已处理，返回null
                    }
                    
                    PatternType.WEEKLY -> {
                        if (state.weekPattern.values.all { it == null }) {
                            throw IllegalStateException("请至少设置一天的班次")
                        }
                        SchedulePattern.Weekly(
                            startDate = state.startDate,
                            endDate = state.endDate,
                            weekPattern = state.weekPattern.filterValues { it != null }.mapValues { it.value!! }
                        )
                    }
                    
                    PatternType.ROTATION -> {
                        if (state.rotationShifts.isEmpty()) {
                            throw IllegalStateException("请选择轮班班次")
                        }
                        SchedulePattern.Rotation(
                            startDate = state.startDate,
                            endDate = state.endDate,
                            shiftIds = state.rotationShifts,
                            restDays = state.restDays
                        )
                    }
                    
                    PatternType.CUSTOM -> {
                        if (state.customPattern.isEmpty()) {
                            throw IllegalStateException("请配置自定义排班模式")
                        }
                        SchedulePattern.Custom(
                            startDate = state.startDate,
                            endDate = state.endDate,
                            pattern = state.customPattern
                        )
                    }
                }
                
                // 如果pattern不为null，执行批量创建
                pattern?.let {
                    createScheduleUseCase.createSchedulesByPattern(it)
                }
                
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "创建失败"
                    )
                }
            }
        }
    }
}

/**
 * 排班模式UI状态
 */
data class SchedulePatternUiState(
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate = LocalDate.now().plusDays(6),
    val patternType: PatternType = PatternType.SINGLE,
    
    // 单次模式
    val selectedShift: Shift? = null,
    
    // 周循环模式
    val weekPattern: Map<DayOfWeek, Long?> = DayOfWeek.values().associateWith { null },
    
    // 轮班模式
    val rotationShifts: List<Long> = emptyList(),
    val restDays: Int = 0,
    
    // 自定义模式
    val customPattern: List<Long?> = emptyList(),
    
    // 状态
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
) {
    /**
     * 是否可以创建排班
     */
    val canCreate: Boolean
        get() = when (patternType) {
            PatternType.SINGLE -> selectedShift != null
            PatternType.WEEKLY -> weekPattern.values.any { it != null }
            PatternType.ROTATION -> rotationShifts.isNotEmpty()
            PatternType.CUSTOM -> customPattern.isNotEmpty()
        }
}