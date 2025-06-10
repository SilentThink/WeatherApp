package com.silenthink.weatherapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.silenthink.weatherapp.data.model.WeatherResponse
import com.silenthink.weatherapp.utils.WeatherVoiceManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceBroadcastCard(
    weatherResponse: WeatherResponse?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var voiceManager by remember { mutableStateOf<WeatherVoiceManager?>(null) }
    var isInitialized by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var broadcastText by remember { mutableStateOf("") }
    
    // 初始化语音管理器
    LaunchedEffect(Unit) {
        voiceManager = WeatherVoiceManager(context)
        isLoading = true
        isInitialized = voiceManager?.initialize() ?: false
        isLoading = false
    }
    
    // 清理资源
    DisposableEffect(Unit) {
        onDispose {
            voiceManager?.release()
        }
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "智能语音播报",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "正在初始化语音服务...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            } else if (!isInitialized) {
                Text(
                    text = "语音服务初始化失败",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
                Button(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            isInitialized = voiceManager?.initialize() ?: false
                            isLoading = false
                        }
                    }
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("重试")
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 智能播报按钮
                    Button(
                        onClick = {
                            weatherResponse?.let { weather ->
                                scope.launch {
                                    isPlaying = true
                                    broadcastText = "" // 清除之前的文字
                                    val reportText = voiceManager?.broadcastWeather(weather)
                                    broadcastText = reportText ?: "播报失败"
                                    // 简单的播放状态管理，实际应该监听TTS回调
                                    kotlinx.coroutines.delay(3000)
                                    isPlaying = false
                                }
                            }
                        },
                        enabled = weatherResponse != null && !isPlaying
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.VolumeUp else Icons.Default.RecordVoiceOver,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isPlaying) "播报中..." else "智能播报")
                    }
                    
                    // 快速播报按钮
                    OutlinedButton(
                        onClick = {
                            weatherResponse?.let { weather ->
                                scope.launch {
                                    isPlaying = true
                                    broadcastText = "" // 清除之前的文字
                                    val reportText = voiceManager?.quickBroadcast(weather)
                                    broadcastText = reportText ?: "播报失败"
                                    kotlinx.coroutines.delay(2000)
                                    isPlaying = false
                                }
                            }
                        },
                        enabled = weatherResponse != null && !isPlaying
                    ) {
                        Icon(Icons.Default.Speed, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("快速播报")
                    }
                }
                
                // 播放控制按钮
                if (isPlaying) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { voiceManager?.pauseBroadcast() }
                        ) {
                            Icon(Icons.Default.Pause, contentDescription = "暂停")
                        }
                        
                        IconButton(
                            onClick = { 
                                voiceManager?.stopBroadcast()
                                isPlaying = false
                                broadcastText = "" // 停止时清除文字
                            }
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = "停止")
                        }
                    }
                }
                
                // 显示播报文字
                if (broadcastText.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TextFields,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "播报内容：",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = broadcastText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                
                Text(
                    text = "点击按钮开始语音播报天气信息",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
} 