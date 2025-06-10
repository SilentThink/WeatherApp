package com.silenthink.weatherapp.utils

import android.content.Context
import android.util.Log
import com.silenthink.weatherapp.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 语音播报功能测试辅助类
 */
class VoiceTestHelper(private val context: Context) {
    
    private val weatherVoiceManager = WeatherVoiceManager(context)
    
    companion object {
        private const val TAG = "VoiceTestHelper"
    }
    
    /**
     * 测试语音播报功能
     */
    fun testVoiceBroadcast() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                Log.d(TAG, "开始测试语音播报功能")
                
                // 初始化语音服务
                val isInitialized = weatherVoiceManager.initialize()
                if (!isInitialized) {
                    Log.e(TAG, "语音服务初始化失败")
                    return@launch
                }
                
                Log.d(TAG, "语音服务初始化成功")
                
                // 创建测试天气数据
                val testWeatherResponse = createTestWeatherData()
                
                // 测试智能播报
                Log.d(TAG, "测试智能播报")
                weatherVoiceManager.broadcastWeather(testWeatherResponse)
                
                // 等待播报完成后测试快速播报
                kotlinx.coroutines.delay(5000)
                
                Log.d(TAG, "测试快速播报")
                weatherVoiceManager.quickBroadcast(testWeatherResponse)
                
            } catch (e: Exception) {
                Log.e(TAG, "测试过程中出现异常: ${e.message}")
            }
        }
    }
    
    /**
     * 测试简单文本播报
     */
    fun testSimpleTextBroadcast() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val isInitialized = weatherVoiceManager.initialize()
                if (isInitialized) {
                    weatherVoiceManager.broadcastText("语音播报功能测试成功！")
                }
            } catch (e: Exception) {
                Log.e(TAG, "简单文本播报测试失败: ${e.message}")
            }
        }
    }
    
    /**
     * 创建测试用的天气数据
     */
    private fun createTestWeatherData(): WeatherResponse {
        return WeatherResponse(
            location = Location(
                name = "北京",
                region = "北京",
                country = "中国",
                lat = 39.9042,
                lon = 116.4074,
                tzId = "Asia/Shanghai",
                localtime = "2024-01-01 12:00"
            ),
            current = Current(
                tempC = 15.0,
                tempF = 59.0,
                isDay = 1,
                condition = Condition(
                    text = "晴",
                    icon = "//cdn.weatherapi.com/weather/64x64/day/113.png",
                    code = 1000
                ),
                windKph = 8.0,
                windDir = "S",
                pressureMb = 1013.0,
                humidity = 65,
                cloud = 25,
                feelslikeC = 15.0,
                visKm = 10.0,
                uv = 5.0
            ),
            forecast = Forecast(
                forecastday = emptyList()
            )
        )
    }
    
    /**
     * 释放资源
     */
    fun release() {
        weatherVoiceManager.release()
    }
} 