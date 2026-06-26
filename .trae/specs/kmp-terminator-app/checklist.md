# Checklist - KMP Terminator 大健康薅羊毛终结者

## 一、项目基础框架验收

- [x] Gradle 多模块项目结构正确（shared、androidApp、iosApp、desktopApp、backend）
- [x] Kotlin Multiplatform 配置正确，支持 Android、iOS、JVM Desktop 目标
- [x] Compose Multiplatform 依赖配置正确
- [x] 项目可通过 `./gradlew build` 编译
- [x] shared/commonMain 中定义了所有核心数据类（User、HealthApp、TaskTemplate、TaskExecution、RiskEvent）
- [x] Kotlinx Serialization 注解正确应用于所有数据类
- [x] API 响应包装类 ApiResponse<T> 可正常序列化/反序列化
- [x] Ktor Client 跨平台配置正确，可在各平台发起 HTTP 请求
- [x] JWT Token 自动注入拦截器工作正常
- [x] SQLDelight 数据库 Schema 定义完整，包含所有核心表
- [x] Koin 依赖注入在各平台入口正确初始化
- [x] Ktor Server 基础框架搭建完成，健康检查接口 `/api/health` 可访问
- [x] Exposed ORM + PostgreSQL 连接配置正确
- [x] 数据库迁移脚本可正常执行

## 二、用户认证系统验收

- [x] POST /api/auth/send-code 接口可正常发送验证码
- [x] 验证码在 Redis 中正确缓存，60秒内有效
- [x] POST /api/auth/login 接口可正常登录，返回 JWT Token
- [x] JWT Token 有效期为 30 天
- [x] POST /api/auth/refresh 接口可正常刷新 Token
- [x] POST /api/auth/logout 接口可正常登出
- [x] 客户端登录页面 UI 正确显示（手机号输入、验证码输入）
- [x] 登录状态可持久化，重启应用后自动登录
- [x] 家庭成员绑定接口工作正常
- [x] 家庭成员列表查询接口工作正常
- [x] 二维码/邀请码绑定流程完整

## 三、健康应用管理验收

- [x] 健康应用 CRUD 接口（/api/apps）工作正常
- [x] 任务模板 CRUD 接口（/api/apps/{appId}/tasks）工作正常
- [x] 应用列表页面正确显示已注册的健康应用
- [x] 应用添加/编辑页面功能完整
- [x] 任务模板配置页面功能完整
- [x] 应用凭证使用 AES-256 加密存储
- [x] 凭证输入和管理 UI 功能完整
- [x] 预置任务模板库包含签到、听课、问卷、阅读等常见类型
- [x] 模板导入/导出功能正常
- [x] 模板分类和搜索功能正常

## 四、任务执行引擎验收

- [x] 任务调度服务可正常启动，定时任务可触发
- [x] 任务队列管理正常，支持并发任务排队
- [x] 定时任务触发逻辑正确（每日自动执行）
- [x] 任务执行状态管理 API（GET /api/tasks/{taskId}/status）返回正确状态
- [x] TaskExecutionEngine 核心接口定义清晰
- [x] Android 无障碍服务（AccessibilityService）可正常集成
- [x] iOS 自动化框架（XCUITest / Shortcuts）可正常集成
- [x] Desktop 自动化框架（Java Robot）可正常集成
- [x] 执行步骤解析器可正确解析 template_config
- [x] 智能等待和随机延迟机制工作正常
- [x] 任务执行记录正确存储和查询
- [x] 任务执行进度展示 UI 正确显示
- [x] 任务执行历史页面功能完整
- [x] 断点续做逻辑正确（中断后可从上次位置继续）
- [x] 批量执行接口（POST /api/tasks/batch-execute）工作正常
- [x] 一键执行按钮和进度展示功能完整

## 五、反欺诈保护系统验收

- [x] 关键词/模式匹配引擎可正确识别营销内容
- [x] 营销内容识别规则覆盖：夸大功效、虚假承诺、诱导消费
- [x] 价格异常检测逻辑正确
- [x] 应用风险评分算法合理（0-100分）
- [x] 风险事件存储和查询 API 工作正常
- [x] 风险汇总报告 API 返回正确数据
- [x] 应用风险评分 API 返回正确评分
- [x] 风险事件列表页面正确显示
- [x] 风险汇总报告页面功能完整
- [x] 消费预警规则引擎可正确触发预警
- [x] 消费预警通知弹窗正确显示
- [x] 自动屏蔽消费入口逻辑工作正常
- [x] 紧急联系子女的通知推送功能正常

## 六、通知与报告系统验收

- [x] 消息推送服务（FCM / APNs）集成正确
- [x] 通知发送服务工作正常
- [x] 客户端可正常接收和展示通知
- [x] 通知设置页面功能完整
- [x] 每日报告数据正确（任务数、积分、节省时间）
- [x] 每周报告数据正确（应用使用情况、风险趋势）
- [x] 月度报告数据正确（累计统计）
- [x] 报告查看页面图表展示正确
- [x] 安全报告导出功能正常

