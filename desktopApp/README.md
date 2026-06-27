# Desktop 应用

基于 Kotlin 和 Compose Multiplatform 的桌面端应用，支持 macOS、Windows 和 Linux。

## 环境要求

- JDK 17+
- Gradle 8.12
- Skiko 0.8.18（原生渲染引擎）

## 快速开始

### 1. 配置环境

```bash
# 设置 JAVA_HOME
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.0.7.jdk/Contents/Home

# 验证 Java 版本
java -version
```

### 2. 运行应用

```bash
# 方式一：使用 Gradle
./gradlew :desktopApp:run

# 方式二：使用 Makefile
make desktop-run
```

### 3. 打包分发

```bash
# 打包为 DMG（macOS）
./gradlew :desktopApp:packageDmg

# 打包为 MSI（Windows）
./gradlew :desktopApp:packageMsi

# 打包为 DEB（Linux）
./gradlew :desktopApp:packageDeb

# 或使用 Makefile
make desktop-package
```

## 项目结构

```
desktopApp/
├── src/jvmMain/
│   └── kotlin/com/terminator/desktop/
│       ├── Main.kt                   # 应用入口
│       ├── App.kt                    # 导航和主题
│       └── ui/
│           ├── screens/              # 页面
│           └── theme/                # 主题配置
└── build.gradle.kts
```

## 常用命令

```bash
# 编译
./gradlew :desktopApp:compileKotlinJvm

# 运行
./gradlew :desktopApp:run

# 打包
./gradlew :desktopApp:package
```

## 平台支持

| 平台 | 格式 | 架构 |
|------|------|------|
| macOS | DMG | x64, arm64 |
| Windows | MSI | x64 |
| Linux | DEB | x64 |

## Skiko 配置

Desktop 应用使用 Skiko 作为原生渲染引擎。根据目标平台选择对应的依赖：

```kotlin
// macOS arm64
implementation("org.jetbrains.skiko:skiko-awt-runtime-macos-arm64:0.8.18")

// macOS x64
implementation("org.jetbrains.skiko:skiko-awt-runtime-macos-x64:0.8.18")

// Windows x64
implementation("org.jetbrains.skiko:skiko-awt-runtime-windows-x64:0.8.18")

// Linux x64
implementation("org.jetbrains.skiko:skiko-awt-runtime-linux-x64:0.8.18")
```

## 验证

```bash
# 使用 Makefile
make verify-desktop

# 或手动验证
./gradlew :desktopApp:compileKotlinJvm
```

## 依赖说明

- `shared`：跨平台共享模块
- Compose Multiplatform：UI 框架
- Skiko：原生渲染引擎
- Koin：依赖注入
- Coil：图片加载

## 注意事项

1. **Java 版本**：必须使用 JDK 17+
2. **Skiko 依赖**：根据目标平台选择正确的 Skiko 依赖
3. **窗口大小**：默认 1200x800，可在 Main.kt 中调整
4. **打包路径**：`desktopApp/build/compose/binaries/`

## 相关文档

- [项目 README](../README.md)
- [贡献指南](../CONTRIBUTING.md)
- [文档入口](../docs/README.md)
