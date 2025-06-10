package com.silenthink.weatherapp.widget

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.silenthink.weatherapp.data.repository.WeatherRepository
import com.silenthink.weatherapp.utils.WeatherTranslator
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

    // 中文城市名到英文城市名的映射，与ViewModel和WeatherWidgetProvider保持一致
    private val cityMapping = mapOf(
        "北京" to "Beijing",
        "上海" to "Shanghai", 
        "广州" to "Guangzhou",
        "深圳" to "Shenzhen",
        "杭州" to "Hangzhou",
        "南京" to "Nanjing",
        "武汉" to "Wuhan",
        "成都" to "Chengdu",
        "西安" to "Xian",
        "重庆" to "Chongqing",
        "天津" to "Tianjin",
        "青岛" to "Qingdao",
        "大连" to "Dalian",
        "厦门" to "Xiamen",
        "苏州" to "Suzhou",
        "无锡" to "Wuxi",
        "宁波" to "Ningbo",
        "长沙" to "Changsha",
        "郑州" to "Zhengzhou",
        "济南" to "Jinan",
        "沈阳" to "Shenyang",
        "哈尔滨" to "Harbin",
        "长春" to "Changchun",
        "石家庄" to "Shijiazhuang"
    )

    // 转换城市名称（如果是中文则转换为英文）
    private fun convertCityName(cityName: String): String {
        return cityMapping[cityName] ?: cityName
    }

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
                
                // 转换城市名称
                val convertedCity = convertCityName(defaultCity)
                
                repository.getCurrentWeather(convertedCity)
                    .catch { e ->
                        Log.e("WeatherWidget", "获取天气数据失败", e)
                        saveErrorToPrefs("网络错误")
                    }
                    .collect { result ->
                        result.onSuccess { weather ->
                            saveWeatherToPrefs(weather.location.name, 
                                             weather.current.tempC.toInt().toString(),
                                             WeatherTranslator.translateWeatherCondition(weather.current.condition.text))
                            
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