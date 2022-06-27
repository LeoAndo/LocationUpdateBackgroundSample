package com.template.locationupdatebackgroundsample

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationResult
import java.util.*

/**
 * Receiver for handling location updates.
 *
 * For apps targeting API level O and above
 * {@link android.app.PendingIntent#getBroadcast(Context, int, Intent, int)} should be used when
 * requesting location updates in the background. Due to limits on background services,
 * {@link android.app.PendingIntent#getService(Context, int, Intent, int)} should NOT be used.
 *
 *  Note: Apps running on "O" devices (regardless of targetSdkVersion) may receive updates
 *  less frequently than the interval specified in the
 *  {@link com.google.android.gms.location.LocationRequest} when the app is no longer in the
 *  foreground.
 */
class MyBackgroundLocationUpdatesBroadcastReceiver : BroadcastReceiver() {
    private var myBackgroundLocationManager: MyBackgroundLocationManager? = null
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive() context:$context, intent:$intent")
        myBackgroundLocationManager = MyBackgroundLocationManager.getInstance(context)
        if (intent.action == ACTION_PROCESS_UPDATES) {

            // Checks for location availability changes. ?でlint warning出てるけど、nullableの可能性あるので消しちゃだめ。
            LocationAvailability.extractLocationAvailability(intent)?.let { locationAvailability ->
                if (!locationAvailability.isLocationAvailable) {
                    Log.d(TAG, "Location services are no longer available!")
                }
            }

            LocationResult.extractResult(intent)?.let { locationResult ->
                val locations = locationResult.locations.map { location ->
                    LocationData(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        isAppForeground = Utils.isAppInForeground(context),
                        date = Date(location.time)
                    )
                }
                Log.d(TAG, "locations: $locations")
                if (locations.isNotEmpty()) {
                    Log.d(TAG, "postValue!")
                    myBackgroundLocationManager?.locations?.postValue(locations)
                }
            }
        }
    }

    companion object {
        const val ACTION_PROCESS_UPDATES = BuildConfig.APPLICATION_ID + ".action.PROCESS_UPDATES"
        private const val TAG = "MyBackgroundLocationUpdatesBroadcastReceiver"
    }
}