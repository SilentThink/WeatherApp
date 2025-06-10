# 智能语音播报功能配置指南

## 概述

本天气应用已集成智能语音播报功能，结合了DeepSeek AI和科大讯飞TTS技术，为用户提供幽默风趣的天气播报体验。

## 功能特点

1. **智能文本生成**：使用DeepSeek AI生成个性化、幽默的天气播报文本
2. **高质量语音合成**：采用科大讯飞TTS技术，提供自然流畅的中文语音播报
3. **实用生活建议**：根据天气条件提供穿衣、出行等实用建议
4. **双模式播报**：支持智能播报和快速播报两种模式

## 配置步骤

### 1. 配置DeepSeek API

1. 访问 [DeepSeek官网](https://www.deepseek.com/) 注册账号
2. 获取API密钥
3. 在 `app/src/main/java/com/silenthink/weatherapp/data/api/DeepSeekApiService.kt` 文件中：
   ```kotlin
   // 将 YOUR_DEEPSEEK_API_KEY 替换为您的实际API密钥
   private val apiKey = "YOUR_DEEPSEEK_API_KEY"
   ```

### 2. 配置科大讯飞TTS

1. 访问 [科大讯飞开放平台](https://www.xfyun.cn/) 注册开发者账号
2. 创建应用并获取APPID
3. 在 `app/src/main/java/com/silenthink/weatherapp/utils/VoiceBroadcastService.kt` 文件中：
   ```kotlin
   // 将 YOUR_IFLYTEK_APPID 替换为您的实际APPID
   private const val APPID = "YOUR_IFLYTEK_APPID"
   ```

## 使用方法

### 智能播报
- 点击"智能播报"按钮
- AI将根据当前天气生成个性化播报文本
- 包含幽默元素和实用建议

### 快速播报
- 点击"快速播报"按钮
- 使用预设模板快速播报基本天气信息
- 播报时间更短，适合快速了解天气

### 播放控制
- 播报过程中可以暂停、停止播放
- 支持播放状态实时显示

## 权限说明

应用需要以下权限来支持语音播报功能：

- `RECORD_AUDIO`：语音合成功能需要
- `WRITE_EXTERNAL_STORAGE`：保存音频文件
- `READ_EXTERNAL_STORAGE`：读取音频文件
- `READ_PHONE_STATE`：TTS服务需要
- `INTERNET`：调用AI API和在线TTS服务

## 故障排除

### 语音服务初始化失败
1. 检查网络连接
2. 确认APPID配置正确
3. 检查权限是否已授予
4. 点击"重试"按钮重新初始化

### AI播报失败
1. 检查DeepSeek API密钥是否正确
2. 确认网络连接正常
3. 系统会自动降级到默认播报模式

### 无声音输出
1. 检查设备音量设置
2. 确认应用音频权限
3. 重启应用重新初始化TTS服务

## 技术架构

```
WeatherVoiceManager
├── DeepSeekApiService (AI文本生成)
└── VoiceBroadcastService (TTS语音合成)
```

## 注意事项

1. 首次使用需要初始化语音服务，可能需要几秒钟时间
2. AI播报需要网络连接，离线时会使用默认模板
3. 语音文件会临时保存在应用私有目录
4. 建议在WiFi环境下使用以获得最佳体验

## 更新日志

- v1.0: 集成DeepSeek AI和科大讯飞TTS
- 支持智能播报和快速播报
- 添加播放控制功能
- 优化用户界面和交互体验 