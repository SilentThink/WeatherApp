package com.silenthink.weatherapp.utils

import android.content.Context
import android.util.Log
import com.silenthink.weatherapp.data.api.DeepSeekApiService
import com.silenthink.weatherapp.data.api.WeatherReportRequest
import com.silenthink.weatherapp.data.model.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherVoiceManager(private val context: Context) {
    
    private val voiceBroadcastService = VoiceBroadcastService(context)
    private val deepSeekApiService = DeepSeekApiService()
    
    companion object {
        private const val TAG = "WeatherVoiceManager"
    }
    
    /**
     * 初始化语音服务
     */
    suspend fun initialize(): Boolean {
        return try {
            // 初始化讯飞SDK
            voiceBroadcastService.initializeIflytek(context)
            // 初始化TTS服务
            voiceBroadcastService.initialize()
        } catch (e: Exception) {
            Log.e(TAG, "初始化语音服务失败: ${e.message}")
            false
        }
    }
    
    /**
     * 播报天气信息
     */
    suspend fun broadcastWeather(weatherResponse: WeatherResponse): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // 提取天气数据
                val weatherData = extractWeatherData(weatherResponse)
                
                // 生成播报文本
                val reportText = deepSeekApiService.generateWeatherReport(weatherData)
                Log.d(TAG, "生成的播报文本: $reportText")
                
                // 执行语音播报
                withContext(Dispatchers.Main) {
                    voiceBroadcastService.startSpeaking(reportText)
                }
            } catch (e: Exception) {
                Log.e(TAG, "播报天气失败: ${e.message}")
                false
            }
        }
    }
    
    /**
     * 播报自定义文本
     */
    suspend fun broadcastText(text: String): Boolean {
        return try {
            voiceBroadcastService.startSpeaking(text)
        } catch (e: Exception) {
            Log.e(TAG, "播报文本失败: ${e.message}")
            false
        }
    }
    
    /**
     * 暂停播报
     */
    fun pauseBroadcast() {
        voiceBroadcastService.pauseSpeaking()
    }
    
    /**
     * 继续播报
     */
    fun resumeBroadcast() {
        voiceBroadcastService.resumeSpeaking()
    }
    
    /**
     * 停止播报
     */
    fun stopBroadcast() {
        voiceBroadcastService.stopSpeaking()
    }
    
    /**
     * 检查是否正在播报
     */
    fun isBroadcasting(): Boolean {
        return voiceBroadcastService.isSpeaking()
    }
    
    /**
     * 释放资源
     */
    fun release() {
        voiceBroadcastService.release()
    }
    
    /**
     * 从天气响应中提取数据
     */
    private fun extractWeatherData(weatherResponse: WeatherResponse): WeatherReportRequest {
        val current = weatherResponse.current
        val location = weatherResponse.location
        
        return WeatherReportRequest(
            city = location.name,
            temperature = "${current.tempC.toInt()}°C",
            condition = WeatherTranslator.translateWeatherCondition(current.condition.text),
            humidity = "${current.humidity}%",
            windSpeed = "${current.windKph}km/h",
            airQuality = null // 如果有空气质量数据可以在这里添加
        )
    }
    
    /**
     * 生成快速播报（使用默认模板）
     */
    suspend fun quickBroadcast(weatherResponse: WeatherResponse): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val current = weatherResponse.current
                val location = weatherResponse.location
                
                val quickText = generateQuickReport(
                    location.name, 
                    current.tempC.toInt(), 
                    WeatherTranslator.translateWeatherCondition(current.condition.text)
                )
                
                withContext(Dispatchers.Main) {
                    voiceBroadcastService.startSpeaking(quickText)
                }
            } catch (e: Exception) {
                Log.e(TAG, "快速播报失败: ${e.message}")
                false
            }
        }
    }
    
    /**
     * 生成简单快速播报文本
     */
    private fun generateQuickReport(city: String, temperature: Int, condition: String): String {
        val timeGreeting = when (System.currentTimeMillis() % (24 * 60 * 60 * 1000) / (60 * 60 * 1000)) {
            in 0..5 -> "深夜好"
            in 6..11 -> "早上好"
            in 12..13 -> "中午好"
            in 14..17 -> "下午好"
            in 18..23 -> "晚上好"
            else -> "您好"
        }
        
        val tempDescription = when {
            temperature < 0 -> "寒冷"
            temperature < 10 -> "较冷"
            temperature < 20 -> "凉爽"
            temperature < 30 -> "舒适"
            else -> "炎热"
        }
        
        return "${timeGreeting}！${city}当前气温${temperature}度，${condition}，天气${tempDescription}。"
    }
} 