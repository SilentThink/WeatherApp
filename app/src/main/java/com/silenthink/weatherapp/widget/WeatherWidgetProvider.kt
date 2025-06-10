package com.silenthink.weatherapp.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.silenthink.weatherapp.MainActivity
import com.silenthink.weatherapp.R
import com.silenthink.weatherapp.data.repository.WeatherRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.catch
import java.text.SimpleDateFormat
import java.util.*

/**
 * 天气桌面小组件Provider
 */
class WeatherWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // 为每个小组件实例更新数据
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // 当第一个小组件被添加时调用
        super.onEnabled(context)
        // 启动后台服务更新天气数据
        val intent = Intent(context, WeatherWidgetUpdateService::class.java)
        context.startService(intent)
    }

    override fun onDisabled(context: Context) {
        // 当最后一个小组件被移除时调用
        super.onDisabled(context)
        // 停止后台服务
        val intent = Intent(context, WeatherWidgetUpdateService::class.java)
        context.stopService(intent)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        
        when (intent.action) {
            ACTION_WIDGET_UPDATE -> {
                // 手动刷新小组件
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val componentName = ComponentName(context, WeatherWidgetProvider::class.java)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
                onUpdate(context, appWidgetManager, appWidgetIds)
            }
        }
    }

    companion object {
        const val ACTION_WIDGET_UPDATE = "com.silenthink.weatherapp.WIDGET_UPDATE"

        // 中文城市名到英文城市名的映射，与ViewModel保持一致
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

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            // 创建RemoteViews
            val views = RemoteViews(context.packageName, R.layout.weather_widget)

            // 从SharedPreferences获取天气数据
            val prefs = context.getSharedPreferences("weather_widget", Context.MODE_PRIVATE)
            val cityName = prefs.getString("city_name", "未知城市")
            val temperature = prefs.getString("temperature", "--")
            val weatherDesc = prefs.getString("weather_desc", "获取中...")
            val lastUpdate = prefs.getString("last_update", "")

            // 设置UI数据
            views.setTextViewText(R.id.widget_city_name, cityName)
            views.setTextViewText(R.id.widget_temperature, "${temperature}°C")
            views.setTextViewText(R.id.widget_weather_desc, weatherDesc)
            views.setTextViewText(R.id.widget_last_update, "更新: $lastUpdate")

            // 设置点击打开应用的Intent
            val openAppIntent = Intent(context, MainActivity::class.java)
            val openAppPendingIntent = PendingIntent.getActivity(
                context, 0, openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_container, openAppPendingIntent)

            // 设置刷新按钮的Intent
            val refreshIntent = Intent(context, WeatherWidgetProvider::class.java).apply {
                action = ACTION_WIDGET_UPDATE
            }
            val refreshPendingIntent = PendingIntent.getBroadcast(
                context, 0, refreshIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_refresh_btn, refreshPendingIntent)

            // 更新小组件
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun updateWidgetData(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, WeatherWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }

        /**
         * 立即更新指定城市的天气数据到小组件
         */
        fun updateWidgetDataForCity(context: Context, cityName: String) {
            val repository = WeatherRepository()
            val widgetScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
            
            // 转换城市名称
            val convertedCityName = convertCityName(cityName)
            
            widgetScope.launch {
                try {
                    repository.getCurrentWeather(convertedCityName)
                        .catch { e ->
                            saveErrorToPrefs(context, "网络错误")
                        }
                        .collect { result ->
                            result.onSuccess { weather ->
                                saveWeatherToPrefs(context, 
                                    weather.location.name, 
                                    weather.current.tempC.toInt().toString(),
                                    weather.current.condition.text)
                                
                                // 更新小组件UI
                                updateWidgetData(context)
                            }
                            result.onFailure { error ->
                                saveErrorToPrefs(context, "获取失败")
                            }
                        }
                } catch (e: Exception) {
                    saveErrorToPrefs(context, "更新异常")
                }
            }
        }

        private fun saveWeatherToPrefs(context: Context, cityName: String, temperature: String, weatherDesc: String) {
            val prefs = context.getSharedPreferences("weather_widget", Context.MODE_PRIVATE)
            val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            
            prefs.edit().apply {
                putString("city_name", cityName)
                putString("temperature", temperature)
                putString("weather_desc", weatherDesc)
                putString("last_update", currentTime)
                apply()
            }
        }

        private fun saveErrorToPrefs(context: Context, errorMsg: String) {
            val prefs = context.getSharedPreferences("weather_widget", Context.MODE_PRIVATE)
            prefs.edit().apply {
                putString("weather_desc", errorMsg)
                putString("last_update", "错误")
                apply()
            }
        }
    }
}