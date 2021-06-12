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
    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        // 更新間隔(ms) OS:8以降のデバイス（targetSdkVersionに関係なく）ではアプリが存在しなくなったときに、この間隔よりも少ないintervalで更新を受信する.
        interval = TimeUnit.SECONDS.toMillis(60)
        fastestInterval = TimeUnit.SECONDS.toMillis(30) // 最速更新間隔(ms)
        maxWaitTime =
            TimeUnit.MINUTES.toMillis(2)// バッチロケーション更新が配信される最大時間を設定します。 更新は、この間隔よりも早く配信される場合があります。
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

    fun checkLocationSettings(activity: Activity) {
        val builder = LocationSettingsRequest.Builder().apply {
            addLocationRequest(locationRequest)
        }
        val task: Task<LocationSettingsResponse> =
            LocationServices.getSettingsClient(context).checkLocationSettings(builder.build())
        task.addOnCompleteListener { task ->
            kotlin.runCatching {
                val response = task.getResult(ApiException::class.java)
                // 位置情報設定が許可されている(正常系).
                Log.d(LOG_TAG, "isBlePresent: " + response.locationSettingsStates?.isBlePresent)
                Log.d(LOG_TAG, "isBleUsable: " + response.locationSettingsStates?.isBleUsable)
                Log.d(LOG_TAG, "isGpsPresent: " + response.locationSettingsStates?.isGpsPresent)
                Log.d(LOG_TAG, "isGpsUsable: " + response.locationSettingsStates?.isGpsUsable)
                Log.d(
                    LOG_TAG,
                    "isLocationPresent: " + response.locationSettingsStates?.isLocationPresent
                )
                Log.d(
                    LOG_TAG,
                    "isLocationUsable: " + response.locationSettingsStates?.isLocationUsable
                )
                Log.d(
                    LOG_TAG,
                    "isNetworkLocationPresent: " + response.locationSettingsStates?.isNetworkLocationPresent
                )
                Log.d(
                    LOG_TAG,
                    "isNetworkLocationUsable: " + response.locationSettingsStates?.isNetworkLocationUsable
                )
            }.onFailure {
                if (it !is ApiException) return@onFailure
                when (it.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        Log.d(LOG_TAG, "statusCode: RESOLUTION_REQUIRED")
                        // Location設定が許可されていないので、許可ダイアログを表示する
                        try {
                            // ダイアログを表示する. 処理結果は、onActivityResult()にコールバックされる
                            val resolvable: ResolvableApiException? = it as? ResolvableApiException
                            resolvable?.startResolutionForResult(
                                activity,
                                REQUEST_CHECK_SETTINGS
                            )
                        } catch (e: IntentSender.SendIntentException) {
                            // Ignore the error.
                            Log.e(LOG_TAG, "error: $e")
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        // 位置情報の設定変更が禁止されている。
                        Log.d(LOG_TAG, "statusCode: SETTINGS_CHANGE_UNAVAILABLE")
                    }
                }
            }
        }
    }

    @MainThread
    fun stopLocationUpdates() {
        locationClient.removeLocationUpdates(locationCallback)
    }

    companion object {
        const val LOG_TAG = "MyLocationManager"

        @SuppressLint("StaticFieldLeak")
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