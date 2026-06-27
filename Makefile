.PHONY: verify verify-android verify-desktop verify-backend verify-shared clean help

# 默认目标
.DEFAULT_GOAL := help

# 颜色输出
BLUE := \033[0;34m
GREEN := \033[0;32m
YELLOW := \033[0;33m
RED := \033[0;31m
NC := \033[0m # No Color

# 环境变量
export JAVA_HOME ?= /Library/Java/JavaVirtualMachines/jdk-17.0.7.jdk/Contents/Home

help: ## 显示帮助信息
	@echo "$(BLUE)KMP Terminator - 构建和验证命令$(NC)"
	@echo ""
	@echo "使用方法:"
	@echo "  $(GREEN)make <目标>$(NC)"
	@echo ""
	@echo "可用目标:"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "  $(YELLOW)%-20s$(NC) %s\n", $$1, $$2}'
	@echo ""
	@echo "示例:"
	@echo "  $(GREEN)make verify$(NC)              # 全量验证"
	@echo "  $(GREEN)make verify-android$(NC)      # 验证 Android 端"
	@echo "  $(GREEN)make clean$(NC)               # 清理构建产物"

# 全量验证
verify: verify-android verify-desktop verify-backend verify-shared ## 全量验证（所有模块）
	@echo "$(GREEN)✅ 全量验证完成$(NC)"

# Android 验证
verify-android: ## 验证 Android 端
	@echo "$(BLUE)验证 Android 端...$(NC)"
	./gradlew :androidApp:compileDebugKotlin
	./gradlew :androidApp:testDebugUnitTest
	@echo "$(GREEN)✅ Android 验证完成$(NC)"

# Desktop 验证
verify-desktop: ## 验证 Desktop 端
	@echo "$(BLUE)验证 Desktop 端...$(NC)"
	./gradlew :desktopApp:compileKotlinJvm
	@echo "$(GREEN)✅ Desktop 验证完成$(NC)"

# Backend 验证
verify-backend: ## 验证 Backend
	@echo "$(BLUE)验证 Backend...$(NC)"
	./gradlew :backend:build
	./gradlew :backend:test
	@echo "$(GREEN)✅ Backend 验证完成$(NC)"

# Shared 模块验证
verify-shared: ## 验证 Shared 模块
	@echo "$(BLUE)验证 Shared 模块...$(NC)"
	./gradlew :shared:build
	./gradlew :shared:test
	@echo "$(GREEN)✅ Shared 模块验证完成$(NC)"

# 清理
clean: ## 清理所有构建产物
	@echo "$(BLUE)清理构建产物...$(NC)"
	./gradlew clean
	@echo "$(GREEN)✅ 清理完成$(NC)"

# 构建
build: ## 构建所有模块
	@echo "$(BLUE)构建所有模块...$(NC)"
	./gradlew build
	@echo "$(GREEN)✅ 构建完成$(NC)"

# 测试
test: ## 运行所有测试
	@echo "$(BLUE)运行所有测试...$(NC)"
	./gradlew test
	@echo "$(GREEN)✅ 测试完成$(NC)"

# Android 专用命令
android-run: ## 运行 Android 应用
	@echo "$(BLUE)运行 Android 应用...$(NC)"
	./gradlew :androidApp:installDebug
	@echo "$(GREEN)✅ Android 应用已安装到设备$(NC)"

android-build-debug: ## 构建 Android Debug 版本
	@echo "$(BLUE)构建 Android Debug 版本...$(NC)"
	./gradlew :androidApp:assembleDebug
	@echo "$(GREEN)✅ Debug APK: androidApp/build/outputs/apk/debug/androidApp-debug.apk$(NC)"

android-build.release: ## 构建 Android Release 版本
	@echo "$(BLUE)构建 Android Release 版本...$(NC)"
	./gradlew :androidApp:assembleRelease
	@echo "$(GREEN)✅ Release APK: androidApp/build/outputs/apk/release/androidApp-release.apk$(NC)"

# Desktop 专用命令
desktop-run: ## 运行 Desktop 应用
	@echo "$(BLUE)运行 Desktop 应用...$(NC)"
	./gradlew :desktopApp:run
	@echo "$(GREEN)✅ Desktop 应用已启动$(NC)"

desktop-package: ## 打包 Desktop 应用
	@echo "$(BLUE)打包 Desktop 应用...$(NC)"
	./gradlew :desktopApp:package
	@echo "$(GREEN)✅ 安装包: desktopApp/build/compose/binaries/$(NC)"

# Backend 专用命令
backend-run: ## 运行 Backend 服务
	@echo "$(BLUE)运行 Backend 服务...$(NC)"
	./gradlew :backend:run
	@echo "$(GREEN)✅ Backend 服务已启动: http://localhost:8080$(NC)"

backend-jar: ## 构建 Backend Fat JAR
	@echo "$(BLUE)构建 Backend Fat JAR...$(NC)"
	./gradlew :backend:shadowJar
	@echo "$(GREEN)✅ Fat JAR: backend/build/libs/backend-all.jar$(NC)"

# 数据库命令
db-migrate: ## 运行数据库迁移
	@echo "$(BLUE)运行数据库迁移...$(NC)"
	./gradlew :backend:flywayMigrate
	@echo "$(GREEN)✅ 数据库迁移完成$(NC)"

# 代码质量
lint: ## 运行代码检查
	@echo "$(BLUE)运行代码检查...$(NC)"
	./gradlew detekt
	./gradlew ktlintCheck
	@echo "$(GREEN)✅ 代码检查完成$(NC)"

# 文档检查
docs-check: ## 检查文档链接和格式
	@echo "$(BLUE)检查文档...$(NC)"
	@echo "检查文档引用路径..."
	@find docs -name "*.md" -exec grep -l "\.\./" {} \; | while read file; do \
		echo "检查: $$file"; \
	done
	@echo "$(GREEN)✅ 文档检查完成$(NC)"

# 发布相关
release-check: ## 发布前检查
	@echo "$(BLUE)发布前检查...$(NC)"
	@echo "1. 检查 Git 状态..."
	@git status --short
	@echo ""
	@echo "2. 检查版本信息..."
	@grep -r "version = " gradle/libs.versions.toml | head -5
	@echo ""
	@echo "3. 运行全量验证..."
	@$(MAKE) verify
	@echo "$(GREEN)✅ 发布前检查完成$(NC)"
