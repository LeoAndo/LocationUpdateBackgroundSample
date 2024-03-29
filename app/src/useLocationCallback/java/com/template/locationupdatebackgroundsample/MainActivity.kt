package com.template.locationupdatebackgroundsample

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.template.locationupdatebackgroundsample.MyBackgroundLocationService.Companion.REQUEST_CHECK_SETTINGS
import com.template.locationupdatebackgroundsample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var myBackgroundLocationService: MyBackgroundLocationService

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
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    Log.d(LOG_TAG, "Precise location access granted.")
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    Log.d(LOG_TAG, "Only approximate location access granted.")
                }
                else -> {
                    Log.d(LOG_TAG, "No location access granted.")
                }
            }
        }
    private val requestBackgroundLocationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            Log.d(LOG_TAG, "granted: $granted")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myBackgroundLocationService = MyBackgroundLocationServiceImpl.getInstance(this)

        binding.buttonCheckLocationSetting.setOnClickListener {
            checkLocationSettings(myBackgroundLocationService.locationRequest)
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
            requestFineLocationPermission.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
        binding.buttonStart.setOnClickListener {
            myBackgroundLocationService.startLocationUpdates()
        }
        binding.buttonStop.setOnClickListener {
            myBackgroundLocationService.stopLocationUpdates()
        }
        myBackgroundLocationService.locations.observe(this) {
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
        myBackgroundLocationService.stopLocationUpdates()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> {
                handleActivityResultCheckSettings(resultCode, data)
            }
        }
    }

    companion object {
        const val LOG_TAG = "MainActivity"
    }
}