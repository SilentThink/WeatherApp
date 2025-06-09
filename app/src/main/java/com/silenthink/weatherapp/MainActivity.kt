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
import com.silenthink.weatherapp.ui.components.*
import com.silenthink.weatherapp.ui.theme.WeatherAppTheme
import com.silenthink.weatherapp.ui.viewmodel.WeatherViewModel

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
    val viewModel: WeatherViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    
    // 监听搜索查询变化
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotEmpty()) {
            viewModel.searchCities(searchQuery)
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
            // 城市搜索栏
            CitySearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                searchResults = uiState.searchCities,
                onCitySelected = { city ->
                    viewModel.selectCity(city)
                    searchQuery = ""
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