# WeatherApp - 智能天气应用

一款基于Android平台的现代化智能天气应用，采用Jetpack Compose构建，集成AI语音播报功能，提供实时天气查询、多城市管理和智能语音播报等功能。

## 📱 应用截图

> 注：请在此处添加应用截图

## ✨ 功能特性

### 🌤️ 核心天气功能
- **实时天气查询**: 获取当前城市的实时天气信息
- **7天天气预报**: 详细的未来一周天气预报
- **多城市支持**: 支持搜索和切换不同城市的天气
- **位置服务**: 自动获取当前位置的天气信息
- **离线缓存**: 本地存储查询历史，减少网络请求

### 🎙️ 智能语音播报
- **AI智能播报**: 集成DeepSeek AI，生成个性化、幽默的天气播报文本
- **高质量TTS**: 采用科大讯飞语音合成技术，提供自然流畅的中文语音播报
- **双模式播报**: 支持智能播报和快速播报两种模式
- **实用建议**: 根据天气条件提供穿衣、出行等生活建议

### 项目结构
```
app/src/main/java/com/silenthink/weatherapp/
├── data/                    # 数据层
│   ├── api/                # API服务接口
│   ├── model/              # 数据模型
│   └── repository/         # 数据仓库
├── ui/                     # UI层
│   ├── components/         # 可复用组件
│   ├── theme/              # 主题配置
│   └── viewmodel/          # ViewModel
├── utils/                  # 工具类
├── widget/                 # 桌面小组件
└── MainActivity.kt         # 主Activity
```

### 核心依赖
```kotlin
// UI框架
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")
implementation("androidx.activity:activity-compose")

// 网络请求
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")

// 状态管理
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
implementation("androidx.navigation:navigation-compose:2.7.5")

// 位置服务
implementation("com.google.android.gms:play-services-location:21.0.1")

// 权限处理
implementation("com.google.accompanist:accompanist-permissions:0.32.0")

// 本地存储
implementation("androidx.datastore:datastore-preferences:1.0.0")

// 科大讯飞TTS SDK
implementation(files("libs/Msc.jar"))
```

## 🚀 快速开始

### 环境要求
- Android Studio Hedgehog | 2023.1.1 或更高版本
- Android SDK API 30 或更高版本
- Kotlin 1.9.0 或更高版本
- JDK 11 或更高版本

### 安装步骤

1. **克隆项目**
   ```bash
   git clone https://github.com/yourusername/WeatherApp.git
   cd WeatherApp
   ```

2. **配置API密钥**
   
   **天气API配置**:
   - 注册 [WeatherAPI](https://www.weatherapi.com/) 账号获取API密钥
   - 在 `app/src/main/java/com/silenthink/weatherapp/data/api/WeatherApiService.kt` 中配置：
   ```kotlin
   private const val API_KEY = "YOUR_WEATHER_API_KEY"
   ```

   **DeepSeek AI配置** (可选):
   - 访问 [DeepSeek官网](https://www.deepseek.com/) 获取API密钥
   - 在 `app/src/main/java/com/silenthink/weatherapp/data/api/DeepSeekApiService.kt` 中配置：
   ```kotlin
   private val apiKey = "YOUR_DEEPSEEK_API_KEY"
   ```

   **科大讯飞TTS配置** (可选):
   - 访问 [科大讯飞开放平台](https://www.xfyun.cn/) 获取APPID
   - 在 `app/src/main/java/com/silenthink/weatherapp/utils/VoiceBroadcastService.kt` 中配置：
   ```kotlin
   private const val APPID = "YOUR_IFLYTEK_APPID"
   ```

3. **构建运行**
   ```bash
   ./gradlew assembleDebug
   ```
   或在Android Studio中直接运行

### 权限说明

应用需要以下权限：
- `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION`: 获取位置信息
- `INTERNET`: 网络请求
- `RECORD_AUDIO`: 语音合成功能
- `WRITE_EXTERNAL_STORAGE` / `READ_EXTERNAL_STORAGE`: 音频文件存储
- `READ_PHONE_STATE`: TTS服务需要

## 📖 使用指南

### 基本使用
1. **获取位置天气**: 启动应用后，点击位置按钮获取当前位置天气
2. **搜索城市**: 在搜索框中输入城市名称，选择目标城市
3. **查看预报**: 滑动日期选择器查看不同日期的天气预报
4. **语音播报**: 点击语音播报按钮听取天气信息

### 高级功能
- **桌面小组件**: 长按桌面添加天气小组件
- **智能播报**: 使用AI生成个性化天气播报内容
- **历史查询**: 查看之前查询过的城市天气

## 🤝 贡献指南

### Commit 提交规范

为保持代码提交的一致性和可读性，本项目采用以下commit图标分类：

| 图标 | 类型 | 说明 |
|------|------|------|
| ✨ | feat | 新功能 |
| 🐛 | fix | 修复bug |
| 📝 | docs | 文档更新 |
| 💄 | style | 代码格式修改，非功能性更改 |
| ♻️ | refactor | 代码重构，既不修复bug也不添加新功能 |
| ⚡️ | perf | 性能优化 |
| ✅ | test | 添加或修改测试代码 |
| 🔧 | chore | 构建过程或辅助工具的变动 |
| 🔀 | merge | 合并分支 |
| 🚀 | deploy | 部署相关 |
| 🗃️ | db | 数据库相关变更 |
| 🎨 | ui | 用户界面和用户体验相关 |
| 🔒 | security | 安全相关更新 |

### 提交示例
```bash
✨ feat: 添加用户登录功能
🐛 fix: 修复列表不刷新的问题
📝 docs: 更新README文档
💄 style: 格式化代码风格
♻️ refactor: 重构数据访问层
⚡️ perf: 优化列表加载性能
✅ test: 添加登录功能单元测试
🔧 chore: 更新Gradle依赖版本
```
