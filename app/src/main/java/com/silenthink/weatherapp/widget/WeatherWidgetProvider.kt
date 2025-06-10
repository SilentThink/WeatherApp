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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
    }
}