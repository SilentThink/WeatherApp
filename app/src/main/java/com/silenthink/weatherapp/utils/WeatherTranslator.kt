package com.silenthink.weatherapp.utils

object WeatherTranslator {
    
    private val weatherTranslations = mapOf(
        // 晴朗天气
        "Sunny" to "晴朗",
        "Clear" to "晴朗",
        "Fair" to "晴朗",
        
        // 多云天气
        "Partly cloudy" to "多云",
        "Partly Cloudy" to "多云",
        "Cloudy" to "阴天",
        "Overcast" to "阴霾",
        "Mostly cloudy" to "大部多云",
        "Mostly Cloudy" to "大部多云",
        
        // 雾霾天气
        "Mist" to "薄雾",
        "Fog" to "雾",
        "Freezing fog" to "冰雾",
        "Haze" to "霾",
        "Smoke" to "烟霾",
        
        // 雨天
        "Light rain" to "小雨",
        "Moderate rain" to "中雨",
        "Heavy rain" to "大雨",
        "Light rain shower" to "小阵雨",
        "Moderate or heavy rain shower" to "中到大阵雨",
        "Torrential rain shower" to "暴雨",
        "Light drizzle" to "小毛毛雨",
        "Patchy rain possible" to "可能有雨",
        "Patchy rain nearby" to "附近有雨",
        "Rain" to "雨",
        "Showers" to "阵雨",
        "Heavy showers" to "强阵雨",
        
        // 雪天
        "Light snow" to "小雪",
        "Moderate snow" to "中雪",
        "Heavy snow" to "大雪",
        "Light snow showers" to "小阵雪",
        "Moderate or heavy snow showers" to "中到大阵雪",
        "Patchy snow possible" to "可能有雪",
        "Patchy snow nearby" to "附近有雪",
        "Snow" to "雪",
        "Blowing snow" to "风雪",
        "Blizzard" to "暴风雪",
        
        // 雨雪混合
        "Light sleet" to "小雨夹雪",
        "Moderate or heavy sleet" to "中到大雨夹雪",
        "Light sleet showers" to "小阵雨夹雪",
        "Moderate or heavy sleet showers" to "中到大阵雨夹雪",
        "Sleet" to "雨夹雪",
        
        // 冰雹
        "Light showers of ice pellets" to "小冰雹阵雨",
        "Moderate or heavy showers of ice pellets" to "中到大冰雹阵雨",
        "Ice pellets" to "冰雹",
        "Hail" to "冰雹",
        
        // 雷暴
        "Thundery outbreaks possible" to "可能有雷暴",
        "Patchy light rain with thunder" to "局部小雨伴雷声",
        "Moderate or heavy rain with thunder" to "中到大雨伴雷暴",
        "Patchy light snow with thunder" to "局部小雪伴雷声",
        "Moderate or heavy snow with thunder" to "中到大雪伴雷暴",
        "Thunderstorm" to "雷暴",
        "Thunder" to "雷暴",
        
        // 冰冻天气
        "Freezing drizzle" to "冰毛毛雨",
        "Heavy freezing drizzle" to "强冰毛毛雨",
        "Light freezing rain" to "小冻雨",
        "Moderate or heavy freezing rain" to "中到大冻雨",
        "Freezing rain" to "冻雨",
        
        // 其他特殊天气
        "Sandstorm" to "沙尘暴",
        "Dust" to "扬尘",
        "Volcanic ash" to "火山灰",
        "Squalls" to "狂风",
        "Tornado" to "龙卷风"
    )
    
    /**
     * 将英文天气状况翻译成中文
     * @param englishCondition 英文天气状况
     * @return 中文天气状况，如果没有对应翻译则返回原文
     */
    fun translateWeatherCondition(englishCondition: String): String {
        return weatherTranslations[englishCondition] ?: englishCondition
    }
    
    /**
     * 将风向从英文翻译成中文
     * @param englishDirection 英文风向（如 N, NE, E, SE, S, SW, W, NW）
     * @return 中文风向
     */
    fun translateWindDirection(englishDirection: String): String {
        return when (englishDirection.uppercase()) {
            "N" -> "北"
            "NE" -> "东北"
            "E" -> "东"
            "SE" -> "东南"
            "S" -> "南"
            "SW" -> "西南"
            "W" -> "西"
            "NW" -> "西北"
            "NNE" -> "北东北"
            "ENE" -> "东东北"
            "ESE" -> "东东南"
            "SSE" -> "南东南"
            "SSW" -> "南西南"
            "WSW" -> "西西南"
            "WNW" -> "西西北"
            "NNW" -> "北西北"
            else -> englishDirection
        }
    }
} 