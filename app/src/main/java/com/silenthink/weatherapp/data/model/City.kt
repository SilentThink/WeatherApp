package com.silenthink.weatherapp.data.model

// 城市数据类
data class City(
    val id: String, // 城市ID
    val name: String, // 城市名称
    val region: String, // 地区
    val country: String, // 国家
    val lat: Double, // 纬度
    val lon: Double, // 经度
    val isDefault: Boolean = false, // 是否为默认城市
    val lastUpdated: Long = System.currentTimeMillis() // 最后更新时间
)

// 搜索城市响应数据类
data class SearchCityResponse(
    val id: String, // 城市ID
    val name: String, // 城市名称
    val region: String, // 地区
    val country: String, // 国家
    val lat: Double, // 纬度
    val lon: Double, // 经度
    val url: String // 城市信息URL
)