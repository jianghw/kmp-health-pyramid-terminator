# Agent 协作契约

本文档定义了 AI Agent（如 Claude）在本项目中的协作规则和工作流程。

## 📋 核心原则

1. **文档优先**：所有决策以文档为准，不依赖记忆或假设
2. **验证驱动**：没有验证证据前，不声称完成
3. **最小改动**：只修改必要的文件，避免不必要的变更
4. **中文优先**：文档和注释使用中文，代码保留英文

## 📚 文档体系

### 文档层级（优先级从高到低）

1. **实时代码和配置**：运行时事实，最高优先级
2. **docs/ 下的 Current 文档**：当前权威规格
3. **各端 README**：工程操作说明
4. **归档文档**：仅用于追溯背景

### 文档边界

| 文档 | 职责 | 不承载 |
|------|------|--------|
| `README.md` | 项目入口、导航 | 详细技术规格 |
| `docs/README.md` | 文档总入口 | 具体实现细节 |
| `CONTRIBUTING.md` | 开发规范、提交约定 | 产品需求、API 细节 |
| `CLAUDE.md` | Agent 协作规则 | 技术栈版本、环境地址 |
| 各端 `README.md` | 工程操作说明 | 跨端通用说明 |

### 文档更新原则

- **时效性信息**：不写死在长期文档中，从实时来源读取
  - Git 状态：`git status --short --branch`
  - 依赖版本：`gradle/libs.versions.toml`
  - 环境地址：各端配置文件
- **冲突处理**：以高优先级文档为准，必要时更新低优先级文档
- **命名约定**：
  - 设计文档：`YYYY-MM-DD-<功能名>-design.md`
  - 实施计划：`YYYY-MM-DD-<功能名>.md`
  - 测试计划：`YYYY-MM-DD-<功能名>-test-plan.md`

## 🔧 工作流程

### 会话启动步骤

每次新会话开始时，执行以下检查：

```bash
# 1. 查看当前任务状态
cat .trae/specs/kmp-terminator-app/tasks.md

# 2. 查看 Git 状态
git status --short --branch
git log --oneline -5

# 3. 查看文档入口
cat docs/README.md
```

### 任务拆分规则

- **Epic → Story → Task**：单个 Task 应能在一个 session 内完成
- **每个 Task 必须有**：
  - 明确的输入和输出
  - 验收条件
  - 验证命令

### 代码改动规则

1. **遵循现有规范**：优先遵循现有模块边界、命名习惯和测试方式
2. **数据模型一致性**：字段调整必须确认全链路一致性（Server、Web、Mobile、存储、测试）
3. **命名约定**：
   - API / JSON / Kotlin DTO 字段：`snake_case`
   - 跨模块时间：ISO 8601 string
   - Kotlin 代码：遵循官方编码约定
4. **文档同步**：代码改动必须同步更新相关文档

### 验证规则

完成任何改动前，必须运行与改动风险匹配的验证：

| 改动类型 | 最低验证要求 |
|----------|--------------|
| 文档改动 | `git diff --check` + 检查引用路径 |
| 单端代码 | 对应端编译 + 测试 |
| 跨端代码 | 全量验证（`make verify`） |
| 数据库改动 | 迁移脚本测试 + 全量验证 |

**验证原则**：没有新鲜验证证据前，不声称完成、修复或通过。

## 🔀 Git 规则

### 禁止操作

- ❌ `git add .`：只暂存当前任务明确涉及的文件
- ❌ `git reset --hard`：除非用户明确要求
- ❌ `git push --force`：除非用户明确要求
- ❌ 删除分支：除非用户明确要求
- ❌ 覆盖远端历史：除非用户明确要求

### 分支策略

```
master (稳定版本)
  │
  ├── develop (开发主线)
  │     ├── feature/<功能描述>
  │     ├── fix/<问题描述>
  │     └── hotfix/<问题描述>
  │
  └── release/v<版本号>
```

### 提交规范

遵循 Conventional Commits 格式：

```
type(scope): description

[optional body]

[optional footer]
```

**类型（type）**：
- `feat`：新功能
- `fix`：修复 bug
- `refactor`：代码重构
- `docs`：文档更新
- `style`：代码格式
- `test`：测试相关
- `chore`：构建/工具/依赖
- `build`：构建系统
- `ci`：CI/CD 配置
- `perf`：性能优化
- `revert`：回滚

**作用域（scope）**：
- `android`、`desktop`、`backend`、`shared`
- `db`、`api`、`ui`、`theme`
- `deps`、`config`、`docs`

**描述规范**：
- 中文或英文均可，项目内保持一致
- 不超过 72 个字符
- 使用祈使句（如"添加"而非"添加了"）
- 不加句号

### 代码审查

- 至少 1 人审查
- 审查要点：功能正确性、代码质量、测试覆盖、文档更新、性能、安全性
- 使用 Squash and Merge 合并

## 🛡️ 安全规则

### 敏感信息

- ❌ 不提交真实 API key、密码、证书
- ❌ 不提交 `.env` 文件（包含敏感配置）
- ✅ 使用 `.env.example` 提供配置模板
- ✅ 使用占位符或测试凭证

### 签名配置

- Android 签名文件不入库
- 使用 `keystore.properties` 配置签名（不入库）
- 提供 `keystore.properties.example` 模板

### 数据库凭证

- 生产环境凭证不入库
- 使用环境变量或配置文件（不入库）
- 提供配置模板

## 📝 语言规则

### 文档语言

- **正文**：统一使用中文
- **代码、命令、API 名称**：保留英文
- **配置键名、路径**：保留英文
- **第三方专有名词**：保留英文

### 注释语言

- **公共 API**：使用中文注释
- **内部实现**：可使用英文
- **KDoc**：使用中文

### 更新策略

发现现有文档为英文时，修改该文件应同步中文化相关段落。

## 🎯 冲突处理

发生信息冲突时，按以下顺序处理：

1. 实时代码、配置和 Git 状态
2. `docs/README.md` 指向的 Current 文档
3. 各端 README
4. 归档文档和历史记录

如果冲突会影响实现选择，先说明冲突并更新共享记忆；无法安全判断时，向用户确认。

## 📖 参考资源

- [Conventional Commits](https://www.conventionalcommits.org/)
- [Kotlin 编码约定](https://kotlinlang.org/docs/coding-conventions.html)
- [Jetpack Compose 文档](https://developer.android.com/jetpack/compose)
- [Ktor 文档](https://ktor.io/docs/welcome.html)
- [SQLDelight 文档](https://cashapp.github.io/sqldelight/)

---

**本契约是 Agent 协作的权威指南，所有 AI Agent 必须遵循。**
