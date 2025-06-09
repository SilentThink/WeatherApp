package com.silenthink.weatherapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.silenthink.weatherapp.data.model.Current
import com.silenthink.weatherapp.data.model.Location
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherCard(
    location: Location,
    current: Current,
    modifier: Modifier = Modifier
) {
    // 创建一个卡片，用于显示天气信息
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        // 创建一个列布局，用于垂直排列天气信息
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 显示位置信息
            Text(
                text = "${location.name}, ${location.region}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = location.country,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 显示主要温度信息
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${current.tempC.roundToInt()}°",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 72.sp
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 显示天气图标
                    Icon(
                        imageVector = getWeatherIcon(current.condition.code, current.isDay == 1),
                        contentDescription = current.condition.text,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    
                    Text(
                        text = current.condition.text,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // 显示详细天气信息网格
            WeatherDetailsGrid(current = current)
        }
    }
}

@Composable
fun WeatherDetailsGrid(current: Current) {
    // 创建一个列布局，用于显示详细天气信息
    Column {
        // 第一行信息
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            WeatherDetailItem(
                icon = Icons.Default.Thermostat,
                label = "体感温度",
                value = "${current.feelslikeC.roundToInt()}°C"
            )
            WeatherDetailItem(
                icon = Icons.Default.Air,
                label = "风速",
                value = "${current.windKph.roundToInt()} km/h"
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
                label = "湿度",
                value = "${current.humidity}%"
            )
            WeatherDetailItem(
                icon = Icons.Default.Visibility,
                label = "能见度",
                value = "${current.visKm.roundToInt()} km"
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 第三行信息
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            WeatherDetailItem(
                icon = Icons.Default.Speed,
                label = "气压",
                value = "${current.pressureMb.roundToInt()} mb"
            )
            WeatherDetailItem(
                icon = Icons.Default.WbSunny,
                label = "紫外线指数",
                value = current.uv.toString()
            )
        }
    }
}

@Composable
fun WeatherDetailItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    // 创建一个列布局，用于显示天气详细信息项
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(100.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

// 根据天气代码返回相应的图标
@Composable
fun getWeatherIcon(code: Int, isDay: Boolean): ImageVector {
    return when (code) {
        1000 -> if (isDay) Icons.Default.WbSunny else Icons.Default.NightsStay
        1003, 1006, 1009 -> Icons.Default.Cloud
        1030, 1135, 1147 -> Icons.Default.Cloud
        1063, 1150, 1153, 1180, 1183, 1186, 1189, 1192, 1195, 1240, 1243, 1246 -> Icons.Default.Grain
        1066, 1069, 1072, 1114, 1117, 1210, 1213, 1216, 1219, 1222, 1225, 1237, 1249, 1252, 1255, 1258, 1261, 1264 -> Icons.Default.AcUnit
        1087, 1273, 1276, 1279, 1282 -> Icons.Default.Bolt
        else -> if (isDay) Icons.Default.WbSunny else Icons.Default.NightsStay
    }
}