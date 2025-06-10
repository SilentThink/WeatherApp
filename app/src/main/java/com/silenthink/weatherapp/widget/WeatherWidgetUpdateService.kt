package com.silenthink.weatherapp.widget

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.silenthink.weatherapp.data.repository.WeatherRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import java.text.SimpleDateFormat
import java.util.*

/**
 * 天气小组件后台更新服务
 */
class WeatherWidgetUpdateService : Service() {
    
    private val repository = WeatherRepository()
    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval = 30 * 60 * 1000L // 30分钟更新一次
    private var updateRunnable: Runnable? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startPeriodicUpdate()
        return START_STICKY // 服务被系统杀死后自动重启
    }

    private fun startPeriodicUpdate() {
        updateRunnable = object : Runnable {
            override fun run() {
                updateWeatherData()
                handler.postDelayed(this, updateInterval)
            }
        }
        handler.post(updateRunnable!!)
    }

    private fun updateWeatherData() {
        serviceScope.launch {
            try {
                // 从SharedPreferences读取用户最后选择的城市
                val prefs = getSharedPreferences("weather_widget", Context.MODE_PRIVATE)
                val defaultCity = prefs.getString("selected_city", "北京") ?: "北京"
                
                repository.getCurrentWeather(defaultCity)
                    .catch { e ->
                        Log.e("WeatherWidget", "获取天气数据失败", e)
                        saveErrorToPrefs("网络错误")
                    }
                    .collect { result ->
                        result.onSuccess { weather ->
                            saveWeatherToPrefs(weather.location.name, 
                                             weather.current.tempC.toInt().toString(),
                                             weather.current.condition.text)
                            
                            // 更新小组件UI
                            withContext(Dispatchers.Main) {
                                WeatherWidgetProvider.updateWidgetData(this@WeatherWidgetUpdateService)
                            }
                        }
                        result.onFailure { error ->
                            Log.e("WeatherWidget", "天气数据获取失败", error)
                            saveErrorToPrefs("获取失败")
                        }
                    }
            } catch (e: Exception) {
                Log.e("WeatherWidget", "更新天气数据异常", e)
                saveErrorToPrefs("更新异常")
            }
        }
    }

    private fun saveWeatherToPrefs(cityName: String, temperature: String, weatherDesc: String) {
        val prefs = getSharedPreferences("weather_widget", Context.MODE_PRIVATE)
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        
        prefs.edit().apply {
            putString("city_name", cityName)
            putString("temperature", temperature)
            putString("weather_desc", weatherDesc)
            putString("last_update", currentTime)
            apply()
        }
    }

    private fun saveErrorToPrefs(errorMsg: String) {
        val prefs = getSharedPreferences("weather_widget", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("weather_desc", errorMsg)
            putString("last_update", "错误")
            apply()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        updateRunnable?.let { handler.removeCallbacks(it) }
        serviceScope.cancel()
    }
}