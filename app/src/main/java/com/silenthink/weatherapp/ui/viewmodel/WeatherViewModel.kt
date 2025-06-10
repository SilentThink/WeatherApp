package com.silenthink.weatherapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.silenthink.weatherapp.data.model.WeatherResponse
import com.silenthink.weatherapp.data.model.City
import com.silenthink.weatherapp.data.repository.WeatherRepository
import com.silenthink.weatherapp.data.LocationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.content.Context

// UI 状态数据类
data class WeatherUiState(
    val currentWeather: WeatherResponse? = null,
    val forecastWeather: WeatherResponse? = null,
    val searchCities: List<City> = emptyList(),
    val selectedCity: String = "Beijing", // 默认城市，使用英文名
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSearching: Boolean = false,
    val isLocationEnabled: Boolean = false,
    val locationCity: String? = null,
    val selectedDateIndex: Int = 0 // 添加选中的日期索引，0表示今天
)

class WeatherViewModel(private val context: Context? = null) : ViewModel() {
    
    private val repository = WeatherRepository()
    private val locationManager = context?.let { LocationManager(it) }
    
    // 私有可变状态
    private val _uiState = MutableStateFlow(WeatherUiState())
    // 公开只读状态
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()
    
    // 中文城市名到英文城市名/坐标的映射
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
        "石家庄" to "Shijiazhuang",
        "长沙" to "Changsha",
        "郑州" to "Zhengzhou",
        "济南" to "Jinan",
    )
    
    init {
        // 初始化时检查位置权限
        checkLocationPermission()
        
        // 如果有位置权限，尝试自动获取当前位置
        if (locationManager?.hasLocationPermission() == true) {
            getCurrentLocationWeather()
        } else {
            // 没有权限时加载默认城市的天气
            loadWeatherData()
        }
    }
    
    // 检查位置权限
    private fun checkLocationPermission() {
        locationManager?.let { manager ->
            val hasPermission = manager.hasLocationPermission()
            _uiState.value = _uiState.value.copy(isLocationEnabled = hasPermission)
        }
    }
    
    // 更新权限状态（供外部调用）
    fun updateLocationPermissionStatus() {
        checkLocationPermission()
        // 如果获得权限，自动获取当前位置
        if (_uiState.value.isLocationEnabled && _uiState.value.locationCity == null) {
            getCurrentLocationWeather()
        }
    }
    
    // 转换城市名称（如果是中文则转换为英文）
    private fun convertCityName(cityName: String): String {
        return cityMapping[cityName] ?: cityName
    }
    
    // 获取当前位置的城市并设置天气
    fun getCurrentLocationWeather() {
        locationManager?.let { manager ->
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null
                )
                
                manager.getCurrentCity().fold(
                    onSuccess = { city ->
                        _uiState.value = _uiState.value.copy(locationCity = city)
                        // 使用获取到的城市加载天气
                        loadWeatherData(city)
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            errorMessage = "获取位置失败: ${exception.message}",
                            isLoading = false
                        )
                    }
                )
            }
        } ?: run {
            _uiState.value = _uiState.value.copy(
                errorMessage = "位置服务不可用",
                isLoading = false
            )
        }
    }
    
    // 加载天气数据
    fun loadWeatherData(city: String = _uiState.value.selectedCity) {
        viewModelScope.launch {
            val convertedCity = convertCityName(city)
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                selectedCity = city
            )
            
            try {
                // 同时获取当前天气和预报天气
                launch {
                    repository.getCurrentWeather(convertedCity).collect { result ->
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
                    repository.getForecastWeather(convertedCity, 7).collect { result ->
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
            
            val convertedQuery = convertCityName(query)
            repository.searchCities(convertedQuery).collect { result ->
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
    
    // 选择日期
    fun selectDate(dateIndex: Int) {
        _uiState.value = _uiState.value.copy(selectedDateIndex = dateIndex)
    }
}