package com.example.cc_xiaoji.presentation.export

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cc_xiaoji.domain.usecase.ExportScheduleDataUseCase
import com.example.cc_xiaoji.presentation.statistics.TimeRange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

/**
 * 数据导出界面的ViewModel
 */
@HiltViewModel
class ExportViewModel @Inject constructor(
    private val exportScheduleDataUseCase: ExportScheduleDataUseCase
) : ViewModel() {
    
    // UI状态
    private val _uiState = MutableStateFlow(ExportUiState())
    val uiState: StateFlow<ExportUiState> = _uiState.asStateFlow()
    
    init {
        // 加载导出历史
        loadExportHistory()
    }
    
    /**
     * 更新时间范围
     */
    fun updateTimeRange(range: TimeRange) {
        _uiState.update { it.copy(timeRange = range) }
    }
    
    /**
     * 更新自定义日期范围
     */
    fun updateCustomDateRange(startDate: LocalDate, endDate: LocalDate) {
        _uiState.update {
            it.copy(
                customStartDate = startDate,
                customEndDate = if (endDate.isBefore(startDate)) startDate else endDate
            )
        }
    }
    
    /**
     * 更新导出格式
     */
    fun updateExportFormat(format: ExportFormat) {
        _uiState.update { it.copy(exportFormat = format) }
    }
    
    /**
     * 更新是否包含统计信息
     */
    fun updateIncludeStatistics(include: Boolean) {
        _uiState.update { it.copy(includeStatistics = include) }
    }
    
    /**
     * 更新是否包含实际打卡时间
     */
    fun updateIncludeActualTime(include: Boolean) {
        _uiState.update { it.copy(includeActualTime = include) }
    }
    
    /**
     * 导出数据
     */
    fun exportData(context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            try {
                val (startDate, endDate) = getDateRange()
                val fileName = generateFileName(startDate, endDate)
                val outputFile = File(context.getExternalFilesDir(null), fileName)
                
                val result = when (_uiState.value.exportFormat) {
                    ExportFormat.CSV -> {
                        exportScheduleDataUseCase.exportToCsv(startDate, endDate, outputFile)
                    }
                    ExportFormat.JSON -> {
                        exportScheduleDataUseCase.exportToJson(startDate, endDate, outputFile)
                    }
                    ExportFormat.REPORT -> {
                        exportScheduleDataUseCase.exportStatisticsReport(startDate, endDate, outputFile)
                    }
                }
                
                result.fold(
                    onSuccess = { file ->
                        // 添加到导出历史
                        val exportInfo = ExportInfo(
                            file = file,
                            fileName = file.name,
                            format = _uiState.value.exportFormat,
                            exportTime = LocalDateTime.now()
                        )
                        
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                exportedFile = file,
                                exportHistory = listOf(exportInfo) + state.exportHistory.take(9)
                            )
                        }
                        
                        // 保存导出历史
                        saveExportHistory()
                    },
                    onFailure = { exception ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = exception.message ?: "导出失败"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "导出失败"
                    )
                }
            }
        }
    }
    
    /**
     * 删除导出文件
     */
    fun deleteExportFile(file: File) {
        viewModelScope.launch {
            try {
                if (file.exists()) {
                    file.delete()
                }
                
                _uiState.update { state ->
                    state.copy(
                        exportHistory = state.exportHistory.filter { it.file != file }
                    )
                }
                
                saveExportHistory()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "删除文件失败: ${e.message}")
                }
            }
        }
    }
    
    /**
     * 清除导出文件引用
     */
    fun clearExportedFile() {
        _uiState.update { it.copy(exportedFile = null) }
    }
    
    /**
     * 清除错误消息
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
    
    /**
     * 获取日期范围
     */
    private fun getDateRange(): Pair<LocalDate, LocalDate> {
        val today = LocalDate.now()
        return when (_uiState.value.timeRange) {
            TimeRange.THIS_WEEK -> {
                val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                val endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                startOfWeek to endOfWeek
            }
            TimeRange.THIS_MONTH -> {
                val yearMonth = YearMonth.now()
                yearMonth.atDay(1) to yearMonth.atEndOfMonth()
            }
            TimeRange.LAST_MONTH -> {
                val lastMonth = YearMonth.now().minusMonths(1)
                lastMonth.atDay(1) to lastMonth.atEndOfMonth()
            }
            TimeRange.CUSTOM -> {
                _uiState.value.customStartDate to _uiState.value.customEndDate
            }
        }
    }
    
    /**
     * 生成文件名
     */
    private fun generateFileName(startDate: LocalDate, endDate: LocalDate): String {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
        val format = _uiState.value.exportFormat
        
        return "schedule_${startDate.format(dateFormatter)}_${endDate.format(dateFormatter)}_$timestamp.${format.extension}"
    }
    
    /**
     * 加载导出历史
     */
    private fun loadExportHistory() {
        // TODO: 从本地存储加载导出历史
        // 这里暂时使用空列表
        _uiState.update { it.copy(exportHistory = emptyList()) }
    }
    
    /**
     * 保存导出历史
     */
    private fun saveExportHistory() {
        // TODO: 保存导出历史到本地存储
    }
}

/**
 * 导出界面UI状态
 */
data class ExportUiState(
    val timeRange: TimeRange = TimeRange.THIS_MONTH,
    val customStartDate: LocalDate = LocalDate.now().minusMonths(1),
    val customEndDate: LocalDate = LocalDate.now(),
    val exportFormat: ExportFormat = ExportFormat.CSV,
    val includeStatistics: Boolean = true,
    val includeActualTime: Boolean = false,
    val isLoading: Boolean = false,
    val exportedFile: File? = null,
    val errorMessage: String? = null,
    val exportHistory: List<ExportInfo> = emptyList()
)