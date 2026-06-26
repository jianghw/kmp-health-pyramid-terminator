# KMP Terminator - 大健康薅羊毛终结者

## 背景与动机

### 问题
当前大健康领域存在大量以"听课打卡"、"签到任务"、"积分兑换"为名的营销应用，通过微信小程序、独立APP等渠道向老年人推送需要长时间盯屏才能完成的任务。这些任务通常包含大量诱导消费内容、虚假宣传和隐性推销，老年人因操作不熟练而被迫长时间沉浸在其中，容易被"洗脑"式营销影响消费决策。

### 目标
构建一个跨平台（Android、iOS、Desktop、Backend）的智能任务自动化工具，帮助老年用户自动完成各大健康应用中的签到、听课、任务打卡等重复性操作，让老人不必长时间盯屏，减少被营销内容洗脑的风险。定位为**社会公益型工具**——"薅羊毛不被骗，终结灰色产业链"。

---

## 设计规范参考（来自 the-flash 项目）

### 架构规范
- **前后端分离**：客户端只负责本地缓存和UI，后端统一管理业务逻辑和数据
- **数据同步策略**：开始任务→下载模板到本地→本地执行→完成时一次性同步
- **本地缓存优先**：网络中断时已执行的步骤不丢失，恢复后继续
- **API 设计统一**：RESTful + JSON，统一响应格式 `{ success, message, data }`
- **认证方式**：JWT Bearer Token，支持多种登录方式

### 代码规范
- **API / DTO 字段**：统一使用 `snake_case`
- **时间格式**：跨模块边界统一使用 ISO 8601 string
- **文档语言**：中文为主，技术术语保留英文
- **验证规则**：每次改动前必须做与风险匹配的验证
- **提交规范**：只暂存当前任务明确涉及的文件

### UI/UX 规范
- **设计风格**：参考 the-flash 的 E-Ink 风格——黑白为主、高对比度、无阴影
- **字体**：Outfit / Inter + 系统回退字体
- **圆角**：统一 2px 极小圆角
- **边框**：2px 实线黑色边框，无阴影
- **按钮**：无大写转换（textTransform: none），700 字重
- **卡片**：2px 黑色边框，无阴影，2px 圆角
- **老年适配**：大字体（16px+）、高对比度、简洁布局、大点击区域

---

## 技术架构

### 整体架构
```
┌─────────────────────────────────────────────────────────────┐
│                    共享业务层 (KMP commonMain)                 │
│         Kotlin Multiplatform - 共享数据模型/网络/存储           │
│    ┌─────────────────────────────────────────────────┐     │
│    │ Ktor Client │ Kotlinx Serialization │ SQLDelight│     │
│    └─────────────────────────────────────────────────┘     │
├────────────┬────────────────┬────────────────┬──────────────┤
│  Android   │      iOS       │    Desktop     │   Backend    │
│  Compose   │   Compose iOS  │  Compose Desk  │   Ktor       │
│  Material3 │   UIKit桥接    │  Material3     │   Exposed    │
└────────────┴────────────────┴────────────────┴──────────────┘
                          │ HTTPS
                          ▼
              ┌───────────────────────────┐
              │   PostgreSQL / Redis      │
              │   消息队列 (RabbitMQ)      │
              └───────────────────────────┘
```

### 技术选型

#### 共享层 (commonMain)
| 模块 | 技术 | 说明 |
|------|------|------|
| 网络 | Ktor Client | 跨平台 HTTP 客户端 |
| 序列化 | Kotlinx Serialization | JSON 编解码 |
| 本地存储 | SQLDelight | 跨平台 SQLite |
| 依赖注入 | Koin | 轻量级跨平台 DI |
| 协程 | Kotlin Coroutines | 异步编程 |
| 日期 | Kotlinx Datetime | 跨平台日期时间 |

