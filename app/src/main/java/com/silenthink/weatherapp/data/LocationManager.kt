package com.silenthink.weatherapp.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class LocationManager(private val context: Context) {
    
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val geocoder = Geocoder(context, Locale.getDefault())
    
    /**
     * 检查是否有位置权限
     */
    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * 获取当前位置
     */
    suspend fun getCurrentLocation(): Result<Location> = withContext(Dispatchers.IO) {
        try {
            if (!hasLocationPermission()) {
                return@withContext Result.failure(SecurityException("没有位置权限"))
            }
            
            val providers = locationManager.getProviders(true)
            if (providers.isEmpty()) {
                return@withContext Result.failure(Exception("没有可用的位置提供者"))
            }
            
            var lastKnownLocation: Location? = null
            
            // 尝试从GPS获取位置
            if (providers.contains(LocationManager.GPS_PROVIDER)) {
                try {
                    lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                } catch (e: SecurityException) {
                    // 忽略安全异常，尝试其他提供者
                }
            }
            
            // 如果GPS没有位置，尝试网络位置
            if (lastKnownLocation == null && providers.contains(LocationManager.NETWORK_PROVIDER)) {
                try {
                    lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                } catch (e: SecurityException) {
                    // 忽略安全异常
                }
            }
            
            if (lastKnownLocation != null) {
                Result.success(lastKnownLocation)
            } else {
                Result.failure(Exception("无法获取位置信息"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 根据经纬度获取城市名称
     */
    suspend fun getCityFromLocation(latitude: Double, longitude: Double): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (!Geocoder.isPresent()) {
                return@withContext Result.failure(Exception("设备不支持地理编码"))
            }
            
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            
            if (addresses?.isNotEmpty() == true) {
                val address = addresses[0]
                val city = address.locality ?: address.subAdminArea ?: address.adminArea
                
                if (!city.isNullOrEmpty()) {
                    Result.success(city)
                } else {
                    Result.failure(Exception("无法获取城市信息"))
                }
            } else {
                Result.failure(Exception("无法解析位置信息"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 获取当前位置的城市名称
     */
    suspend fun getCurrentCity(): Result<String> {
        return getCurrentLocation().fold(
            onSuccess = { location ->
                getCityFromLocation(location.latitude, location.longitude)
            },
            onFailure = { exception ->
                Result.failure(exception)
            }
        )
    }
} 