package com.silenthink.weatherapp.data.model

import com.google.gson.annotations.SerializedName

// 主要天气信息
data class WeatherResponse(
    @SerializedName("location") val location: Location,
    @SerializedName("current") val current: Current,
    @SerializedName("forecast") val forecast: Forecast
)

// 位置信息
data class Location(
    @SerializedName("name") val name: String, // 位置名称
    @SerializedName("region") val region: String, // 地区
    @SerializedName("country") val country: String, // 国家
    @SerializedName("lat") val lat: Double, // 纬度
    @SerializedName("lon") val lon: Double, // 经度
    @SerializedName("tz_id") val tzId: String, // 时区ID
    @SerializedName("localtime") val localtime: String // 当地时间
)

// 当前天气
data class Current(
    @SerializedName("temp_c") val tempC: Double, // 摄氏温度
    @SerializedName("temp_f") val tempF: Double, // 华氏温度
    @SerializedName("condition") val condition: Condition, // 天气条件
    @SerializedName("wind_kph") val windKph: Double, // 风速（公里每小时）
    @SerializedName("wind_dir") val windDir: String, // 风向
    @SerializedName("pressure_mb") val pressureMb: Double, // 气压（毫巴）
    @SerializedName("humidity") val humidity: Int, // 湿度
    @SerializedName("cloud") val cloud: Int, // 云量
    @SerializedName("feelslike_c") val feelslikeC: Double, // 感觉温度（摄氏）
    @SerializedName("vis_km") val visKm: Double, // 能见度（公里）
    @SerializedName("uv") val uv: Double, // 紫外线指数
    @SerializedName("is_day") val isDay: Int // 是否为白天
)

// 天气状况
data class Condition(
    @SerializedName("text") val text: String, // 天气描述
    @SerializedName("icon") val icon: String, // 天气图标
    @SerializedName("code") val code: Int // 天气代码
)

// 预报天气
data class Forecast(
    @SerializedName("forecastday") val forecastday: List<ForecastDay> // 预报日期列表
)

// 预报日期
data class ForecastDay(
    @SerializedName("date") val date: String, // 日期
    @SerializedName("day") val day: Day, // 全天信息
    @SerializedName("hour") val hour: List<Hour> // 小时信息列表
)

// 全天信息
data class Day(
    @SerializedName("maxtemp_c") val maxtempC: Double, // 最高温度（摄氏）
    @SerializedName("mintemp_c") val mintempC: Double, // 最低温度（摄氏）
    @SerializedName("avgtemp_c") val avgtempC: Double, // 平均温度（摄氏）
    @SerializedName("condition") val condition: Condition, // 天气状况
    @SerializedName("avghumidity") val avghumidity: Int, // 平均湿度
    @SerializedName("daily_chance_of_rain") val dailyChanceOfRain: Int, // 日降雨概率
    @SerializedName("uv") val uv: Double // 紫外线指数
)

// 小时信息
data class Hour(
    @SerializedName("time") val time: String, // 时间
    @SerializedName("temp_c") val tempC: Double, // 温度（摄氏）
    @SerializedName("condition") val condition: Condition, // 天气状况
    @SerializedName("wind_kph") val windKph: Double, // 风速（公里每小时）
    @SerializedName("humidity") val humidity: Int, // 湿度
    @SerializedName("chance_of_rain") val chanceOfRain: Int, // 降雨概率
    @SerializedName("is_day") val isDay: Int // 是否为白天
)