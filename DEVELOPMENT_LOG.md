# 开发日志 - CC小记排班模块

## 2025-06-11 开发记录

### 日期选择器改进

#### 1. Material3 DatePicker 星期显示问题
**问题**: 批量排班界面的日期选择器中，星期标题只显示"星"字
**原因**: Material3 DatePicker 使用系统本地化字符串，在中文环境下被截断
**尝试方案**:
- 自定义星期标题（位置错误）
- 密度调整（无效）
- DisplayMode 修改（无效）
**最终解决**: 创建基于 CalendarView 的 CustomDatePickerDialog

#### 2. 日期选择器美化
**尝试方案**:
- 方案四（卡片堆叠风格）- 效果不理想
- 方案一（Material You 风格）- 成功实施
**改进内容**:
- 大圆角卡片设计
- Tonal 风格按钮
- Surface 层次感
- 统一的色彩系统

#### 3. 格子尺寸优化
**问题**: 日期选择器格子太小，不便点击
**解决**: 创建 PickerCalendarView 组件
- 固定格子高度 48dp
- 字体大小 18sp
- 优化点击区域
- 保持 Material You 风格

#### 4. 项目日期选择器统一规划
**现状分析**:
- CustomDatePickerDialog（已完成）：批量排班界面
- DateRangePickerDialog（待改进）：统计、导出界面
- YearMonthPickerDialog（待改进）：日历主界面
- TimePickerDialog（待改进）：班次编辑
**创建文档**: DATE_PICKER_IMPROVEMENT.md 记录改进方案

### 技术要点
- 使用 DialogProperties(usePlatformDefaultWidth = false) 自定义对话框宽度
- FilledTonalButton/FilledTonalIconButton 实现 Material You 风格
- 固定高度布局避免内容跳动
- 复用现有组件提高开发效率

## 2025-06-09 开发记录

### 项目初始化
- 创建了基于 Clean Architecture 的项目结构
- 配置了 Hilt、Room、Compose、Navigation 等核心依赖
- 设置了阿里云 Maven 镜像解决依赖下载问题

### 功能开发顺序
1. **数据层** (data layer)
   - 创建了 ShiftEntity 和 ScheduleEntity 数据库实体
   - 实现了 ShiftDao 和 ScheduleDao
   - 配置了 ScheduleDatabase 和 TypeConverter
   - 实现了 ScheduleRepositoryImpl

2. **领域层** (domain layer)
   - 定义了 Shift、Schedule、SchedulePattern 等领域模型
   - 创建了 ScheduleRepository 接口
   - 实现了所有必要的 UseCase

3. **表现层** (presentation layer)
   - CalendarScreen - 主界面
   - ShiftManageScreen - 班次管理
   - ScheduleEditScreen - 排班编辑
   - SchedulePatternScreen - 批量排班
   - ScheduleStatisticsScreen - 统计分析
   - ExportScreen - 数据导出
   - SettingsScreen - 设置页面

### 遇到的问题及解决方案

#### 1. 应用启动问题
**问题**: 应用启动后一直显示加载动画
**原因**: 导航系统中重复调用了测试UI
**解决**: 恢复正确的导航路由

#### 2. NullPointerException 崩溃
**问题**: CalendarViewModel 初始化时崩溃
**原因**: 
- 第一次：init 块在属性初始化之前执行
- 第二次：直接调用挂起函数而不是在协程中调用
**解决**: 
- 移除 init 块
- 使用 viewModelScope.launch 包装挂起函数调用

#### 3. 无限加载问题
**问题**: Repository 中的 Flow 导致无限加载
**原因**: 使用 collect() 而不是 first()
**解决**: 将 collect() 改为 first() 获取单次数据

## 2025-06-10 开发记录

### 日历视图模式功能
实现了日历的舒适/紧凑两种视图模式切换：

1. **视图模式设计**
   - **紧凑模式**：正方形格子，较小间距，适合查看更多日期信息
   - **舒适模式**：矩形格子（高度是宽度的2倍），更大的内容显示空间
   - 两种模式都保留统计信息显示

2. **参数调整**
   - 紧凑模式：6dp间距，8dp边距，titleMedium字体
   - 舒适模式：4dp间距，6dp边距，headlineMedium字体
   - 通过减小间距让舒适模式格子实际更大

3. **选中日期详情卡片**
   - 在紧凑模式下利用月视图下方空白区域
   - 显示完整日期、星期、班次详情
   - 提供快速编辑和删除操作按钮
   - 无排班时显示提示信息

### 技术要点
- 使用 `aspectRatio` 控制格子宽高比
- 默认以紧凑模式打开，提供更好的初始体验
- 删除功能集成到 CalendarViewModel 中

### 任意天数循环排班功能
实现了支持任意天数（2-365天）的循环排班模式：

1. **数据结构改进**
   - 新增 `SchedulePattern.Cycle` 模式，支持任意天数循环
   - 保留 `Weekly` 模式并标记为废弃，确保向后兼容
   - 循环模式使用 `Map<Int, Long?>` 存储每天的班次

2. **UI 功能增强**
   - 将"周循环"改为"循环排班"
   - 提供常用周期快捷选择（3天、4天、5天、7天）
   - 支持自定义输入 2-30 天的循环周期
   - 动态生成对应天数的班次选择界面

3. **业务逻辑更新**
   - `CreateScheduleUseCase` 新增 `handleCyclePattern` 方法
   - 支持将旧的 Weekly 模式自动转换为 Cycle 模式
   - 循环计算使用模运算确保正确循环

### 应用场景
- 4天工作制（做四休三）
- 5天工作制（周一到周五）
- 三班倒（3天或6天周期）
- 其他特殊行业的轮班需求

#### 4. CreateScheduleUseCase 调用错误
**问题**: 编译错误 "Unresolved reference"
**原因**: UseCase 没有定义 invoke 操作符
**解决**: 直接调用具体方法 createOrUpdateSchedule

#### 5. 类型和导入问题
- PatternType 枚举的位置调整
- collectAsStateWithLifecycle 需要额外依赖
- 字符串模板语法错误
- Map 类型不匹配问题

### 技术决策
1. **使用 java.time API**
   - 通过 Core Library Desugaring 支持低版本 Android
   - 统一使用 LocalDate、LocalTime、YearMonth

2. **状态管理**
   - 使用 StateFlow 而不是 LiveData
   - UI 状态集中在 UiState 数据类中

3. **导航架构**
   - 使用 Navigation Compose
   - 单 Activity 多 Composable 架构

4. **数据导出**
   - 使用 FileProvider 支持文件分享
   - 支持多种格式满足不同需求

### 性能优化考虑
- 日历视图使用 LazyVerticalGrid 避免一次性加载过多数据
- Repository 使用 Flow 支持响应式更新
- ViewModel 中使用 stateIn 缓存 Flow 数据

### 未来改进方向
1. 添加更多动画效果提升用户体验
2. 实现真正的通知功能
3. 添加数据同步功能
4. 支持更多自定义选项
5. 优化大数据量下的性能

### 代码质量
- 所有代码遵循 Kotlin 编码规范
- 使用中文注释提高可读性
- 保持单一职责原则
- 依赖注入降低耦合度

### 测试建议
1. 单元测试重点：
   - UseCase 业务逻辑
   - Repository 数据转换
   - ViewModel 状态管理

2. UI 测试重点：
   - 导航流程
   - 用户交互
   - 数据显示正确性

### 集成准备
项目设计时已考虑未来集成到 CC小记主项目：
- 模块化架构便于迁移
- 最小化外部依赖
- 预留 API 接口
- 独立的数据管理