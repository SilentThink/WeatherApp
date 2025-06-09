package com.silenthink.weatherapp.data.repository

import com.silenthink.weatherapp.data.api.ApiConfig
import com.silenthink.weatherapp.data.model.City
import com.silenthink.weatherapp.data.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

// WeatherRepository 类，用于处理天气数据的获取
class WeatherRepository {
    
    // 初始化 ApiConfig 中的 weatherApi
    private val api = ApiConfig.weatherApi
    
    // 获取当前天气信息
    suspend fun getCurrentWeather(city: String): Flow<Result<WeatherResponse>> = flow {
        try {
            val response = api.getCurrentWeather(
                apiKey = ApiConfig.API_KEY,
                query = city
            )
            
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("获取天气数据失败: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    // 获取天气预报信息
    suspend fun getForecastWeather(city: String, days: Int = 7): Flow<Result<WeatherResponse>> = flow {
        try {
            val response = api.getForecastWeather(
                apiKey = ApiConfig.API_KEY,
                query = city,
                days = days
            )
            
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("获取预报数据失败: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    // 搜索城市信息
    suspend fun searchCities(query: String): Flow<Result<List<City>>> = flow {
        try {
            val response = api.searchCities(
                apiKey = ApiConfig.API_KEY,
                query = query
            )
            
            if (response.isSuccessful && response.body() != null) {
                val cities = response.body()!!.map { searchResult ->
                    City(
                        id = "${searchResult.lat},${searchResult.lon}",
                        name = searchResult.name,
                        region = searchResult.region,
                        country = searchResult.country,
                        lat = searchResult.lat,
                        lon = searchResult.lon
                    )
                }
                emit(Result.success(cities))
            } else {
                emit(Result.failure(Exception("搜索城市失败: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    // 获取历史天气信息
    suspend fun getHistoryWeather(city: String, date: String): Flow<Result<WeatherResponse>> = flow {
        try {
            val response = api.getHistoryWeather(
                apiKey = ApiConfig.API_KEY,
                query = city,
                date = date
            )
            
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("获取历史天气失败: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}