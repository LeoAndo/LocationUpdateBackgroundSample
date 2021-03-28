package com.template.locationupdatebackgroundsample

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var myLocationManager: MyLocationManager? = null

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
            myLocationManager?.startLocationUpdates()
        }
    private val requestBackgroundLocationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            Log.d(LOG_TAG, "granted: $granted")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        myLocationManager = MyLocationManager.getInstance(this)

        findViewById<Button>(R.id.button_access_background_location_on).setOnClickListener {
            requestBackgroundLocationPermission.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
        findViewById<Button>(R.id.buttonStart).setOnClickListener {
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
        findViewById<Button>(R.id.buttonStop).setOnClickListener {
            myLocationManager?.stopLocationUpdates()
        }
        myLocationManager?.locations?.observe(this, {
            val text = StringBuilder()
            text.append(findViewById<TextView>(R.id.textView).text).append("\n")
            it.forEach { locationData ->
                text.append("lat: " + locationData.latitude + " lon: " + locationData.longitude + " isAppForeground: " + locationData.isAppForeground)
                text.append("\n")
            }

            findViewById<TextView>(R.id.textView).text = text
        })
    }

    override fun onDestroy() {
        myLocationManager?.stopLocationUpdates()
        super.onDestroy()
    }

    companion object {
        const val LOG_TAG = "MainActivity"
    }
}