#### Android / Desktop (Compose Multiplatform)
| 模块 | 技术 | 说明 |
|------|------|------|
| UI 框架 | Compose Multiplatform | 声明式 UI |
| 设计系统 | Material 3 | 自定义老年友好主题 |
| 导航 | Compose Navigation | 跨平台导航 |
| 图片加载 | Coil Compose | 图片加载和缓存 |
| 状态管理 | Kotlin StateFlow | 响应式状态 |

#### iOS
| 模块 | 技术 | 说明 |
|------|------|------|
| UI 框架 | Compose iOS / SwiftUI 桥接 | 混合方案 |
| 原生桥接 | Kotlin/Native | 访问 iOS 原生 API |

#### Backend
| 模块 | 技术 | 说明 |
|------|------|------|
| 框架 | Ktor Server | Kotlin 原生后端框架 |
| 数据库 | Exposed ORM + PostgreSQL | 关系型数据存储 |
| 缓存 | Redis | 验证码/会话缓存 |
| 鉴权 | JWT | Bearer Token 认证 |
| 任务调度 | Quartz / 自定义调度器 | 定时任务执行 |
| 消息队列 | RabbitMQ | 任务异步处理 |

---

## 功能模块设计

### 模块1：用户管理

#### 1.1 用户注册与登录
- **手机号 + 验证码登录**（参考 the-flash 的认证流程）
- **子女代注册**：子女可以为父母创建账号并绑定
- **JWT 鉴权**：Token 有效期 30 天，支持刷新
- **多设备同步**：同一账号可在多设备登录

#### 1.2 家庭成员绑定
- 子女账号可以绑定多个老人账号
- 子女可以远程查看老人的任务完成情况
- 子女可以远程管理任务配置

### 模块2：健康应用管理

#### 2.1 应用配置
- **应用注册**：支持注册多个健康类应用（微信小程序、独立APP等）
- **任务模板**：每个应用的任务类型以模板形式配置
  - 签到任务
  - 听课任务（音视频播放）
  - 问卷任务
  - 阅读任务
  - 积分兑换任务
- **凭证管理**：安全存储各应用的登录凭证（加密存储）

#### 2.2 任务模板配置
```json
{
  "app_id": "wechat_miniprogram_health_100",
  "task_type": "course_listen",
  "template_config": {
    "steps": [
      {
        "step_no": 1,
        "action": "open_miniprogram",
        "target": "gh_xxxxx",
        "params": { "page": "pages/course/detail" }
      },
      {
        "step_no": 2,
        "action": "play_audio",
        "duration_minutes": 15,
        "skip_if_listened": true
      },
      {
        "step_no": 3,
        "action": "click_button",
        "target": "完成学习",
        "wait_seconds": 3
      }
    ]
  }
}
```

### 模块3：任务自动执行引擎

#### 3.1 任务调度
- **定时任务**：每日自动执行签到、听课等任务
- **手动触发**：用户可以手动触发单个任务
- **批量执行**：支持一键执行所有待完成任务
- **执行队列**：任务排队执行，避免冲突

#### 3.2 执行策略
- **模拟操作**：通过无障碍服务(Android)、自动化框架执行UI操作
- **音视频处理**：自动播放课程音视频，支持静音播放
- **智能等待**：根据页面加载速度动态调整等待时间
- **异常重试**：失败自动重试，最多 3 次
- **模拟真实性**：随机操作间隔，模拟人类行为模式

#### 3.3 执行结果
- **成功**：任务自动完成，记录完成时间和获得的积分
- **部分完成**：记录已完成的步骤，支持断点续做
- **失败**：记录失败原因，支持人工介入

### 模块4：反欺诈保护

#### 4.1 内容分析
- **营销内容识别**：自动识别课程中的诱导消费内容
- **虚假宣传检测**：标记夸大功效、虚假承诺的内容
- **价格异常监测**：监测积分兑换商品的异常高价
- **风险评分**：为每个健康应用计算风险评分

