# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 🚀 项目开发进度 (2025-06-09)

### ✅ 已完成功能

#### 1. 基础架构
- Clean Architecture + MVVM 架构实现
- Hilt 依赖注入完整配置
- Room 数据库（版本1）
- Navigation Compose 导航系统
- Material Design 3 主题配置

#### 2. 核心功能模块
- **日历视图** (`CalendarScreen`)
  - 月度日历显示
  - 排班状态标记
  - 快速导航（上月/下月/今天）
  - 月度统计信息显示
  
- **班次管理** (`ShiftManageScreen`)
  - 班次列表展示
  - 新增/编辑班次
  - 班次激活/停用
  - 颜色选择器
  
- **排班编辑** (`ScheduleEditScreen`)
  - 单日排班选择
  - 班次快速切换
  - 备注功能（预留）
  
- **批量排班** (`SchedulePatternScreen`)
  - 单次排班模式
  - 周循环模式
  - 轮班模式（支持休息日设置）
  - 自定义模式（UI已完成，逻辑待实现）
  
- **统计分析** (`ScheduleStatisticsScreen`)
  - 时间范围选择（本周/本月/上月/自定义）
  - 工作天数、休息天数统计
  - 总工时和平均工时计算
  - 班次分布图表
  - 详细班次统计
  
- **数据导出** (`ExportScreen`)
  - CSV格式（Excel兼容）
  - JSON格式（程序处理）
  - 统计报表格式（人工阅读）
  - 文件分享功能
  - 导出历史记录
  
- **设置页面** (`SettingsScreen`)
  - 通用设置（通知、提醒时间、周起始日）
  - 数据管理（备份、恢复、清除）
  - 外观设置（深色模式、主题颜色）
  - 关于信息

#### 3. 数据层实现
- `ShiftEntity` - 班次数据表
- `ScheduleEntity` - 排班数据表
- `ShiftDao` / `ScheduleDao` - 数据访问对象
- `ScheduleRepository` - 仓库模式实现
- TypeConverter - 时间类型转换

#### 4. 业务逻辑层
- 所有必要的 UseCase 已实现
- 领域模型定义完整
- 业务规则封装良好

### 🔧 已解决的技术问题
1. 应用启动时的加载循环问题
2. CalendarViewModel 初始化顺序导致的 NullPointerException
3. Repository 中使用 collect() 导致的无限加载
4. CreateScheduleUseCase 的 invoke 操作符问题
5. Compose 版本兼容性问题
6. Room 配置语法错误
7. Hilt 导入路径错误
8. 网络依赖下载失败（添加阿里云镜像）
9. 各种类型推断和枚举引用问题

### 📋 待完成功能（优先级排序）
1. **中优先级**
   - 排班提醒通知功能实现
   - 日期选择器对话框
   - 时间选择器对话框
   
2. **低优先级**
   - 班次快速选择功能
   - 数据备份恢复功能的完整实现
   - 深色模式的实际切换
   - 自定义排班模式的逻辑实现
   - 应用分享和评价功能

### 🏗️ 项目结构
```
app/src/main/java/com/example/cc_xiaoji/
├── data/
│   ├── local/
│   │   ├── dao/
│   │   ├── database/
│   │   └── entity/
│   └── repository/
├── domain/
│   ├── model/
│   ├── repository/
│   └── usecase/
├── presentation/
│   ├── calendar/
│   ├── edit/
│   ├── export/
│   ├── navigation/
│   ├── pattern/
│   ├── schedule/
│   ├── settings/
│   ├── shift/
│   ├── statistics/
│   └── viewmodel/
├── MainActivity.kt
└── ScheduleApplication.kt
```

### 💡 开发建议
1. 下次开发可以从待完成功能列表继续
2. 所有核心功能已经可以正常使用
3. 建议先进行整体测试，确保功能正常
4. 可以考虑添加单元测试和UI测试

## Important: Development Workflow
**Claude Code should NOT attempt to compile or build the project after making changes.**

The compilation and testing process will be handled manually by the developer in Android Studio. The workflow is:
1. Claude Code makes the requested code changes
2. Developer manually compiles the project in Android Studio
3. If there are compilation errors or issues, the developer will provide feedback to Claude Code
4. Claude Code can then make corrections based on the feedback

### Problem-Solving Approach
**Before implementing any bug fix or solution, Claude Code must:**

