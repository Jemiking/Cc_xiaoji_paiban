package com.example.cc_xiaoji.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cc_xiaoji.domain.model.Schedule
import com.example.cc_xiaoji.domain.model.ScheduleStatistics
import com.example.cc_xiaoji.domain.usecase.GetMonthScheduleUseCase
import com.example.cc_xiaoji.domain.usecase.GetScheduleStatisticsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

/**
 * 日历视图的ViewModel
 * 管理排班日历的状态和业务逻辑
 */
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val getMonthScheduleUseCase: GetMonthScheduleUseCase,
    private val getScheduleStatisticsUseCase: GetScheduleStatisticsUseCase
) : ViewModel() {
    
    // 在属性初始化完成后，init块会在下面执行
    
    // 当前显示的年月
    private val _currentYearMonth = MutableStateFlow(YearMonth.now())
    val currentYearMonth: StateFlow<YearMonth> = _currentYearMonth.asStateFlow()
    
    // 选中的日期
    private val _selectedDate = MutableStateFlow<LocalDate?>(LocalDate.now())
    val selectedDate: StateFlow<LocalDate?> = _selectedDate.asStateFlow()
    
    // 当前月份的排班列表
    val schedules: StateFlow<List<Schedule>> = _currentYearMonth
        .flatMapLatest { yearMonth ->
            getMonthScheduleUseCase(yearMonth)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // 月度统计信息
    private val _monthlyStatistics = MutableStateFlow<ScheduleStatistics?>(null)
    val monthlyStatistics: StateFlow<ScheduleStatistics?> = _monthlyStatistics.asStateFlow()
    
    // UI状态
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()
    
    init {
        android.util.Log.d("CalendarViewModel", "ViewModel initialized")
        // 延迟加载初始统计信息，确保所有属性都已初始化
        viewModelScope.launch {
            android.util.Log.d("CalendarViewModel", "Loading initial statistics")
            loadMonthlyStatistics()
        }
    }
    
    /**
     * 切换到上一个月
     */
    fun navigateToPreviousMonth() {
        _currentYearMonth.value = _currentYearMonth.value.minusMonths(1)
        loadMonthlyStatistics()
    }
    
    /**
     * 切换到下一个月
     */
    fun navigateToNextMonth() {
        _currentYearMonth.value = _currentYearMonth.value.plusMonths(1)
        loadMonthlyStatistics()
    }
    
    /**
     * 切换到今天
     */
    fun navigateToToday() {
        _currentYearMonth.value = YearMonth.now()
        _selectedDate.value = LocalDate.now()
        loadMonthlyStatistics()
    }
    
    /**
     * 选择日期
     */
    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }
    
    /**
     * 加载月度统计信息
     */
    private fun loadMonthlyStatistics() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val statistics = getScheduleStatisticsUseCase.getMonthlyStatistics(_currentYearMonth.value)
                _monthlyStatistics.value = statistics
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = "加载统计信息失败：${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * 清除错误信息
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

/**
 * 日历UI状态
 */
data class CalendarUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)