#### 4.2 保护措施
- **消费预警**：检测到消费诱导时发送预警通知
- **自动屏蔽**：可配置自动屏蔽高风险应用的消费入口
- **安全报告**：定期生成老人使用情况和风险报告
- **紧急联系**：高风险操作时自动通知子女

### 模块5：通知与报告

#### 5.1 通知系统
- **任务完成通知**：每日任务完成情况汇总
- **风险预警通知**：检测到营销诱导时实时通知
- **异常提醒**：应用登录异常、任务失败等
- **子女通知**：重要事件同步通知子女

#### 5.2 数据报告
- **每日报告**：今日自动完成的任务数、获得的积分
- **每周报告**：各应用使用情况、风险评分趋势
- **月度报告**：累计节省时间、避免的营销风险
- **安全报告**：识别的虚假内容、屏蔽的消费诱导

### 模块6：设置与个性化

#### 6.1 基础设置
- **通知偏好**：通知频率、通知方式
- **执行时间**：设置自动执行的时间窗口
- **音量控制**：自动播放音视频时的音量设置
- **深色模式**：大字体、高对比度的显示选项

#### 6.2 高级设置
- **应用优先级**：设置各健康应用的任务优先级
- **执行规则**：自定义任务执行规则和条件
- **白名单/黑名单**：管理允许/禁止自动执行的应用
- **数据同步频率**：控制与服务器的数据同步频率

---

## UI/UX 设计规范

### 设计原则
1. **大字体优先**：默认字体 16px，标题 24px+，便于老年人阅读
2. **高对比度**：黑白为主，关键信息使用高亮色（绿色/红色状态）
3. **简洁布局**：每个页面只展示核心信息，避免信息过载
4. **大点击区域**：按钮最小高度 48px，间距充足
5. **明确反馈**：操作后有明确的视觉/文字反馈
6. **无障碍支持**：支持系统字体缩放、屏幕阅读器

### 主题配置（参考 the-flash E-Ink 风格）
```kotlin
// 老年友好主题 - 比 the-flash 更温暖的色调
val ElderlyFriendlyLightColorScheme = lightColorScheme(
    primary = Color(0xFF1B5E20),        // 深绿 - 安全/健康
    onPrimary = Color.White,
    primaryContainer = Color(0xFFA5D6A7), // 浅绿
    secondary = Color(0xFF37474F),       // 深灰蓝
    background = Color(0xFFFAFAFA),      // 浅灰白
    surface = Color.White,
    error = Color(0xFFC62828),           // 深红
    success = Color(0xFF2E7D32),         // 深绿
    warning = Color(0xFFEF6C00),         // 橙色
)

// 字体大小预设
object ElderlyTypography {
    val displayLarge = 32.sp    // 大标题
    val displayMedium = 28.sp   // 中标题
    val headlineLarge = 24.sp   // 页面标题
    val headlineMedium = 20.sp  // 区域标题
    val titleLarge = 18.sp      // 卡片标题
    val bodyLarge = 16.sp       // 正文（默认）
    val bodyMedium = 14.sp      // 辅助文字
    val labelLarge = 16.sp      // 按钮文字
}
```

### 主要页面布局

#### 首页（Dashboard）
```
┌─────────────────────────────────────┐
│  🛡️ 薅羊毛终结者              [设置]│
├─────────────────────────────────────┤
│  今日保护                           │
│  ┌─────────────────────────────────┐│
│  │ ✅ 自动完成 5 个任务            ││
│  │ ⏱️ 节省 2 小时盯屏时间          ││
│  │ 🛡️ 拦截 3 次营销诱导            ││
│  └─────────────────────────────────┘│
├─────────────────────────────────────┤
│  快捷操作                           │
│  ┌──────────┐  ┌──────────┐        │
│  │ 🚀 一键  │  │ 📊 查看  │        │
│  │ 执行任务 │  │ 报告     │        │
│  └──────────┘  └──────────┘        │
├─────────────────────────────────────┤
│  应用任务列表                       │
│  ┌─────────────────────────────────┐│
│  │ 📱 微信健康打卡    ✅ 已完成    ││
│  │ 📱 养生课堂       ⏳ 执行中...  ││
│  │ 📱 健康积分       ⏰ 待执行     ││
│  └─────────────────────────────────┘│
├─────────────────────────────────────┤
│  [首页]   [任务]   [保护]   [我的]  │
└─────────────────────────────────────┘
```

