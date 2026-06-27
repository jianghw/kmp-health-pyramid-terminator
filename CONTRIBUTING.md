# 贡献指南

本文档定义了 KMP Terminator 项目的开发规范、提交约定和协作规则。

## 📋 目录

- [项目架构](#项目架构)
- [开发规范](#开发规范)
- [提交规范](#提交规范)
- [代码规范](#代码规范)
- [文档规范](#文档规范)
- [验证规则](#验证规则)
- [Git 工作流](#git-工作流)

---

## 🏗️ 项目架构

### 模块职责

| 模块 | 职责 | 技术栈 |
|------|------|--------|
| `androidApp` | Android 移动端应用 | Kotlin + Compose Multiplatform |
| `desktopApp` | Desktop 桌面端应用 | Kotlin + Compose Multiplatform |
| `backend` | 后端 API 服务 | Kotlin + Ktor + PostgreSQL |
| `shared` | 跨平台共享代码 | Kotlin Multiplatform |

### 模块边界

- **androidApp**：只包含 Android 特定的 UI 代码、平台集成
- **desktopApp**：只包含 Desktop 特定的 UI 代码、平台集成
- **backend**：只包含服务端逻辑、数据库访问、API 路由
- **shared**：包含所有平台共享的业务逻辑、数据模型、网络层、工具类

### 依赖方向

```
androidApp → shared
desktopApp → shared
backend → shared (可选)
```

**禁止**：
- `shared` 依赖 `androidApp`、`desktopApp` 或 `backend`
- `androidApp` 依赖 `desktopApp` 或 `backend`
- 循环依赖

---

## 📝 开发规范

### 语言规则

- **文档语言**：所有文档正文统一使用中文
- **代码语言**：代码、命令、API 名称、配置键名保留英文
- **注释语言**：公共 API 使用中文注释，内部实现可使用英文

### 命名约定

#### Kotlin 代码

- **包名**：全小写，使用反向域名（如 `com.terminator.shared.model`）
- **类名**：PascalCase（如 `UserProfile`、`TaskRepository`）
- **函数名**：camelCase（如 `getUserProfile`、`fetchTasks`）
- **变量名**：camelCase（如 `userName`、`taskList`）
- **常量名**：UPPER_SNAKE_CASE（如 `MAX_RETRY_COUNT`）
- **枚举值**：UPPER_SNAKE_CASE（如 `STATUS_ACTIVE`）

#### 数据模型

- **JSON 字段**：snake_case（如 `user_name`、`created_at`）
- **Kotlin 属性**：camelCase（如 `userName`、`createdAt`）
- **时间格式**：跨模块边界统一使用 ISO 8601 字符串

#### 数据库

- **表名**：snake_case（如 `user_profiles`、`task_records`）
- **列名**：snake_case（如 `user_id`、`created_at`）
- **索引名**：`idx_表名_列名`（如 `idx_users_email`）
- **外键名**：`fk_表名_关联表名`（如 `fk_tasks_user_id`）

### 代码组织

#### 包结构

```
com.terminator.{module}/
├── model/          # 数据模型
├── repository/     # 数据仓库
├── network/        # 网络层
├── di/             # 依赖注入
├── ui/             # UI 层（仅 androidApp/desktopApp）
│   ├── screens/    # 页面
│   ├── components/ # 可复用组件
│   └── theme/      # 主题配置
└── util/           # 工具类
```

#### 文件命名

- **数据模型**：`{ModelName}.kt`（如 `User.kt`、`Task.kt`）
- **Repository**：`{Entity}Repository.kt`（如 `UserRepository.kt`）
- **Screen**：`{ScreenName}Screen.kt`（如 `DashboardScreen.kt`）
- **Component**：`{ComponentName}.kt`（如 `TaskCard.kt`）
- **ViewModel**：`{ScreenName}ViewModel.kt`（如 `DashboardViewModel.kt`）

---

## 🔖 提交规范

### Conventional Commits 格式

```
type(scope): description

[optional body]

[optional footer]
```

### 提交类型 (type)

| 类型 | 说明 | 示例 |
|------|------|------|
| `feat` | 新功能 | `feat(android): 添加任务执行页面` |
| `fix` | 修复 bug | `fix(desktop): 修复导航栏样式问题` |
| `refactor` | 代码重构（不改变功能） | `refactor(shared): 重构网络请求逻辑` |
| `docs` | 文档更新 | `docs: 更新 README 安装说明` |
| `style` | 代码格式（不影响逻辑） | `style: 调整缩进和空行` |
| `test` | 添加或修改测试 | `test(android): 添加 Dashboard 单元测试` |
| `chore` | 构建/工具/依赖变更 | `chore: 升级 Kotlin 版本到 2.1.20` |
| `build` | 构建系统或外部依赖变更 | `build: 更新 Gradle wrapper 到 8.12` |
| `ci` | CI/CD 配置变更 | `ci: 添加 GitHub Actions 工作流` |
| `perf` | 性能优化 | `perf(shared): 优化数据库查询性能` |
| `revert` | 回滚提交 | `revert: 回滚 commit abc123` |

### 作用域 (scope)

| 作用域 | 说明 |
|--------|------|
| `android` | Android 应用相关 |
| `desktop` | Desktop 应用相关 |
| `backend` | 后端服务相关 |
| `shared` | 共享模块相关 |
| `db` | 数据库相关 |
| `api` | API 接口相关 |
| `ui` | UI 相关（跨平台） |
| `theme` | 主题相关 |
| `deps` | 依赖相关 |
| `config` | 配置相关 |
| `docs` | 文档相关 |

### 描述规范

- **语言**：中文或英文均可，项目内保持一致
- **长度**：不超过 72 个字符
- **时态**：使用祈使句（如"添加"而非"添加了"）
- **首字母**：中文无需大写，英文首字母小写
- **结尾**：不加句号

### 提交体 (body)

- **用途**：解释"为什么"而非"做什么"
- **格式**：每行不超过 72 个字符
- **空行**：与描述之间空一行

### 提交示例

#### 简单提交

```bash
feat(android): 添加消费预警页面

实现消费预警功能，包括预警列表展示和预警详情页面。
```

#### 带详细说明的提交

```bash
fix(desktop): 修复导航栏选中状态不更新的问题

之前切换子页面时，侧边导航栏的选中状态没有正确更新，
导致用户无法准确识别当前所在页面。

通过监听 subScreen 状态变化，同步更新导航栏选中状态。

Fixes #42
```

#### 跨模块提交

```bash
refactor(shared, android, desktop): 统一主题颜色定义

将分散在各端的颜色定义统一到 shared 模块的 Theme.kt，
确保 Android 和 Desktop 使用一致的品牌色系。

- 提取 BrandColors、HeaderColors、CardColors 到 shared
- 更新 androidApp 和 desktopApp 引用新的颜色定义
- 删除各端重复的颜色常量
```

#### 破坏性变更

```bash
feat(api)!: 重构用户认证接口

BREAKING CHANGE: 将 /api/login 拆分为 /api/auth/login 和 /api/auth/refresh，
客户端需要更新登录和刷新 token 的接口调用。

Migration guide:
- POST /api/login → POST /api/auth/login
- 新增 POST /api/auth/refresh
```

---

## 💻 代码规范

### Kotlin 代码风格

#### 遵循官方规范

- [Kotlin 官方编码约定](https://kotlinlang.org/docs/coding-conventions.html)
- [Android Kotlin 风格指南](https://developer.android.com/kotlin/style-guide)

#### 格式化

- **缩进**：4 个空格
- **行宽**：不超过 120 个字符
- **空行**：
  - 类声明之间空 1 行
  - 方法之间空 1 行
  - 逻辑块之间空 1 行

#### 导入

- 不使用通配符导入（`import xxx.*`）
- 按字母顺序排列
- 使用 IDE 自动优化导入功能

#### 注释

```kotlin
/**
 * 用户数据模型
 *
 * @property id 用户唯一标识
 * @property name 用户名称
 * @property email 用户邮箱
 */
data class User(
    val id: Long,
    val name: String,
    val email: String
)

// 好的注释：解释"为什么"
// 使用延迟加载避免初始化时的性能问题
private val cache by lazy { initializeCache() }

// 避免的注释：解释"做什么"
// 获取用户名称
fun getUserName(): String = user.name
```

### Compose 规范

#### 可组合函数

```kotlin
// ✅ 好的命名
@Composable
fun UserProfileCard(
    user: User,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // ...
}

// ❌ 避免的命名
@Composable
fun Card(user: User, onClick: () -> Unit) { ... }
```

#### 状态管理

```kotlin
// ✅ 状态提升
@Composable
fun TaskListScreen(
    viewModel: TaskListViewModel = koinViewModel()
) {
    val tasks by viewModel.tasks.collectAsState()
    TaskListContent(
        tasks = tasks,
        onTaskClick = viewModel::onTaskClick
    )
}

// ❌ 避免：在 Composable 中直接管理业务状态
@Composable
fun TaskListScreen() {
    var tasks by remember { mutableStateOf(emptyList<Task>()) }
    // ...
}
```

#### Modifier 链

```kotlin
// ✅ 正确的 Modifier 顺序
Box(
    modifier = Modifier
        .fillMaxWidth()           // 尺寸
        .padding(16.dp)           // 外边距
        .background(Color.White)  // 背景
        .clickable { onClick() }  // 交互
        .semantics { /* ... */ }  // 语义
)

// ❌ 避免：顺序混乱
Box(
    modifier = Modifier
        .clickable { onClick() }
        .fillMaxWidth()
        .background(Color.White)
        .padding(16.dp)
)
```

### 数据库规范

#### SQLDelight

```sql
-- ✅ 好的查询
SELECT user_id, user_name, email
FROM users
WHERE status = :status
ORDER BY created_at DESC
LIMIT :limit;

-- ❌ 避免：SELECT *
SELECT * FROM users WHERE status = :status;
```

#### 迁移

- 每个迁移文件命名：`{version}.sqm`（如 `1.sqm`、`2.sqm`）
- 迁移必须是幂等的（可重复执行）
- 包含回滚脚本

---

## 📚 文档规范

### 文档分层

| 优先级 | 文档 | 职责 |
|--------|------|------|
| 1（最高） | 实时代码、配置和 Git 状态 | 运行时事实 |
| 2 | `docs/` 下的 Current 文档 | 当前权威规格 |
| 3 | 各端 README | 工程操作说明 |
| 4 | 归档文档和历史记录 | 仅追溯背景 |

### 文档目录结构

```
docs/
├── README.md              # 文档总入口
├── features/              # 产品需求文档
│   └── 产品需求说明书.md
├── specs/                 # 技术规格（Current）
│   ├── 技术方案.md
│   ├── api-design.md
│   └── database-design.md
├── archive/               # 历史归档
│   ├── source/            # 原始输入资料
│   └── specs/             # 旧方案
└── guides/                # 开发指南
    ├── architecture.md
    └── best-practices.md
```

### README 规范

#### 根目录 README

- 项目简介（1-2 句话）
- 项目入口导航
- 目录概览
- 常用命令
- 文档边界说明

#### 各端 README

- 环境要求
- 常用命令
- 配置说明
- 构建和部署
- 验证方法

### 文档命名

- 文件名使用中文或英文短横线分隔（kebab-case）
- 设计文档：`YYYY-MM-DD-<功能名>-design.md`
- 实施计划：`YYYY-MM-DD-<功能名>.md`
- 测试计划：`YYYY-MM-DD-<功能名>-test-plan.md`

---

## ✅ 验证规则

### 全量验证

```bash
# 编译所有模块
./gradlew build

# 运行所有测试
./gradlew test

# 代码检查（如果配置了 ktlint/detekt）
./gradlew ktlintCheck
./gradlew detekt
```

### 各端验证

#### Android

```bash
# 编译
./gradlew :androidApp:assembleDebug

# 测试
./gradlew :androidApp:test

# Lint
./gradlew :androidApp:lint
```

#### Desktop

```bash
# 编译
./gradlew :desktopApp:compileKotlinJvm

# 运行
./gradlew :desktopApp:run
```

#### Backend

```bash
# 编译
./gradlew :backend:build

# 测试
./gradlew :backend:test

# 运行
./gradlew :backend:run
```

#### Shared

```bash
# 编译
./gradlew :shared:build

# 测试
./gradlew :shared:test
```

### 验证时机

- **提交前**：至少运行对应模块的编译和测试
- **合并前**：运行全量验证
- **发布前**：运行全量验证 + 集成测试

### 验证原则

- **没有新鲜验证证据前，不要声称完成、修复或通过**
- 文档改动：至少运行 `git diff --check` 并检查引用路径
- 代码改动：运行对应模块的编译、测试
- 跨模块改动：运行全量验证

---

## 🔀 Git 工作流

### 分支策略

```
master (稳定版本)
  │
  ├── develop (开发主线)
  │     │
  │     ├── feature/user-auth (功能分支)
  │     ├── feature/task-execution
  │     │
  │     └── hotfix/critical-bug (热修复)
  │
  └── release/v1.0.0 (发布分支)
```

### 分支命名

- **功能分支**：`feature/<功能描述>`（如 `feature/user-auth`）
- **修复分支**：`fix/<问题描述>`（如 `fix/navigation-bug`）
- **热修复**：`hotfix/<问题描述>`（如 `hotfix/crash-on-startup`）
- **发布分支**：`release/v<版本号>`（如 `release/v1.0.0`）

### 工作流程

1. **从 develop 创建功能分支**
   ```bash
   git checkout develop
   git pull origin develop
   git checkout -b feature/my-feature
   ```

2. **在功能分支上开发**
   - 小步提交，频繁提交
   - 遵循提交规范
   - 定期从 develop 合并最新代码

3. **提交 Pull Request**
   - 推送到远端
   - 创建 PR 到 develop
   - 等待代码审查
   - 通过 CI 检查

4. **合并到 develop**
   - 使用 Squash and Merge（推荐）
   - 或删除分支后合并

5. **发布到 master**
   - 从 develop 创建 release 分支
   - 测试和修复 bug
   - 合并到 master 和 develop
   - 打 tag

### Git 规则

- **禁止 `git add .`**：只暂存当前任务明确涉及的文件
- **不自动执行破坏性操作**：
  - `git reset --hard`
  - `git push --force`
  - 删除分支
  - 覆盖远端历史
- **不直接改 master**：普通功能开发在功能分支上进行
- **及时删除已合并的分支**：保持仓库整洁

### 代码审查

#### 审查要点

- **功能正确性**：代码是否实现了预期功能
- **代码质量**：是否遵循编码规范
- **测试覆盖**：是否有足够的测试
- **文档更新**：是否更新了相关文档
- **性能影响**：是否有性能问题
- **安全性**：是否有安全隐患

#### 审查流程

1. 提交 PR 后，至少需要 1 人审查
2. 审查者提出修改建议
3. 作者修改后重新提交
4. 审查通过后，由作者合并

---

## 📖 参考资源

- [Conventional Commits](https://www.conventionalcommits.org/)
- [Kotlin 编码约定](https://kotlinlang.org/docs/coding-conventions.html)
- [Jetpack Compose 文档](https://developer.android.com/jetpack/compose)
- [Ktor 文档](https://ktor.io/docs/welcome.html)
- [SQLDelight 文档](https://cashapp.github.io/sqldelight/)

---

**感谢你的贡献！** 🎉
