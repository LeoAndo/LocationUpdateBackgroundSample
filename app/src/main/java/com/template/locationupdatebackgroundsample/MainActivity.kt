package com.template.locationupdatebackgroundsample

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.LocationSettingsStates
import com.template.locationupdatebackgroundsample.MyBackgroundLocationManager.Companion.REQUEST_CHECK_SETTINGS
import com.template.locationupdatebackgroundsample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var myBackgroundLocationManager: MyBackgroundLocationManager? = null

    // Android 11以上の場合、Manifest.permission.ACCESS_BACKGROUND_LOCATION パーミッションは他の権限と一緒に要求すべきではない。
    // https://stackoverflow.com/questions/66475027/activityresultlauncher-with-requestmultiplepermissions-contract-doesnt-show-per
    /*
    private val requestLocationPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { granted ->
            val hasReject = granted.values.any { !it }
            if (hasReject) return@registerForActivityResult
            Log.d(LOG_TAG, "granted: $granted")
            myLocationManager?.getLocation()
        }

     */
    private val requestFineLocationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            Log.d(LOG_TAG, "granted: $granted")
        }
    private val requestBackgroundLocationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            Log.d(LOG_TAG, "granted: $granted")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myBackgroundLocationManager = MyBackgroundLocationManager.getInstance(this)

        binding.buttonCheckLocationSetting.setOnClickListener {
            myBackgroundLocationManager?.checkLocationSettings(this@MainActivity)
        }

        val isEnabledBKLocationButton: Boolean = (Build.VERSION_CODES.Q <= Build.VERSION.SDK_INT)
        binding.buttonAccessBackgroundLocation.isEnabled = isEnabledBKLocationButton
        binding.buttonAccessBackgroundLocation.setOnClickListener {
            requestBackgroundLocationPermission.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }

        binding.buttonAccessFineLocation.setOnClickListener {
            /*
requestLocationPermissions.launch(
    arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )
)
 */
            requestFineLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        binding.buttonStart.setOnClickListener {
            myBackgroundLocationManager?.startLocationUpdates()
        }
        binding.buttonStop.setOnClickListener {
            myBackgroundLocationManager?.stopLocationUpdates()
        }
        myBackgroundLocationManager?.locations?.observe(this) {
            Log.d(LOG_TAG, "observe")
            val text = StringBuilder()
            text.append(binding.textView.text).append("\n")
            it.forEach { locationData ->
                text.append("lat: ${locationData.latitude} lon: ${locationData.longitude} isAppForeground: ${locationData.isAppForeground} date: ${locationData.date}")
                text.append("\n")
            }

            binding.textView.text = text
        }
    }

    override fun onDestroy() {
        myBackgroundLocationManager?.stopLocationUpdates()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> {
                val message = when (resultCode) {
                    Activity.RESULT_OK -> {
                        "All required changes were successfully made."
                    }
                    Activity.RESULT_CANCELED -> {
                        "The user was asked to change settings, but chose not to"
                    }
                    else -> "unknown..."
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                if (resultCode == Activity.RESULT_OK) {
                    // クライアントが使用可能なロケーションプロバイダーに関心がある場合は、
                    // LocationSettingsStates.fromIntent（Intent）を呼び出すことにより取得可能です。
                    val states: LocationSettingsStates? =
                        data?.let { LocationSettingsStates.fromIntent(it) }
                    Log.d(LOG_TAG, "isGpsPresent: " + states?.isGpsPresent)
                    Log.d(LOG_TAG, "isGpsUsable: " + states?.isGpsUsable)
                }
            }
        }
    }

    companion object {
        const val LOG_TAG = "MainActivity"
    }
}