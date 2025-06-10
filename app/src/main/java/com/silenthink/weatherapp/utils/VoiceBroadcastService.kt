package com.silenthink.weatherapp.utils

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.iflytek.cloud.*
import com.iflytek.cloud.util.ResourceUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.suspendCoroutine
import kotlin.coroutines.resume

class VoiceBroadcastService(private val context: Context) {
    
    private var mTts: SpeechSynthesizer? = null
    private var isInitialized = false
    
    companion object {
        private const val TAG = "VoiceBroadcastService"
        // 请替换为您的讯飞语音appid
        private const val APPID = "f5b3c3fc"
    }
    
    // TTS监听器
    private val mTtsListener = object : SynthesizerListener {
        override fun onSpeakBegin() {
            Log.d(TAG, "开始播放")
        }
        
        override fun onSpeakPaused() {
            Log.d(TAG, "暂停播放")
        }
        
        override fun onSpeakResumed() {
            Log.d(TAG, "继续播放")
        }
        
        override fun onBufferProgress(percent: Int, beginPos: Int, endPos: Int, info: String?) {
            // 缓冲进度
        }
        
        override fun onSpeakProgress(percent: Int, beginPos: Int, endPos: Int) {
            // 播放进度
        }
        
        override fun onCompleted(error: SpeechError?) {
            if (error == null) {
                Log.d(TAG, "播放完成")
            } else {
                Log.e(TAG, "播放出错: ${error.errorDescription}")
            }
        }
        
        override fun onEvent(eventType: Int, arg1: Int, arg2: Int, obj: Bundle?) {
            // 事件处理
        }
    }
    
    /**
     * 初始化语音合成服务
     */
    suspend fun initialize(): Boolean {
        return withContext(Dispatchers.Main) {
            suspendCoroutine { continuation ->
                try {
                    // 创建单例对象
                    mTts = SpeechSynthesizer.createSynthesizer(context) { code ->
                        if (code != ErrorCode.SUCCESS) {
                            Log.e(TAG, "初始化失败,错误码：$code")
                            isInitialized = false
                            continuation.resume(false)
                        } else {
                            Log.d(TAG, "初始化成功")
                            isInitialized = true
                            // 设置参数
                            setTtsParams()
                            continuation.resume(true)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "初始化异常: ${e.message}")
                    isInitialized = false
                    continuation.resume(false)
                }
            }
        }
    }
    
    /**
     * 设置语音合成参数
     */
    private fun setTtsParams() {
        mTts?.let { tts ->
            // 清空参数
            tts.setParameter(SpeechConstant.PARAMS, null)
            
            // 根据合成引擎设置相应参数
            tts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD)
            // 设置在线合成发音人
            tts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan")
            
            // 设置合成语速
            tts.setParameter(SpeechConstant.SPEED, "50")
            // 设置合成音调
            tts.setParameter(SpeechConstant.PITCH, "50")
            // 设置合成音量
            tts.setParameter(SpeechConstant.VOLUME, "80")
            
            // 设置播放器音频流类型
            tts.setParameter(SpeechConstant.STREAM_TYPE, "3")
            
            // 设置播放合成音频打断音乐播放，默认为true
            tts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true")
            
            // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
            // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
            tts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav")
            tts.setParameter(SpeechConstant.TTS_AUDIO_PATH, 
                context.getExternalFilesDir(null)?.absolutePath + "/msc/tts.wav")
        }
    }
    
    /**
     * 开始语音播报
     */
    suspend fun startSpeaking(text: String): Boolean {
        return withContext(Dispatchers.Main) {
            if (!isInitialized) {
                Log.w(TAG, "TTS未初始化")
                return@withContext false
            }
            
            mTts?.let { tts ->
                val code = tts.startSpeaking(text, mTtsListener)
                if (code != ErrorCode.SUCCESS) {
                    Log.e(TAG, "语音合成失败,错误码: $code")
                    return@withContext false
                }
                return@withContext true
            } ?: run {
                Log.e(TAG, "TTS对象为空")
                return@withContext false
            }
        }
    }
    
    /**
     * 暂停播放
     */
    fun pauseSpeaking() {
        mTts?.pauseSpeaking()
    }
    
    /**
     * 继续播放
     */
    fun resumeSpeaking() {
        mTts?.resumeSpeaking()
    }
    
    /**
     * 停止播放
     */
    fun stopSpeaking() {
        mTts?.stopSpeaking()
    }
    
    /**
     * 检查是否正在播放
     */
    fun isSpeaking(): Boolean {
        return mTts?.isSpeaking ?: false
    }
    
    /**
     * 释放资源
     */
    fun release() {
        mTts?.let { tts ->
            tts.stopSpeaking()
            // 退出时释放连接
            tts.destroy()
        }
        mTts = null
        isInitialized = false
    }
    
    /**
     * 初始化讯飞语音SDK
     */
    fun initializeIflytek(context: Context) {
        // 应用程序入口处调用，避免手机内存过小，杀死后台进程后通过历史intent进入Activity造成SpeechUtility对象为null
        // 注意：此接口在非主进程调用会返回null对象，如需在非主进程使用语音功能建议增加业务判断
        SpeechUtility.createUtility(context, "${SpeechConstant.APPID}=$APPID")
    }
} 