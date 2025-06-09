package com.silenthink.weatherapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.silenthink.weatherapp.data.model.WeatherResponse
import com.silenthink.weatherapp.data.model.City
import com.silenthink.weatherapp.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// UI 状态数据类
data class WeatherUiState(
    val currentWeather: WeatherResponse? = null,
    val forecastWeather: WeatherResponse? = null,
    val searchCities: List<City> = emptyList(),
    val selectedCity: String = "北京", // 默认城市
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSearching: Boolean = false
)

class WeatherViewModel : ViewModel() {
    
    private val repository = WeatherRepository()
    
    // 私有可变状态
    private val _uiState = MutableStateFlow(WeatherUiState())
    // 公开只读状态
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()
    
    init {
        // 初始化时加载默认城市的天气
        loadWeatherData()
    }
    
    // 加载天气数据
    fun loadWeatherData(city: String = _uiState.value.selectedCity) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                selectedCity = city
            )
            
            try {
                // 同时获取当前天气和预报天气
                launch {
                    repository.getCurrentWeather(city).collect { result ->
                        result.fold(
                            onSuccess = { weather ->
                                _uiState.value = _uiState.value.copy(
                                    currentWeather = weather,
                                    isLoading = false
                                )
                            },
                            onFailure = { exception ->
                                _uiState.value = _uiState.value.copy(
                                    errorMessage = exception.message ?: "获取当前天气失败",
                                    isLoading = false
                                )
                            }
                        )
                    }
                }
                
                launch {
                    repository.getForecastWeather(city, 7).collect { result ->
                        result.fold(
                            onSuccess = { forecast ->
                                _uiState.value = _uiState.value.copy(
                                    forecastWeather = forecast
                                )
                            },
                            onFailure = { exception ->
                                // 预报失败不影响当前天气显示
                                println("获取天气预报失败: ${exception.message}")
                            }
                        )
                    }
                }
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "加载天气数据失败",
                    isLoading = false
                )
            }
        }
    }
    
    // 搜索城市
    fun searchCities(query: String) {
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(searchCities = emptyList())
            return
        }
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearching = true)
            
            repository.searchCities(query).collect { result ->
                result.fold(
                    onSuccess = { cities ->
                        _uiState.value = _uiState.value.copy(
                            searchCities = cities,
                            isSearching = false
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            searchCities = emptyList(),
                            isSearching = false
                        )
                        println("搜索城市失败: ${exception.message}")
                    }
                )
            }
        }
    }
    
    // 选择城市
    fun selectCity(city: String) {
        loadWeatherData(city)
        // 清空搜索结果
        _uiState.value = _uiState.value.copy(searchCities = emptyList())
    }
    
    // 刷新天气数据
    fun refreshWeather() {
        loadWeatherData()
    }
    
    // 清除错误信息
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}