package com.silenthink.weatherapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.silenthink.weatherapp.data.model.ForecastDay
import com.silenthink.weatherapp.data.model.Hour
import com.silenthink.weatherapp.utils.WeatherTranslator
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectedDateWeatherCard(
    forecastDay: ForecastDay,
    isToday: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 显示日期信息
            Text(
                text = if (isToday) "今天" else formatFullDate(forecastDay.date),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = forecastDay.date,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 显示主要温度信息
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${forecastDay.day.maxtempC.roundToInt()}°",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 60.sp
                    )
                    Text(
                        text = "最高温度",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
                
                Spacer(modifier = Modifier.width(24.dp))
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 显示天气图标
                    Icon(
                        imageVector = getWeatherIcon(forecastDay.day.condition.code, true),
                        contentDescription = WeatherTranslator.translateWeatherCondition(forecastDay.day.condition.text),
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    
                    Text(
                        text = WeatherTranslator.translateWeatherCondition(forecastDay.day.condition.text),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
                
                Spacer(modifier = Modifier.width(24.dp))
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${forecastDay.day.mintempC.roundToInt()}°",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 60.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "最低温度",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // 显示详细天气信息网格
            DayWeatherDetailsGrid(day = forecastDay.day)
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // 显示24小时天气预报
            if (forecastDay.hour.isNotEmpty()) {
                Text(
                    text = "24小时预报",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(forecastDay.hour) { hour ->
                        HourlyWeatherItem(hour = hour)
                    }
                }
            }
        }
    }
}

@Composable
fun DayWeatherDetailsGrid(day: com.silenthink.weatherapp.data.model.Day) {
    Column {
        // 第一行信息
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            WeatherDetailItem(
                icon = Icons.Default.Thermostat,
                label = "平均温度",
                value = "${day.avgtempC.roundToInt()}°C"
            )
            WeatherDetailItem(
                icon = Icons.Default.WaterDrop,
                label = "降雨概率",
                value = "${day.dailyChanceOfRain}%"
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 第二行信息
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            WeatherDetailItem(
                icon = Icons.Default.WaterDrop,
                label = "平均湿度",
                value = "${day.avghumidity}%"
            )
            WeatherDetailItem(
                icon = Icons.Default.WbSunny,
                label = "紫外线指数",
                value = day.uv.toString()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HourlyWeatherItem(hour: Hour) {
    Card(
        modifier = Modifier.width(80.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 时间
            Text(
                text = formatHour(hour.time),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(6.dp))
            
            // 天气图标
            Icon(
                imageVector = getWeatherIcon(hour.condition.code, hour.isDay == 1),
                contentDescription = WeatherTranslator.translateWeatherCondition(hour.condition.text),
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(6.dp))
            
            // 温度
            Text(
                text = "${hour.tempC.roundToInt()}°",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            
            // 降雨概率
            if (hour.chanceOfRain > 0) {
                Text(
                    text = "${hour.chanceOfRain}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// 格式化完整日期
fun formatFullDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy年MM月dd日 EEEE", Locale.CHINA)
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        dateString
    }
}

// 格式化小时
fun formatHour(timeString: String): String {
    return try {
        // timeString格式：2023-12-25 14:00
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = inputFormat.parse(timeString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        timeString.substring(11, 16) // 提取时间部分
    }
} 