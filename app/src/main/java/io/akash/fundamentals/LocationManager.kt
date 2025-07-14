package io.akash.fundamentals

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.widget.Toast
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority

@SuppressLint("MissingPermission")
class LocationManager(
    private val context: Context
) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    fun getLocation(
        onSuccess: (latitude: String, longitude: String) -> Unit
    ) {
        fusedLocationClient
            .lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val latitude = location.latitude.toString().takeLast(4)
                    val longitude = location.longitude.toString().takeLast(4)
                    onSuccess(latitude, longitude)
                } else {
                    Toast.makeText(context, "Location not available yet", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to get location", Toast.LENGTH_SHORT).show()
            }
    }

    fun trackLocation() {

    }

}