#### 任务详情页
```
┌─────────────────────────────────────┐
│  ← 任务详情                         │
├─────────────────────────────────────┤
│  📱 微信健康打卡 - 每日签到         │
│  状态：✅ 已完成                     │
│  完成时间：2026-06-25 08:30         │
│  获得积分：+10                      │
├─────────────────────────────────────┤
│  执行步骤                           │
│  1. 打开小程序          ✅          │
│  2. 点击签到按钮        ✅          │
│  3. 确认签到成功        ✅          │
├─────────────────────────────────────┤
│  [重新执行]        [查看历史]       │
└─────────────────────────────────────┘
```

---

## 数据模型设计

### 核心实体

#### 用户 (users)
| 字段 | 类型 | 说明 |
|------|------|------|
| user_id | Long | 主键 |
| phone | String | 手机号，全局唯一 |
| nickname | String | 昵称 |
| role | Enum | elder / family_member / admin |
| status | Enum | enabled / disabled |
| created_at | ISO 8601 | 创建时间 |
| updated_at | ISO 8601 | 更新时间 |

#### 家庭绑定 (family_bindings)
| 字段 | 类型 | 说明 |
|------|------|------|
| binding_id | Long | 主键 |
| elder_user_id | Long | 老人用户ID |
| family_user_id | Long | 子女用户ID |
| relationship | String | 关系（子女/配偶/其他） |
| permissions | JSON | 权限配置 |
| created_at | ISO 8601 | 创建时间 |

#### 健康应用 (health_apps)
| 字段 | 类型 | 说明 |
|------|------|------|
| app_id | Long | 主键 |
| app_name | String | 应用名称 |
| app_type | Enum | wechat_miniprogram / independent_app / h5 |
| app_icon | String | 应用图标URL |
| risk_score | Int | 风险评分 0-100 |
| status | Enum | enabled / disabled |
| config | JSON | 应用配置 |
| created_at | ISO 8601 | 创建时间 |

#### 任务模板 (task_templates)
| 字段 | 类型 | 说明 |
|------|------|------|
| template_id | Long | 主键 |
| app_id | Long | 关联应用 |
| task_name | String | 任务名称 |
| task_type | Enum | sign_in / course_listen / survey / reading / exchange |
| template_config | JSON | 任务执行配置 |
| estimated_minutes | Int | 预计耗时（分钟） |
| reward_points | Int | 奖励积分 |
| status | Enum | enabled / disabled |
| created_at | ISO 8601 | 创建时间 |

#### 任务执行记录 (task_executions)
| 字段 | 类型 | 说明 |
|------|------|------|
| execution_id | Long | 主键 |
| user_id | Long | 用户ID |
| template_id | Long | 任务模板ID |
| status | Enum | pending / running / completed / failed / partial |
| started_at | ISO 8601 | 开始时间 |
| completed_at | ISO 8601 | 完成时间 |
| result_data | JSON | 执行结果数据 |
| error_message | String | 错误信息 |
| retry_count | Int | 重试次数 |
| created_at | ISO 8601 | 创建时间 |

