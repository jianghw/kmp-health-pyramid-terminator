# Android 应用

基于 Kotlin 和 Compose Multiplatform 的 Android 移动端应用。

## 环境要求

- JDK 17+
- Android Studio Hedgehog 或更高版本
- Android SDK 35
- Gradle 8.12

## 快速开始

### 1. 配置环境

```bash
# 设置 JAVA_HOME
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.0.7.jdk/Contents/Home

# 验证 Java 版本
java -version
```

### 2. 构建和运行

```bash
# 方式一：使用 Android Studio
# 打开项目，等待 Gradle 同步完成，选择 androidApp 运行配置

# 方式二：命令行
./gradlew :androidApp:installDebug
```

### 3. 构建 Release 版本

```bash
# 1. 创建签名配置
cp androidApp/keystore.properties.example androidApp/keystore.properties
# 编辑 keystore.properties，填入签名信息

# 2. 构建 Release APK
./gradlew :androidApp:assembleRelease

# APK 位置：androidApp/build/outputs/apk/release/androidApp-release.apk
```

## 项目结构

```
androidApp/
├── src/main/
│   ├── kotlin/com/terminator/android/
│   │   ├── MainActivity.kt          # 应用入口
│   │   ├── App.kt                    # 导航和主题
│   │   └── ui/
│   │       ├── screens/              # 页面
│   │       ├── components/           # 可复用组件
│   │       └── theme/                # 主题配置
│   └── AndroidManifest.xml
└── build.gradle.kts
```

## 常用命令

```bash
# 编译
./gradlew :androidApp:compileDebugKotlin

# 运行测试
./gradlew :androidApp:testDebugUnitTest

# Lint 检查
./gradlew :androidApp:lint

# 构建 Debug APK
./gradlew :androidApp:assembleDebug

# 构建 Release APK
./gradlew :androidApp:assembleRelease

# 安装到设备
./gradlew :androidApp:installDebug
```

## 签名配置

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

## 验证

```bash
# 使用 Makefile
make verify-android

# 或手动验证
./gradlew :androidApp:compileDebugKotlin
./gradlew :androidApp:testDebugUnitTest
```

## 依赖说明

- `shared`：跨平台共享模块
- Compose Multiplatform：UI 框架
- Koin：依赖注入
- Coil：图片加载
- Ktor：网络请求

## 注意事项

1. **Java 版本**：必须使用 JDK 17+
2. **签名文件**：不要将 `release-keystore.jks` 和 `keystore.properties` 提交到 Git
3. **最低 SDK**：API 25 (Android 7.1)
4. **目标 SDK**：API 35 (Android 15)

## 相关文档

- [项目 README](../README.md)
- [贡献指南](../CONTRIBUTING.md)
- [文档入口](../docs/README.md)