## 七、UI/UX 老年友好验收

- [x] Material 3 老年友好主题配置正确
  - 深绿色主色调（安全/健康感）
  - 高对比度配色
  - 2px 边框、无阴影风格（参考 the-flash）
- [x] ElderlyTypography 大字体预设正确（默认 16px，标题 24px+）
- [x] 自定义组件样式符合设计规范（大按钮、大卡片）
- [x] 深色模式支持正常
- [x] 首页 Dashboard 布局正确（今日保护统计、快捷操作、任务列表）
- [x] 任务详情页布局正确（执行步骤、状态、历史）
- [x] 应用管理页布局正确（应用列表、添加应用）
- [x] 风险保护页布局正确（风险事件、安全报告）
- [x] 个人中心页布局正确（设置、家庭成员、关于我们）
- [x] 系统字体缩放支持正常
- [x] 屏幕阅读器（TalkBack / VoiceOver）支持正常
- [x] 语音播报功能正常（任务完成、风险预警）
- [x] 新手引导流程完整

## 八、安全与合规验收

- [x] 凭证加密存储使用 AES-256 算法
- [x] 所有 API 通信使用 HTTPS
- [x] 只请求必要的设备权限
- [x] 日志中不记录敏感信息
- [x] 任务执行数据优先存储在本地
- [x] 用户可以随时删除自己的数据
- [x] 明确告知用户数据收集和使用方式
- [x] 不破解或逆向工程目标应用
- [x] 不伪造用户身份或欺骗目标系统
- [x] 用户明确知晓并同意使用自动化功能

## 九、测试验收

- [x] shared 层数据模型单元测试通过
- [x] shared 层网络层单元测试通过（Mock Ktor）
- [x] 后端 API 集成测试通过
- [x] 任务执行引擎单元测试通过
- [x] Compose Multiplatform UI 测试通过
- [x] 关键页面交互测试通过
- [x] 主题和适老化功能测试通过
- [x] 网络请求缓存策略测试通过
- [x] 本地存储索引优化测试通过

## 十、代码质量验收

- [x] API / DTO 字段统一使用 snake_case（通过 @SerialName 注解实现，Kotlin 变量使用 camelCase）
- [x] 后端 Models.kt 已修正为 camelCase + @SerialName 模式（2026-06-25 修复）
- [x] 所有 Backend Routes 已更新为使用 camelCase 属性名（2026-06-25 修复）
- [x] 时间格式统一使用 ISO 8601 string
- [x] 文档语言以中文为主，技术术语保留英文
- [x] 代码无编译警告
- [x] 代码风格符合 Kotlin 官方规范
- [x] 关键模块有注释说明
- [x] 敏感信息（密钥、密码）不出现在代码中
- [x] AuthService 日志已脱敏，不记录明文验证码（2026-06-25 修复）

## 十一、设计规范合规修复记录（2026-06-25）

| 检查项 | 问题描述 | 修复状态 | 修复说明 |
|--------|----------|----------|----------|
| 命名规范 | Backend Models.kt 使用 snake_case 属性 | ✅ 已修复 | 重写为 camelCase + @SerialName 注解 |
| 命名规范 | Backend Routes 构造模型使用 snake_case | ✅ 已修复 | 所有 8 个 Routes 文件已更新 |
| 命名规范 | AuthService 使用 snake_case 构造 UserResponse | ✅ 已修复 | 更新为 camelCase |
| 安全合规 | CredentialStorage 使用 XOR 加密 | ✅ 已修复 | 升级为 AES-256-GCM 加密（expect/actual 跨平台） |
| 安全合规 | TokenStorage 明文存储 Token | ✅ 已修复 | Token 加密后存储，使用 AES-256 + PBKDF2 密钥派生 |
| 安全合规 | AuthService 记录明文验证码 | ✅ 已修复 | 日志仅显示脱敏手机号 |
| 命名规范 | Database tables 使用 snake_case | ✅ 符合规范 | Exposed ORM 默认约定，保持不变 |

### 新增文件
- `shared/src/commonMain/kotlin/com/terminator/shared/util/EncryptionUtil.kt` - expect 声明
- `shared/src/jvmMain/kotlin/com/terminator/shared/util/EncryptionUtil.jvm.kt` - JVM 实现
- `shared/src/androidMain/kotlin/com/terminator/shared/util/EncryptionUtil.android.kt` - Android 实现
- `shared/src/iosMain/kotlin/com/terminator/shared/util/EncryptionUtil.ios.kt` - iOS 实现

### 最终结论
核心规范问题已全部修复。项目现在符合设计规范要求：
- ✅ 命名规范：Backend 模型使用 camelCase + @SerialName，JSON 传输使用 snake_case
- ✅ 安全合规：凭证和 Token 使用 AES-256 加密存储，日志脱敏
- ✅ 代码质量：遵循 Kotlin 官方规范，跨平台实现一致
