package com.template.locationupdatebackgroundsample

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task

fun Activity.handleActivityResultCheckSettings(resultCode: Int, data: Intent?) {
    val LOG_TAG = "handleActivityResultCheckSettings"
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

fun Activity.checkLocationSettings(locationRequest: LocationRequest) {
    val LOG_TAG = "checkLocationSettings"
    val builder = LocationSettingsRequest.Builder().apply {
        addLocationRequest(locationRequest)
    }
    val task: Task<LocationSettingsResponse> =
        LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())
    task.addOnCompleteListener { task ->
        kotlin.runCatching {
            val response = task.getResult(ApiException::class.java)
            Toast.makeText(this, "位置情報設定が許可されている(正常系).", Toast.LENGTH_SHORT).show()
            Log.d(LOG_TAG, "isBlePresent: " + response.locationSettingsStates?.isBlePresent)
            Log.d(LOG_TAG, "isBleUsable: " + response.locationSettingsStates?.isBleUsable)
            Log.d(LOG_TAG, "isGpsPresent: " + response.locationSettingsStates?.isGpsPresent)
            Log.d(LOG_TAG, "isGpsUsable: " + response.locationSettingsStates?.isGpsUsable)
            Log.d(
                LOG_TAG,
                "isLocationPresent: " + response.locationSettingsStates?.isLocationPresent
            )
            Log.d(LOG_TAG, "isLocationUsable: " + response.locationSettingsStates?.isLocationUsable)
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
                            this,
                            MyBackgroundLocationService.REQUEST_CHECK_SETTINGS
                        )
                    } catch (e: IntentSender.SendIntentException) {
                        // Ignore the error.
                        Log.e(LOG_TAG, "error: $e")
                    }
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    Toast.makeText(this, "位置情報の設定変更が禁止されている(異常系).", Toast.LENGTH_SHORT)
                        .show()
                    Log.d(LOG_TAG, "statusCode: SETTINGS_CHANGE_UNAVAILABLE")
                }
            }
        }
    }
}