package com.template.locationupdatebackgroundsample

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.annotation.MainThread
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*
import java.util.concurrent.TimeUnit

class MyLocationManager private constructor(private val context: Context) {
    private val locationClient: FusedLocationProviderClient
        get() = LocationServices.getFusedLocationProviderClient(
            context
        )
    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            Log.d(LOG_TAG, "currentThread is: " + Thread.currentThread())
            locations.value =
                locationResult.locations.map {
                    LocationData(
                        latitude = it.latitude,
                        longitude = it.longitude,
                        isAppForeground = isAppInForeground(context)
                    )
                }
        }

        // android.permission.ACCESS_BACKGROUND_LOCATIONが許可されてない場合、
        // アプリがバックグランドに遷移すると、onLocationAvailabilityがfalseになる。
        override fun onLocationAvailability(locationAvailability: LocationAvailability) {
            super.onLocationAvailability(locationAvailability)
            Log.d(LOG_TAG, "currentThread is: " + Thread.currentThread())
            Log.d(LOG_TAG, "isLocationAvailable: " + locationAvailability.isLocationAvailable)
        }
    }
    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        // 更新間隔(ms) OS:8以降のデバイス（targetSdkVersionに関係なく）ではアプリが存在しなくなったときに、この間隔よりも少ないintervalで更新を受信する.
        interval = TimeUnit.SECONDS.toMillis(3)
        fastestInterval = TimeUnit.SECONDS.toMillis(1) // 最速更新間隔(ms)
        maxWaitTime = TimeUnit.SECONDS.toMillis(5)// バッチロケーション更新が配信される最大時間を設定します。 更新は、この間隔よりも早く配信される場合があります。
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
    val locations = MutableLiveData<List<LocationData>>()

    @MainThread
    fun startLocationUpdates() {
        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            return
        }
        // 第３引数はコールバックのスレッドを指定する.
        locationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    @MainThread
    fun stopLocationUpdates() {
        locationClient.removeLocationUpdates(locationCallback)
    }

    /**
     * debugコード(本番コードには使用してはダメ).
     * アプリが現在フォアグランド中かどうか判定する.
     */
    private fun isAppInForeground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false

        appProcesses.forEach { appProcess ->
            if (appProcess.importance ==
                ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                appProcess.processName == context.packageName
            ) {
                return true
            }
        }
        return false
    }

    companion object {
        const val LOG_TAG = "MyLocationManager"

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: MyLocationManager? = null
        fun getInstance(context: Context): MyLocationManager {
            return INSTANCE ?: synchronized(this) {
                MyLocationManager(context).also { INSTANCE = it }
            }
        }
    }
}