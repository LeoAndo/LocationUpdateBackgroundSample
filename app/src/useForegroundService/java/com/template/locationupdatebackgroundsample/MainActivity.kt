package com.template.locationupdatebackgroundsample

import android.Manifest
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.template.locationupdatebackgroundsample.MyBackgroundLocationService.Companion.REQUEST_CHECK_SETTINGS
import com.template.locationupdatebackgroundsample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var myBackgroundLocationService: MyBackgroundLocationService
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

    private val requestPostNotificationsPermission =
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

        val isEnabledBKLocationButton = false
        binding.buttonAccessBackgroundLocation.isEnabled = isEnabledBKLocationButton

        binding.buttonAccessFineLocation.setOnClickListener {
            requestFineLocationPermission.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
        binding.buttonStart.setOnClickListener {
            startForegroundService(Intent(this, MyForegroundService::class.java))
        }
        binding.buttonStop.setOnClickListener {
            stopService(Intent(this, MyForegroundService::class.java))
        }

        binding.buttonGotoNotificationSettings.setOnClickListener {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, BuildConfig.APPLICATION_ID)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        }

        // TODO: 定数を使う！
        binding.buttonRequestPushNotification.isEnabled = (33 <= Build.VERSION.SDK_INT)
        binding.buttonRequestPushNotification.setOnClickListener {
            requestPostNotificationsPermission.launch("android.permission.POST_NOTIFICATIONS")
        }

        binding.buttonAreNotificationsEnabled.isEnabled = true
        binding.buttonAreNotificationsEnabled.setOnClickListener {
            val notificationManager = getSystemService(NotificationManager::class.java)
            Toast.makeText(
                this,
                "areNotificationsEnabled: " + notificationManager.areNotificationsEnabled(),
                Toast.LENGTH_SHORT
            ).show()
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
        stopService(Intent(this, MyForegroundService::class.java))
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