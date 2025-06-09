package com.silenthink.weatherapp.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// ApiConfig 对象，用于配置和提供 Retrofit 实例
object ApiConfig {
    
    // WeatherAPI 的免费 API Key (需要去 https://www.weatherapi.com/ 注册获取)
    const val API_KEY = "c3e02c6193c74e319e265011250906"
    private const val BASE_URL = "https://api.weatherapi.com/v1/" // WeatherAPI 的基础 URL
    
    // 创建一个日志拦截器，用于打印 HTTP 请求和响应的日志
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // 设置日志级别为 BODY，以打印请求和响应的正文
    }
    
    // 创建一个 OkHttpClient 实例，用于发送 HTTP 请求
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor) // 添加日志拦截器
        .connectTimeout(30, TimeUnit.SECONDS) // 连接超时时间
        .readTimeout(30, TimeUnit.SECONDS) // 读取超时时间
        .writeTimeout(30, TimeUnit.SECONDS) // 写入超时时间
        .build()
    
    // 创建一个 Retrofit 实例，用于发送 HTTP 请求
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL) // 设置基础 URL
        .client(client) // 使用上面创建的 OkHttpClient 实例
        .addConverterFactory(GsonConverterFactory.create()) // 使用 Gson 进行 JSON 转换
        .build()
    
    // 使用 Retrofit 创建一个 WeatherApi 实例，用于调用 WeatherAPI
    val weatherApi: WeatherApi = retrofit.create(WeatherApi::class.java)
}