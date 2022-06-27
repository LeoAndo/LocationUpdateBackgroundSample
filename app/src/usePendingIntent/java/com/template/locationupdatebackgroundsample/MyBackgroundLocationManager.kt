package com.template.locationupdatebackgroundsample

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*
import java.util.concurrent.TimeUnit

/**
 * PendingIntentを使うパターン
 */
class MyBackgroundLocationManager private constructor(private val context: Context) {
    private val locationClient: FusedLocationProviderClient
        get() = LocationServices.getFusedLocationProviderClient(
            context
        )
    val locationRequest: LocationRequest = LocationRequest.create().apply {
        // 更新間隔(ms) OS:8以降のデバイス（targetSdkVersionに関係なく）ではアプリが存在しなくなったときに、この間隔よりも少ないintervalで更新を受信する.
        interval = TimeUnit.SECONDS.toMillis(5) // 5秒
        fastestInterval = TimeUnit.SECONDS.toMillis(1) // 最速更新間隔(ms)
        maxWaitTime =
            TimeUnit.MINUTES.toMillis(2)// バッチロケーション更新が配信される最大時間を設定します。 更新は、この間隔よりも早く配信される場合があります。
        priority = Priority.PRIORITY_HIGH_ACCURACY
    }
    val locations = MutableLiveData<List<LocationData>>()

    /**
     * Creates default PendingIntent for location changes.
     *
     * Note: We use a BroadcastReceiver because on API level 26 and above (Oreo+), Android places
     * limits on Services.
     */
    private val locationUpdatePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, MyBackgroundLocationUpdatesBroadcastReceiver::class.java)
        intent.action = MyBackgroundLocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES
        PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE)
    }

    /**
     * Uses the FusedLocationProvider to start location updates if the correct fine locations are
     * approved.
     *
     * @throws SecurityException if ACCESS_FINE_LOCATION permission is removed before the
     * FusedLocationClient's requestLocationUpdates() has been completed.
     */
    @MainThread
    fun startLocationUpdates() {
        Log.d(LOG_TAG, "startLocationUpdates")
        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            return
        }

        try {
            // If the PendingIntent is the same as the last request (which it always is), this
            // request will replace any requestLocationUpdates() called before.
            locationClient.requestLocationUpdates(locationRequest, locationUpdatePendingIntent)
        } catch (permissionRevoked: SecurityException) {
            // Exception only occurs if the user revokes the FINE location permission before
            // requestLocationUpdates() is finished executing (very rare).
            Toast.makeText(
                context,
                "Location permission revoked; details: $permissionRevoked",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    @MainThread
    fun stopLocationUpdates() {
        Log.d(LOG_TAG, "stopLocationUpdates()")
        locationClient.removeLocationUpdates(locationUpdatePendingIntent)
    }

    companion object {
        const val LOG_TAG = "MyBackgroundLocationManager"

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