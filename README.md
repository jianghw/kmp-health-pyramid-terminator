# KMP Terminator - 薅羊毛终结者

一个基于 Kotlin Multiplatform 的跨平台应用，支持 Android、Desktop（JVM）和 Backend 服务。帮助用户自动化管理各类健康类应用的签到、任务执行和风险控制。

## 📋 目录

- [项目概述](#项目概述)
- [技术栈](#技术栈)
- [项目结构](#项目结构)
- [环境要求](#环境要求)
- [快速开始](#快速开始)
- [各模块启动指南](#各模块启动指南)
- [构建与部署](#构建与部署)
- [配置说明](#配置说明)

---

## 🎯 项目概述

KMP Terminator 是一个全栈 Kotlin Multiplatform 项目，包含：

- **Android 应用**：移动端应用，提供完整的任务管理界面
- **Desktop 应用**：桌面端应用，使用 Compose Multiplatform 构建
- **Backend 服务**：基于 Ktor 的后端 API 服务，提供数据同步和认证
- **Shared 模块**：跨平台共享的业务逻辑、数据模型和网络层

### 主要功能

- 📱 多应用任务管理（微信健康、支付宝运动、京东健康等）
- 🤖 AI 智能答题（支持 OpenAI、通义千问、智谱 AI、文心一言）
- 🔐 凭证安全管理
- 📊 数据报告与统计
- ⚠️ 消费预警与风险控制
- 🎨 清新浅绿主题设计

---

## 🛠 技术栈

### 核心技术

| 技术 | 版本 | 用途 |
|------|------|------|
| Kotlin | 2.1.20 | 主要开发语言 |
| Compose Multiplatform | 1.7.3 | 跨平台 UI 框架 |
| Ktor | 3.0.3 | HTTP 客户端/服务端 |
| SQLDelight | 2.0.2 | 跨平台数据库 |
| Koin | 4.0.2 | 依赖注入 |
| Gradle | 8.12 | 构建工具 |

### Android 特定

| 技术 | 版本 | 用途 |
|------|------|------|
| Android SDK | 35 | 编译版本 |
| Min SDK | 25 | 最低支持版本 |
| Coil | 3.0.4 | 图片加载 |
| AndroidX Activity | 1.9.3 | Activity 支持 |
| AndroidX Lifecycle | 2.8.7 | 生命周期管理 |

### Desktop 特定

| 技术 | 版本 | 用途 |
|------|------|------|
| Skiko | 0.8.18 | 原生渲染引擎 |
| Compose Desktop | 1.7.3 | 桌面 UI 框架 |

### Backend 特定

| 技术 | 版本 | 用途 |
|------|------|------|
| Ktor Server | 3.0.3 | Web 服务框架 |
| Netty | - | HTTP 服务器 |
| Exposed | 0.56.0 | 数据库 ORM |
| PostgreSQL | 42.7.4 | 数据库驱动 |
| HikariCP | 6.2.1 | 连接池 |
| Logback | 1.5.15 | 日志框架 |

---

## 📁 项目结构

```
kmp-terminator/
├── androidApp/              # Android 应用模块
│   ├── src/main/
│   │   ├── kotlin/com/terminator/android/
│   │   │   ├── MainActivity.kt
│   │   │   ├── App.kt
│   │   │   └── ui/
│   │   │       ├── screens/          # 各个页面
│   │   │       ├── theme/            # 主题配置
│   │   │       └── components/       # 可复用组件
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
│
├── desktopApp/              # Desktop 应用模块
│   ├── src/jvmMain/
│   │   └── kotlin/com/terminator/desktop/
│   │       ├── Main.kt
│   │       ├── App.kt
│   │       └── ui/
│   │           ├── screens/          # 各个页面
│   │           └── theme/            # 主题配置
│   └── build.gradle.kts
│
├── backend/                 # Backend 服务模块
│   ├── src/jvmMain/
│   │   ├── kotlin/com/terminator/backend/
│   │   │   ├── Main.kt
│   │   │   ├── plugins/              # Ktor 插件配置
│   │   │   ├── routes/               # API 路由
│   │   │   └── db/                   # 数据库配置
│   │   └── resources/
│   │       ├── application.conf      # 应用配置
│   │       └── logback.xml           # 日志配置
│   └── build.gradle.kts
│
├── shared/                  # 共享模块
│   ├── src/
│   │   ├── commonMain/              # 跨平台共享代码
│   │   │   └── kotlin/com/terminator/shared/
│   │   │       ├── model/            # 数据模型
│   │   │       ├── network/          # 网络层
│   │   │       ├── repository/       # 数据仓库
│   │   │       ├── di/               # 依赖注入
│   │   │       └── util/             # 工具类
│   │   ├── androidMain/             # Android 特定实现
│   │   ├── iosMain/                 # iOS 特定实现
│   │   └── jvmMain/                 # JVM 特定实现
│   └── build.gradle.kts
│
├── gradle/
│   ├── libs.versions.toml           # 版本目录
│   └── wrapper/
│       └── gradle-wrapper.properties
│
├── build.gradle.kts         # 根构建文件
├── settings.gradle.kts      # 项目设置
└── gradle.properties        # Gradle 属性
```

---

## ⚙️ 环境要求

### 通用要求

- **JDK**: 17 或更高版本
  ```bash
  # macOS
  brew install openjdk@17
  
  # Linux
  sudo apt install openjdk-17-jdk
  
  # Windows
  # 下载并安装 https://adoptium.net/
  ```

- **Gradle**: 8.12（项目自带 wrapper，无需手动安装）

- **Kotlin**: 2.1.20（通过 Gradle 自动下载）

### Android 开发

- **Android Studio**: Hedgehog (2023.1.1) 或更高版本
- **Android SDK**: API 35
- **Android NDK**: 如果需要编译原生代码

### Desktop 开发

- **IDE**: IntelliJ IDEA 2023.3 或更高版本（推荐）
- **操作系统**:
  - macOS 10.14+ (x64/arm64)
  - Windows 10+ (x64)
  - Linux (x64)

### Backend 开发

- **数据库**: PostgreSQL 12+
- **IDE**: IntelliJ IDEA（推荐）

---

## 🚀 快速开始

### 1. 克隆项目

```bash
git clone <repository-url>
cd kmp-terminator
```

### 2. 配置环境变量

```bash
# 设置 JAVA_HOME（推荐 JDK 17）
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.0.7.jdk/Contents/Home

# 验证 Java 版本
java -version
# 应该显示：openjdk version "17.x.x"
```

### 3. 初始化 Gradle

```bash
# 下载 Gradle wrapper
./gradlew wrapper

# 同步项目依赖
./gradlew build
```

### 4. 配置数据库（Backend 必需）

创建 PostgreSQL 数据库：

```sql
CREATE DATABASE terminator_db;
CREATE USER terminator_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE terminator_db TO terminator_user;
```

编辑 `backend/src/jvmMain/resources/application.conf`：

```hocon
database {
    url = "jdbc:postgresql://localhost:5432/terminator_db"
    user = "terminator_user"
    password = "your_password"
    driver = "org.postgresql.Driver"
}
```

---

## 📱 各模块启动指南

### Android 应用

#### 方式一：使用 Android Studio（推荐）

1. 用 Android Studio 打开项目
2. 等待 Gradle 同步完成
3. 选择 `androidApp` 运行配置
4. 选择模拟器或连接真机
5. 点击运行按钮

#### 方式二：命令行

```bash
# 编译 Debug 版本
./gradlew :androidApp:assembleDebug

# 安装到已连接的设备
./gradlew :androidApp:installDebug

# 运行（需要已连接设备或模拟器）
./gradlew :androidApp:run
```

#### 构建 Release 版本

1. 创建签名配置文件 `androidApp/keystore.properties`：

```properties
storeFile=release-keystore.jks
storePassword=your_store_password
keyAlias=your_key_alias
keyPassword=your_key_password
```

2. 构建 Release APK：

```bash
./gradlew :androidApp:assembleRelease
```

APK 文件位于：`androidApp/build/outputs/apk/release/androidApp-release.apk`

---

### Desktop 应用

#### 方式一：使用 IntelliJ IDEA（推荐）

1. 用 IntelliJ IDEA 打开项目
2. 等待 Gradle 同步完成
3. 找到 `desktopApp/src/jvmMain/kotlin/com/terminator/desktop/Main.kt`
4. 右键点击 `main()` 函数，选择 "Run 'MainKt'"

#### 方式二：命令行

```bash
# 运行 Desktop 应用
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.0.7.jdk/Contents/Home
./gradlew :desktopApp:run
```

#### 打包分发

```bash
# macOS DMG
./gradlew :desktopApp:packageDmg

# Windows MSI
./gradlew :desktopApp:packageMsi

# Linux DEB
./gradlew :desktopApp:packageDeb
```

打包文件位于：`desktopApp/build/compose/binaries/`

---

### Backend 服务

#### 前置条件

1. 确保 PostgreSQL 正在运行：

```bash
# macOS
brew services start postgresql

# Linux
sudo systemctl start postgresql
```

2. 确认数据库配置正确（见"快速开始"第 4 步）

#### 方式一：使用 IDE

1. 找到 `backend/src/jvmMain/kotlin/com/terminator/backend/Main.kt`
2. 右键点击 `main()` 函数，选择 "Run 'MainKt'"

#### 方式二：命令行

```bash
# 运行 Backend 服务
./gradlew :backend:run
```

服务将在 `http://0.0.0.0:8080` 启动。

#### 构建 Fat JAR

```bash
# 构建可执行 JAR
./gradlew :backend:buildFatJar

# JAR 文件位置
ls backend/build/libs/backend-*-all.jar
```

运行 Fat JAR：

```bash
java -jar backend/build/libs/backend-1.0.0-all.jar
```

#### 测试 API

```bash
# 健康检查
curl http://localhost:8080/health

# 查看 API 文档（如果配置了 Swagger）
open http://localhost:8080/swagger-ui
```

---

## 🔨 构建与部署

### 全量构建

```bash
# 构建所有模块
./gradlew build

# 清理构建缓存
./gradlew clean

# 运行测试
./gradlew test
```

### Android 部署

1. **Google Play Store**
   - 构建签名 Release APK/AAB
   - 上传到 Google Play Console

2. **国内应用市场**
   - 构建签名 Release APK
   - 按照各市场要求提交

### Desktop 部署

1. **macOS**
   - 打包为 DMG：`./gradlew :desktopApp:packageDmg`
   - 签名（可选）：使用 `codesign` 工具
   - 公证（可选）：使用 `xcrun notarytool`

2. **Windows**
   - 打包为 MSI：`./gradlew :desktopApp:packageMsi`
   - 代码签名（可选）

3. **Linux**
   - 打包为 DEB：`./gradlew :desktopApp:packageDeb`
   - 上传到软件仓库

### Backend 部署

1. **Docker 部署**（推荐）

创建 `Dockerfile`：

```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app
COPY backend/build/libs/backend-*-all.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
```

构建和运行：

```bash
./gradlew :backend:buildFatJar
docker build -t kmp-terminator-backend .
docker run -p 8080:8080 kmp-terminator-backend
```

2. **直接部署**

```bash
# 构建 Fat JAR
./gradlew :backend:buildFatJar

# 上传到服务器
scp backend/build/libs/backend-*-all.jar user@server:/opt/app/

# 在服务器上运行
ssh user@server
cd /opt/app
nohup java -jar backend-1.0.0-all.jar > app.log 2>&1 &
```

---

## ⚙️ 配置说明

### Android 配置

#### 权限配置

`androidApp/src/main/AndroidManifest.xml` 中已配置以下权限：

- `INTERNET`：网络访问
- `ACCESS_NETWORK_STATE`：网络状态检查
- `FOREGROUND_SERVICE`：前台服务（自动任务）
- `RECEIVE_BOOT_COMPLETED`：开机自启
- `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`：忽略电池优化

#### 签名配置

创建 `androidApp/keystore.properties`：

```properties
storeFile=release-keystore.jks
storePassword=your_store_password
keyAlias=your_key_alias
keyPassword=your_key_password
```

生成签名密钥：

```bash
keytool -genkey -v -keystore release-keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias your_key_alias
```

### Desktop 配置

#### 窗口配置

编辑 `desktopApp/src/jvmMain/kotlin/com/terminator/desktop/Main.kt`：

```kotlin
Window(
    onCloseRequest = ::exitApplication,
    title = "KMP Terminator",
    state = rememberWindowState(width = 1200.dp, height = 800.dp)  // 调整窗口大小
)
```

#### Skiko 渲染配置

`desktopApp/build.gradle.kts` 中已配置 macOS arm64 的 Skiko 依赖：

```kotlin
implementation("org.jetbrains.skiko:skiko-awt-runtime-macos-arm64:0.8.18")
```

如需支持其他平台，修改为对应依赖：

- macOS x64: `skiko-awt-runtime-macos-x64`
- Windows x64: `skiko-awt-runtime-windows-x64`
- Linux x64: `skiko-awt-runtime-linux-x64`

### Backend 配置

#### 应用配置

`backend/src/jvmMain/resources/application.conf`：

```hocon
ktor {
    deployment {
        port = 8080
        host = "0.0.0.0"
    }
    application {
        modules = [ com.terminator.backend.MainKt.module ]
    }
}

database {
    url = "jdbc:postgresql://localhost:5432/terminator_db"
    user = "terminator_user"
    password = "your_password"
    driver = "org.postgresql.Driver"
    maxPoolSize = 10
}

security {
    jwt {
        secret = "your-jwt-secret-key"
        realm = "KMP Terminator"
        issuer = "https://your-domain.com"
        audience = "kmp-terminator-clients"
    }
}
```

#### 日志配置

`backend/src/jvmMain/resources/logback.xml`：

```xml
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{YYYY-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="STDOUT"/>
    </root>
</configuration>
```

### Shared 模块配置

#### 网络配置

`shared/src/commonMain/kotlin/com/terminator/shared/network/HttpClient.kt`：

```kotlin
expect fun getHttpClient(): HttpClient

// Android 实现：使用 OkHttp
// iOS 实现：使用 Darwin
// JVM 实现：使用 Java HttpClient
```

#### 数据库配置

`shared/build.gradle.kts` 中配置了 SQLDelight：

```kotlin
sqldelight {
    databases {
        create("TerminatorDatabase") {
            packageName.set("com.terminator.db")
        }
    }
}
```

数据库文件位于：`shared/src/commonMain/sqldelight/`

---

## 🧪 测试

### 运行测试

```bash
# 运行所有测试
./gradlew test

# 运行 Android 测试
./gradlew :androidApp:test

# 运行 Shared 模块测试
./gradlew :shared:test

# 运行 Backend 测试
./gradlew :backend:test
```

### UI 测试（Android）

```bash
# 运行 Compose UI 测试
./gradlew :androidApp:connectedAndroidTest
```

---

## 📚 开发资源

### 官方文档

- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Ktor](https://ktor.io/)
- [SQLDelight](https://cashapp.github.io/sqldelight/)
- [Koin](https://insert-koin.io/)

### 项目相关

- 技术博客：[待补充]
- 设计文档：[待补充]
- API 文档：[待补充]

---

## 🤝 贡献指南

欢迎贡献！请遵循以下步骤：

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

---

## 📄 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

---

## 👥 作者

- **项目作者** - [待补充]

---

## 🙏 致谢

感谢以下开源项目：

- [Kotlin](https://kotlinlang.org/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Ktor](https://ktor.io/)

---

## 📞 联系方式

如有问题或建议，请通过以下方式联系：

- Email: [待补充]
- Issues: [GitHub Issues](待补充)

---

**祝你使用愉快！** 🎉
