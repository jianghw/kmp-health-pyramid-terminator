# KMP Terminator 文档入口

本文档是 `docs/` 下项目文档的总入口。开发、验收和需求对齐时，优先阅读 Current 文档；历史方案和过程记录仅用于追溯背景。

## Current

| 文档 | 路径 | 说明 |
|------|------|------|
| 产品需求说明书 | `features/产品需求说明书.md` | 唯一产品需求事实来源 |
| 技术方案 | `specs/技术方案.md` | 当前系统架构、端侧形态、核心实现方案 |
| API 接口设计 | `specs/api-design.md` | 前后端接口契约、请求响应、鉴权方式 |
| 数据库设计 | `specs/database-design.md` | 数据模型、表结构、索引策略 |
| 主题设计规范 | `specs/theme-design.md` | 品牌色系统、页面 Header 配色、组件样式 |
| 端到端验收清单 | `specs/e2e-acceptance-checklist.md` | 各功能模块验收标准 |
| Android 工程说明 | `../androidApp/README.md` | Android 端环境、开发、构建和运行说明 |
| Desktop 工程说明 | `../desktopApp/README.md` | Desktop 端环境、开发、构建和运行说明 |
| Backend 工程说明 | `../backend/README.md` | 后端环境、开发、数据库配置和运行说明 |
| Shared 模块说明 | `../shared/README.md` | 共享模块结构、跨平台实现说明 |

## Reference

| 文档 | 路径 | 说明 |
|------|------|------|
| 技术规格索引 | `specs/README.md` | 当前技术文档导航 |
| 开发指南 | `guides/development-guide.md` | 开发流程、最佳实践 |
| 代码审查指南 | `guides/code-review-guide.md` | 代码审查标准和流程 |

## Archive

| 文档 | 路径 | 说明 |
|------|------|------|
| 原始需求输入 | `archive/source/原始需求文档.md` | 项目早期输入资料 |
| 历史方案 | `archive/specs/` | 历史实施方案，已被当前方案取代 |

## Process

| 目录 | 说明 |
|------|------|
| `process/designs/` | 历史设计草稿和功能设计过程记录 |
| `process/plans/` | 历史实施计划和任务拆解记录 |

`process/` 下的文档不作为当前规格依据。若过程记录与 Current 文档冲突，以 Current 文档为准。

## 文档更新原则

1. **时效性信息**：不写死在长期文档中，从实时来源读取
2. **冲突处理**：以高优先级文档为准，必要时更新低优先级文档
3. **命名约定**：
   - 设计文档：`YYYY-MM-DD-<功能名>-design.md`
   - 实施计划：`YYYY-MM-DD-<功能名>.md`
   - 测试计划：`YYYY-MM-DD-<功能名>-test-plan.md`

## 文档层级

| 优先级 | 文档 | 职责 |
|--------|------|------|
| 1（最高） | 实时代码、配置和 Git 状态 | 运行时事实 |
| 2 | `docs/` 下的 Current 文档 | 当前权威规格 |
| 3 | 各端 README | 工程操作说明 |
| 4 | 归档文档和历史记录 | 仅追溯背景 |

---

**本文档是项目文档的权威入口，所有文档导航以此为准。**
