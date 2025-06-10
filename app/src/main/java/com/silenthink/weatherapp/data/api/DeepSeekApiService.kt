package com.silenthink.weatherapp.data.api

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.suspendCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class WeatherReportRequest(
    val city: String,
    val temperature: String,
    val condition: String,
    val humidity: String,
    val windSpeed: String,
    val airQuality: String? = null
)

class DeepSeekApiService {
    
    private val client = OkHttpClient()
    
    // 注意：请在实际使用时替换为您的DeepSeek API密钥
    private val apiKey = "sk-cd28597d65ef4227bdd211b794b70b58"
    
    suspend fun generateWeatherReport(weatherData: WeatherReportRequest): String {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = buildPrompt(weatherData)
                callDeepSeekApi(prompt)
            } catch (e: Exception) {
                // 如果API调用失败，返回默认播报内容
                generateDefaultReport(weatherData)
            }
        }
    }
    
    private fun buildPrompt(weatherData: WeatherReportRequest): String {
        return """
作为一个幽默风趣的天气播报员，请为以下天气信息生成一段简洁有趣的中文播报，包含实用的生活建议：

城市：${weatherData.city}
温度：${weatherData.temperature}
天气状况：${weatherData.condition}
湿度：${weatherData.humidity}
风速：${weatherData.windSpeed}
${weatherData.airQuality?.let { "空气质量：$it" } ?: ""}

要求：
1. 语言幽默风趣，但不失实用性
2. 包含穿衣、出行建议
3. 总长度控制在100字以内
4. 纯中文输出，不要包含任何英文
5. 语调自然，适合语音播报

请直接返回播报内容，不要包含任何解释或额外文字。
        """.trimIndent()
    }
    
    private suspend fun callDeepSeekApi(prompt: String): String {
        return suspendCoroutine { continuation ->
            val json = JSONObject().apply {
                put("model", "deepseek-chat")
                put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", prompt)
                    })
                })
                put("max_tokens", 200)
                put("temperature", 0.7)
            }
            
            val requestBody = RequestBody.create(
                "application/json".toMediaType(),
                json.toString()
            )
            
            val request = Request.Builder()
                .url("https://api.deepseek.com/v1/chat/completions")
                .post(requestBody)
                .header("Authorization", "Bearer $apiKey")
                .header("Content-Type", "application/json")
                .build()
            
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }
                
                override fun onResponse(call: Call, response: Response) {
                    try {
                        val responseBody = response.body?.string()
                        if (response.isSuccessful && responseBody != null) {
                            val jsonResponse = JSONObject(responseBody)
                            val content = jsonResponse
                                .getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content")
                                .trim()
                            continuation.resume(content)
                        } else {
                            continuation.resumeWithException(Exception("API调用失败: ${response.code}"))
                        }
                    } catch (e: Exception) {
                        continuation.resumeWithException(e)
                    }
                }
            })
        }
    }
    
    private fun generateDefaultReport(weatherData: WeatherReportRequest): String {
        val temperature = weatherData.temperature.replace("°C", "").toIntOrNull() ?: 20
        
        val tempAdvice = when {
            temperature < 0 -> "天寒地冻，记得多穿点，别冻成冰棍了！"
            temperature < 10 -> "有点凉意，外套不能少，温暖最重要。"
            temperature < 20 -> "温度适中，轻装出行正当时。"
            temperature < 30 -> "天气不错，心情也要跟着好起来！"
            else -> "高温预警，记得防暑降温，多喝水！"
        }
        
        val conditionAdvice = when {
            weatherData.condition.contains("雨") -> "记得带伞，别被雨淋成落汤鸡。"
            weatherData.condition.contains("雪") -> "雪花飞舞，路面湿滑，出行要小心。"
            weatherData.condition.contains("雾") -> "大雾弥漫，开车慢行，安全第一。"
            weatherData.condition.contains("晴") -> "阳光明媚，正是出门好时光。"
            else -> "天气多变，随机应变最聪明。"
        }
        
        return "${weatherData.city}现在${weatherData.temperature}，${weatherData.condition}。$tempAdvice $conditionAdvice"
    }
} 