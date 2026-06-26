# Tasks - KMP Terminator 大健康薅羊毛终结者

## Phase 1: 项目基础框架搭建

- [x] Task 1.1: 初始化 KMP 项目结构
  - 创建 Gradle 多模块项目（shared、androidApp、iosApp、desktopApp、backend）
  - 配置 Kotlin Multiplatform 插件和目标平台（Android、iOS、JVM Desktop）
  - 配置 Compose Multiplatform 依赖
  - 创建基础 build.gradle.kts 和 settings.gradle.kts
  - 验证项目可编译通过

- [x] Task 1.2: 搭建共享数据模型层 (shared/commonMain)
  - 定义核心数据类：User、HealthApp、TaskTemplate、TaskExecution、RiskEvent
  - 使用 Kotlinx Serialization 注解
  - 创建枚举类型：UserRole、TaskType、ExecutionStatus、RiskLevel 等
  - 创建 API 响应包装类 ApiResponse<T>
  - 编写数据模型单元测试

- [x] Task 1.3: 搭建共享网络层 (shared/commonMain)
  - 配置 Ktor Client 跨平台实例
  - 创建统一的 API 服务接口
  - 实现认证拦截器（JWT Token 注入）
  - 实现错误处理拦截器
  - 创建各模块的 API Client 类（AuthApi、TaskApi、RiskApi、FamilyApi）

- [x] Task 1.4: 搭建共享本地存储层 (shared/commonMain)
  - 配置 SQLDelight 跨平台数据库
  - 创建数据库 Schema（users、health_apps、task_templates、task_executions、risk_events）
  - 实现本地缓存 Repository
  - 实现 Token 存储和读取

- [x] Task 1.5: 搭建依赖注入框架 (shared/commonMain)
  - 配置 Koin 跨平台 DI
  - 创建各模块的 Koin Module
  - 注册网络层、存储层、Repository 的依赖
  - 在各平台入口初始化 Koin

- [x] Task 1.6: 搭建后端基础框架 (backend)
  - 初始化 Ktor Server 项目
  - 配置 Content Negotiation、Status Pages、CORS
  - 配置 JWT 认证中间件
  - 配置 Exposed ORM + PostgreSQL 连接
  - 创建基础数据库迁移脚本
  - 实现健康检查接口 `/api/health`

---

## Phase 2: 用户认证系统

- [x] Task 2.1: 实现后端认证 API
  - 实现 POST /api/auth/send-code（发送验证码，Redis 缓存）
  - 实现 POST /api/auth/login（验证码校验，JWT 生成）
  - 实现 POST /api/auth/refresh（Token 刷新）
  - 实现 POST /api/auth/logout（登出）
  - 编写认证模块集成测试

- [x] Task 2.2: 实现客户端认证流程
  - 创建 AuthRepository（登录、登出、Token 管理）
  - 实现 Token 自动刷新机制
  - 创建登录页面 UI（手机号输入、验证码输入）
  - 实现登录状态持久化和自动登录
  - 创建 AuthViewModel

- [x] Task 2.3: 实现家庭成员绑定功能
  - 后端：实现 POST /api/family/bind 绑定接口
  - 后端：实现 GET /api/family/members 列表接口
  - 客户端：创建家庭成员管理页面
  - 客户端：实现二维码/邀请码绑定流程

---

## Phase 3: 健康应用管理

- [x] Task 3.1: 实现健康应用注册与管理
  - 后端：实现 CRUD 接口 /api/apps
  - 后端：实现任务模板 CRUD 接口 /api/apps/{appId}/tasks
  - 客户端：创建应用列表页面（显示已注册的健康应用）
  - 客户端：创建应用添加/编辑页面
  - 客户端：创建任务模板配置页面

- [x] Task 3.2: 实现应用凭证安全存储
  - 后端：实现凭证加密存储（AES-256）
  - 客户端：创建凭证输入和管理 UI
  - 客户端：实现本地凭证加密缓存

- [x] Task 3.3: 创建预置任务模板库
  - 设计常见健康应用的任务模板（签到、听课、问卷、阅读）
  - 创建模板导入/导出功能
  - 实现模板分类和搜索

---

## Phase 4: 任务执行引擎

- [x] Task 4.1: 实现后端任务调度系统
  - 创建任务调度服务（Quartz 或自定义）
  - 实现任务队列管理（RabbitMQ 集成）
  - 实现定时任务触发逻辑
  - 实现任务执行状态管理 API（GET /api/tasks/{taskId}/status）

- [x] Task 4.2: 实现客户端任务执行引擎
  - 创建 TaskExecutionEngine 核心接口
  - 实现 Android 无障碍服务集成（AccessibilityService）
  - 实现 iOS 自动化框架集成（XCUITest / Shortcuts）
  - 实现 Desktop 自动化框架集成（Java Robot / 原生自动化）
  - 创建执行步骤解析器（解析 template_config）
  - 实现智能等待和随机延迟机制