#### 风险事件 (risk_events)
| 字段 | 类型 | 说明 |
|------|------|------|
| event_id | Long | 主键 |
| user_id | Long | 用户ID |
| app_id | Long | 关联应用 |
| event_type | Enum | marketing_inducement / false_claim / price_anomaly / consumption_trigger |
| severity | Enum | low / medium / high / critical |
| description | String | 事件描述 |
| evidence | JSON | 证据数据 |
| action_taken | Enum | none / warned / blocked / notified_family |
| created_at | ISO 8601 | 创建时间 |

---

## API 设计规范

### 通用规范（参考 the-flash）
- **Base URL**: `http://localhost:8080/api` (开发) / `https://api.kmp-terminator.com/api` (生产)
- **认证**: JWT Bearer Token
- **响应格式**:
```json
{
  "success": true,
  "message": "操作成功",
  "data": {}
}
```
- **错误响应**:
```json
{
  "success": false,
  "message": "错误信息",
  "code": "ERROR_CODE"
}
```

### 主要 API 模块

#### 认证模块
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/auth/send-code` | POST | 发送验证码 |
| `/api/auth/login` | POST | 验证码登录 |
| `/api/auth/refresh` | POST | 刷新Token |
| `/api/auth/logout` | POST | 登出 |

#### 任务管理模块
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/apps` | GET | 获取已注册应用列表 |
| `/api/apps/{appId}/tasks` | GET | 获取应用任务列表 |
| `/api/tasks/execute` | POST | 手动触发任务执行 |
| `/api/tasks/batch-execute` | POST | 批量执行任务 |
| `/api/tasks/{taskId}/status` | GET | 查询任务执行状态 |
| `/api/tasks/history` | GET | 任务执行历史 |

#### 风险管理模块
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/risks/events` | GET | 风险事件列表 |
| `/api/risks/summary` | GET | 风险汇总报告 |
| `/api/risks/apps/{appId}/score` | GET | 应用风险评分 |

#### 家庭管理模块
| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/family/bind` | POST | 绑定家庭成员 |
| `/api/family/members` | GET | 获取家庭成员列表 |
| `/api/family/{elderId}/tasks` | GET | 查看老人任务执行情况 |
| `/api/family/{elderId}/risks` | GET | 查看老人风险事件 |

---

## 安全与合规

### 数据安全
- **凭证加密**：健康应用登录凭证使用 AES-256 加密存储
- **传输加密**：所有 API 通信使用 HTTPS
- **最小权限**：只请求必要的设备权限
- **数据脱敏**：日志中不记录敏感信息

### 隐私保护
- **本地优先**：任务执行数据优先存储在本地
- **数据最小化**：只收集必要的用户数据
- **用户控制**：用户可以随时删除自己的数据
- **透明告知**：明确告知用户数据收集和使用方式

### 合规要求
- **不破解**：不破解或逆向工程目标应用
- **不欺诈**：不伪造用户身份或欺骗目标系统
- **自动化声明**：向目标应用声明自动化访问（如适用）
- **用户知情**：用户明确知晓并同意使用自动化功能

---

## 开发阶段规划

### Phase 1: 基础框架
- KMP 项目初始化
- 共享数据模型和网络层
- 基础 UI 框架和主题系统
- 用户认证流程

### Phase 2: 核心功能
- 健康应用管理
- 任务模板配置
- 任务执行引擎
- 执行结果管理

### Phase 3: 保护功能
- 风险内容识别
- 消费预警系统
- 安全报告生成
- 家庭成员通知

### Phase 4: 优化完善
- UI/UX 优化（老年友好适配）
- 性能优化
- 测试覆盖
- 文档完善

---

## 影响范围

### 受影响的规范
- 需要新建完整的 KMP 项目结构
- 需要设计跨平台共享的数据模型
- 需要实现 Android、iOS、Desktop 三端 UI
- 需要实现 Ktor 后端服务

### 关键文件
- `shared/` - KMP 共享模块
- `androidApp/` - Android 应用
- `iosApp/` - iOS 应用
- `desktopApp/` - Desktop 应用
- `backend/` - Ktor 后端服务