1. **Present multiple solution options** (typically 2-3 different approaches)
2. **Analyze pros and cons for each solution:**
  - **优点 (Pros)**: Performance impact, maintainability, code simplicity, compatibility
  - **缺点 (Cons)**: Implementation complexity, potential risks, technical debt, limitations
3. **Provide a clear recommendation with reasoning:**
  - Explicitly state which solution is recommended
  - Explain why this solution is best for the specific context
  - Consider project architecture, existing patterns, and long-term maintainability

**Example format:**
```
问题：[描述具体问题]

方案一：[方案名称]
- 优点：
  • [优点1]
  • [优点2]
- 缺点：
  • [缺点1]
  • [缺点2]

方案二：[方案名称]
- 优点：
  • [优点1]
  • [优点2]
- 缺点：
  • [缺点1]
  • [缺点2]

推荐方案：方案X
理由：[详细解释为什么推荐这个方案]
```

## Language Requirement
**All responses from Claude Code should be in Chinese (中文).** This includes:
- Code comments and documentation
- Explanations and descriptions
- Error messages and feedback
- Communication with the developer

## Project Overview

这是 CC小记排班模块的独立开发项目，最终将作为 `feature/schedule/` 模块集成到 CC小记主项目中。

- **项目名称**: Cc_xiaoji_paiban (排班管理模块)
- **包名**: `com.example.cc_xiaoji`（集成后改为 `com.ccxiaoji.schedule`）
- **语言**: Kotlin
- **最小 SDK**: 24 (Android 7.0)
- **目标 SDK**: 35 (Android 15)
- **架构**: MVVM + Clean Architecture

## Architecture Guidelines

### 模块结构（遵循 CC小记规范）
```
feature-schedule/（当前独立开发，未来集成路径）
├── api/                # 对外暴露的接口
│   └── ScheduleApi.kt
├── data/               # 数据层
│   ├── local/
│   │   ├── dao/       # ShiftDao, ScheduleDao
│   │   └── entity/    # 数据库实体
│   └── repository/    # Repository 实现
├── domain/            # 业务逻辑
│   ├── model/        # Shift, Schedule 等领域模型
│   └── usecase/      # 业务用例
└── presentation/     # UI 层
    ├── navigation/   # 模块内导航
    ├── calendar/     # 日历视图
    ├── shift/        # 班次管理
    └── viewmodel/    # ViewModels
```

### 依赖规则
- 遵循 CC小记的依赖方向：presentation → domain → data
- 不允许反向依赖
- 模块对外只通过 `api/ScheduleApi.kt` 暴露功能

### 技术栈（与 CC小记保持一致）
- **UI**: Jetpack Compose + Material Design 3
- **依赖注入**: Hilt
- **数据库**: Room
- **异步**: Coroutines + Flow
- **导航**: Navigation Compose

## Development Standards

### 命名规范
- **类名**: PascalCase（如 `ShiftEntity`, `ScheduleViewModel`）
- **函数名**: camelCase（如 `getShiftById`, `updateSchedule`）
- **常量**: UPPER_SNAKE_CASE（如 `DEFAULT_SHIFT_COLOR`）
- **包名**: 全小写（如 `com.example.cc_xiaoji.data.local`）

### 数据库设计原则
- 所有实体包含 `syncStatus` 字段（为未来同步功能预留）
- 使用 Room 的 TypeConverter 处理复杂类型
- 数据库版本从 1 开始

### UI 开发原则
- 使用 Compose 构建所有 UI
- 遵循 Material Design 3 设计规范
- 支持深色模式
- 响应式布局适配不同屏幕

## Current Development Focus

当前作为独立 APP 开发，但需要考虑未来集成：
1. **保持模块化**: 所有功能封装在合适的层级中
2. **最小化外部依赖**: 避免使用 CC小记核心模块之外的依赖
3. **预留接口**: 设计时考虑与其他模块（如 Todo、Habit）的潜在交互
4. **数据独立**: 排班数据独立管理，通过 API 暴露

## Integration Preparation

未来集成到 CC小记时需要：
1. 调整包名为 `com.ccxiaoji.schedule`
2. 实现 `ScheduleApi` 接口
3. 注册到 CC小记的模块系统
4. 适配 CC小记的主题系统
5. 集成到底部导航

## Important Notes

- 代码注释使用中文
- 所有字符串资源需要支持国际化（至少中文）
- 考虑无障碍支持
- 注意内存优化，特别是日历视图的性能