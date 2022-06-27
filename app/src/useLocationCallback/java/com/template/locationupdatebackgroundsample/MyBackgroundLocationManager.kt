package com.template.locationupdatebackgroundsample

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.annotation.MainThread
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * LocationCallbackを使うパターン.
 */
class MyBackgroundLocationManager private constructor(private val context: Context) {
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
                        isAppForeground = Utils.isAppInForeground(context),
                        date = Date(it.time)
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
    val locationRequest: LocationRequest = LocationRequest.create().apply {
        // 更新間隔(ms) OS:8以降のデバイス（targetSdkVersionに関係なく）ではアプリが存在しなくなったときに、この間隔よりも少ないintervalで更新を受信する.
        interval = TimeUnit.SECONDS.toMillis(5) // 5秒
        fastestInterval = TimeUnit.SECONDS.toMillis(1) // 最速更新間隔(ms)
        maxWaitTime =
            TimeUnit.MINUTES.toMillis(2)// バッチロケーション更新が配信される最大時間を設定します。 更新は、この間隔よりも早く配信される場合があります。
        priority = Priority.PRIORITY_HIGH_ACCURACY
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

    companion object {
        const val LOG_TAG = "MyLocationManager"

        @Volatile
        private var INSTANCE: MyBackgroundLocationManager? = null
        fun getInstance(context: Context): MyBackgroundLocationManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MyBackgroundLocationManager(context).also { INSTANCE = it }
            }
        }

        const val REQUEST_CHECK_SETTINGS: Int = 100
    }
}