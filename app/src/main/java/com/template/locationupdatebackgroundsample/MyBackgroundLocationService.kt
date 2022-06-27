package com.template.locationupdatebackgroundsample

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest

interface MyBackgroundLocationService {
    val locationClient: FusedLocationProviderClient
    val locationRequest: LocationRequest
    val locations: MutableLiveData<List<LocationData>>
    fun startLocationUpdates()
    fun stopLocationUpdates()

    companion object {
        const val REQUEST_CHECK_SETTINGS: Int = 100
    }
}