- [x] Task 4.3: 实现任务执行结果管理
  - 后端：实现任务执行记录存储和查询 API
  - 客户端：创建任务执行进度展示 UI
  - 客户端：创建任务执行历史页面
  - 客户端：实现断点续做逻辑

- [x] Task 4.4: 实现批量任务执行
  - 后端：实现 POST /api/tasks/batch-execute 批量执行接口
  - 客户端：创建一键执行按钮和进度展示
  - 客户端：实现任务执行队列管理

---

## Phase 5: 反欺诈保护系统

- [x] Task 5.1: 实现风险内容分析引擎
  - 创建关键词/模式匹配引擎
  - 实现营销内容识别规则（夸大功效、虚假承诺等）
  - 实现价格异常检测逻辑
  - 创建应用风险评分算法

- [x] Task 5.2: 实现风险事件管理
  - 后端：实现风险事件存储和查询 API（/api/risks/events、/api/risks/summary）
  - 后端：实现应用风险评分 API（/api/risks/apps/{appId}/score）
  - 客户端：创建风险事件列表页面
  - 客户端：创建风险汇总报告页面

- [x] Task 5.3: 实现消费预警和保护措施
  - 后端：实现消费预警规则引擎
  - 客户端：创建消费预警通知弹窗
  - 客户端：实现自动屏蔽消费入口的逻辑
  - 客户端：实现紧急联系子女的通知推送

---

## Phase 6: 通知与报告系统

- [x] Task 6.1: 实现通知系统
  - 后端：集成消息推送服务（FCM / APNs）
  - 后端：实现通知发送服务
  - 客户端：实现通知接收和展示
  - 客户端：创建通知设置页面

- [x] Task 6.2: 实现数据报告生成
  - 后端：实现每日/每周/月度报告生成逻辑
  - 后端：实现报告数据聚合 API
  - 客户端：创建报告查看页面（图表展示）
  - 客户端：创建安全报告导出功能

---

## Phase 7: UI/UX 老年友好适配

- [x] Task 7.1: 实现 Material 3 老年友好主题
  - 创建 ElderlyFriendlyLightColorScheme
  - 创建 ElderlyTypography 大字体预设
  - 创建自定义组件样式（大按钮、大卡片、高对比度）
  - 实现深色模式支持

- [x] Task 7.2: 实现核心页面 UI
  - 首页 Dashboard（今日保护统计、快捷操作、任务列表）
  - 任务详情页（执行步骤、状态、历史）
  - 应用管理页（已注册应用列表、添加应用）
  - 风险保护页（风险事件、安全报告）
  - 个人中心页（设置、家庭成员、关于我们）

- [x] Task 7.3: 实现无障碍和适老化功能
  - 支持系统字体缩放
  - 支持屏幕阅读器（TalkBack / VoiceOver）
  - 实现语音播报功能（任务完成、风险预警）
  - 创建新手引导流程

---

## Phase 8: 测试与优化

- [x] Task 8.1: 编写单元测试
  - shared 层数据模型测试
  - shared 层网络层测试（Mock Ktor）
  - 后端 API 集成测试
  - 任务执行引擎单元测试

- [x] Task 8.2: 编写 UI 测试
  - Compose Multiplatform UI 测试
  - 关键页面交互测试
  - 主题和适老化功能测试

- [x] Task 8.3: 性能优化
  - 优化网络请求（缓存策略、请求合并）
  - 优化本地存储（索引优化、数据清理）
  - 优化 UI 渲染（列表懒加载、图片缓存）

---

# Task Dependencies

- Task 1.1 → Task 1.2, Task 1.3, Task 1.4, Task 1.5, Task 1.6（项目初始化后才能搭建各层）
- Task 1.2, Task 1.3, Task 1.4, Task 1.5 → Task 2.1（共享层完成后实现后端认证）
- Task 2.1 → Task 2.2（后端认证完成后实现客户端认证）
- Task 2.1 → Task 2.3（后端认证完成后实现家庭绑定）
- Task 1.2, Task 1.3 → Task 3.1（共享数据模型和网络层完成后实现应用管理）
- Task 3.1 → Task 3.2, Task 3.3（应用管理基础完成后实现凭证存储和模板库）
- Task 3.1 → Task 4.1（应用管理完成后实现任务调度）
- Task 4.1 → Task 4.2（后端调度完成后实现客户端执行引擎）
- Task 4.2 → Task 4.3, Task 4.4（执行引擎完成后实现结果管理和批量执行）
- Task 3.1 → Task 5.1（应用管理完成后实现风险分析）
- Task 5.1 → Task 5.2, Task 5.3（风险分析完成后实现事件管理和消费预警）
- Task 4.3 → Task 6.2（任务结果数据用于生成报告）
- Task 1.1 → Task 7.1（项目初始化后实现主题系统）
- Task 7.1 → Task 7.2（主题完成后实现页面 UI）
- Task 7.2 → Task 7.3（页面 UI 完成后实现无障碍功能）
- 所有功能任务 → Task 8.1, Task 8.2, Task 8.3（功能完成后进行测试和优化）
