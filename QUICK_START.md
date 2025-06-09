# 快速启动指南

## 项目状态概览
- **开发阶段**: 功能开发基本完成，可进行测试
- **当前版本**: 1.0 (初始版本)
- **最后更新**: 2025-06-09

## 如何继续开发

### 1. 查看待完成功能
查看 `CLAUDE.md` 中的 "📋 待完成功能" 部分，按优先级排序

### 2. 主要入口文件
- **导航配置**: `/presentation/navigation/ScheduleNavigation.kt`
- **主界面**: `/presentation/calendar/CalendarScreen.kt`
- **数据库**: `/data/local/database/ScheduleDatabase.kt`

### 3. 常见编译问题解决
如果遇到编译错误，可能是：
- 依赖版本问题 → 检查 `build.gradle.kts`
- 导入路径错误 → 使用 Android Studio 的自动修复
- 类型推断问题 → 添加显式类型声明

### 4. 测试流程
1. 运行应用，检查主界面是否正常显示
2. 测试班次管理功能（添加、编辑、删除）
3. 测试排班功能（单日、批量）
4. 查看统计和导出功能

### 5. 关键技术点
- **架构**: Clean Architecture + MVVM
- **UI框架**: Jetpack Compose
- **数据库**: Room
- **依赖注入**: Hilt
- **异步处理**: Coroutines + Flow

### 6. 调试技巧
- 所有 ViewModel 和 Repository 都添加了日志
- 使用 `android.util.Log.d()` 查看运行时状态
- Room 数据库位置：`/data/data/com.example.cc_xiaoji/databases/`

### 7. 下一步建议
1. **高优先级**: 实现排班提醒通知功能
2. **中优先级**: 添加日期/时间选择器
3. **低优先级**: 完善数据备份恢复

## 快速命令

### 查看项目结构
```bash
find app/src/main/java/com/example/cc_xiaoji -type f -name "*.kt" | head -20
```

### 查找特定功能
```bash
grep -r "TODO" app/src/main/java/com/example/cc_xiaoji/
```

### 查看最近修改
```bash
find app/src/main/java -name "*.kt" -mtime -1
```

## 联系方式
如有问题，请查看：
- `CLAUDE.md` - 项目指南和开发进度
- `DEVELOPMENT_LOG.md` - 详细开发记录
- 代码注释 - 所有关键逻辑都有中文注释

## 重要提醒
1. **不要**直接修改数据库结构，需要处理迁移
2. **保持**代码风格一致性
3. **测试**所有改动，特别是数据相关操作
4. **更新**文档，记录重要变更