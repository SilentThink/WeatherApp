package com.silenthink.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import com.silenthink.weatherapp.widget.WeatherWidgetProvider
import com.silenthink.weatherapp.ui.components.*
import com.silenthink.weatherapp.ui.theme.WeatherAppTheme
import com.silenthink.weatherapp.ui.viewmodel.WeatherViewModel
import com.silenthink.weatherapp.ui.viewmodel.WeatherViewModelFactory
import com.silenthink.weatherapp.utils.rememberLocationPermissionLauncher
import com.silenthink.weatherapp.utils.LOCATION_PERMISSIONS
import androidx.core.content.edit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                WeatherApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherApp() {
    val context = LocalContext.current
    val viewModel: WeatherViewModel = viewModel(
        factory = WeatherViewModelFactory(context)
    )
    val uiState by viewModel.uiState.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var hasRequestedPermission by remember { mutableStateOf(false) }
    
    // 权限请求处理
    val locationPermissionLauncher = rememberLocationPermissionLauncher { granted ->
        // 无论是否授权，都更新权限状态
        viewModel.updateLocationPermissionStatus()
        
        if (granted) {
            // 权限已授权，getCurrentLocationWeather会在updateLocationPermissionStatus中自动调用
        }
    }
    
    // 应用启动时自动请求位置权限
    LaunchedEffect(Unit) {
        if (!uiState.isLocationEnabled && !hasRequestedPermission) {
            hasRequestedPermission = true
            locationPermissionLauncher.launch(LOCATION_PERMISSIONS)
        }
    }
    
    // 监听搜索查询变化
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotEmpty()) {
            viewModel.searchCities(searchQuery)
        }
    }
    
    // 监听位置城市变化，自动同步到小组件
    LaunchedEffect(uiState.locationCity) {
        uiState.locationCity?.let { city ->
            // 保存位置城市到widget偏好设置
            context.getSharedPreferences("weather_widget", Context.MODE_PRIVATE)
                .edit {
                    putString("selected_city", city)
                }
            
            // 立即更新widget数据
            WeatherWidgetProvider.updateWidgetDataForCity(context, city)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("天气预报") },
                actions = {
                    IconButton(
                        onClick = { viewModel.refreshWeather() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "刷新"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 位置按钮
            LocationButton(
                isLocationEnabled = uiState.isLocationEnabled,
                isLoading = uiState.isLoading,
                locationCity = uiState.locationCity,
                onLocationClick = {
                    if (uiState.isLocationEnabled) {
                        viewModel.getCurrentLocationWeather()
                    } else {
                        locationPermissionLauncher.launch(LOCATION_PERMISSIONS)
                    }
                }
            )
            
            // 城市搜索栏
            CitySearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                searchResults = uiState.searchCities,
                onCitySelected = { city ->
                    viewModel.selectCity(city)
                    searchQuery = ""

                    // 保存选中的城市到widget偏好设置
                    context.getSharedPreferences("weather_widget", Context.MODE_PRIVATE)
                        .edit {
                            putString("selected_city", city)
                        }
                        
                    // 立即更新widget数据
                    WeatherWidgetProvider.updateWidgetDataForCity(context, city)
                },
                isSearching = uiState.isSearching
            )
            
            // 错误信息显示
            uiState.errorMessage?.let { error ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        
                        TextButton(
                            onClick = { viewModel.clearError() }
                        ) {
                            Text("关闭")
                        }
                    }
                }
            }
            
            // 加载状态
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            // 当前天气卡片
            uiState.currentWeather?.let { weather ->
                WeatherCard(
                    location = weather.location,
                    current = weather.current
                )
            }
            
            // 语音播报卡片
            VoiceBroadcastCard(
                weatherResponse = uiState.currentWeather
            )
            
            // 天气预报卡片
            uiState.forecastWeather?.let { forecast ->
                if (forecast.forecast.forecastday.isNotEmpty()) {
                    ForecastCard(
                        forecastDays = forecast.forecast.forecastday
                    )
                }
            }
        }
    }
}