package com.silenthink.weatherapp.data.api

import com.silenthink.weatherapp.data.model.SearchCityResponse
import com.silenthink.weatherapp.data.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// WeatherApi 接口，用于定义与 WeatherAPI 的交互
interface WeatherApi {
    
    // 获取当前天气信息
    @GET("current.json")
    suspend fun getCurrentWeather(
        @Query("key") apiKey: String, // API 密钥
        @Query("q") query: String, // 查询字符串，可能是城市名或邮编
        @Query("aqi") aqi: String = "no" // 是否包含空气质量信息，默认不包含
    ): Response<WeatherResponse>
    
    // 获取天气预报信息
    @GET("forecast.json")
    suspend fun getForecastWeather(
        @Query("key") apiKey: String, // API 密钥
        @Query("q") query: String, // 查询字符串，可能是城市名或邮编
        @Query("days") days: Int = 7, // 预报天数，默认为 7
        @Query("aqi") aqi: String = "no", // 是否包含空气质量信息，默认不包含
        @Query("alerts") alerts: String = "no" // 是否包含警报信息，默认不包含
    ): Response<WeatherResponse>
    
    // 搜索城市信息
    @GET("search.json")
    suspend fun searchCities(
        @Query("key") apiKey: String, // API 密钥
        @Query("q") query: String // 查询字符串，可能是城市名或邮编
    ): Response<List<SearchCityResponse>>
    
    // 获取历史天气信息
    @GET("history.json")
    suspend fun getHistoryWeather(
        @Query("key") apiKey: String, // API 密钥
        @Query("q") query: String, // 查询字符串，可能是城市名或邮编
        @Query("dt") date: String // 日期，格式为 YYYY-MM-DD
    ): Response<WeatherResponse>
}