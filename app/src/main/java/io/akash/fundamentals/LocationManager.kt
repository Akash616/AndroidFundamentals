package io.akash.fundamentals

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await

@SuppressLint("MissingPermission")
class LocationManager(
    private val context: Context
) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val systemLocationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    suspend fun getLocation(
        onSuccess: (latitude: String, longitude: String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (!hasLocationPermissions()) {
            onFailure("Location permissions not granted")
            return
        }

        if (!isLocationEnabled()) {
            onFailure("Location services are disabled. Please enable them in Settings.")
            return
        }

        try {
            val location = fusedLocationClient.lastLocation.await()
            if (location != null) {
                val latitude = location.latitude.toString().takeLast(4)
                val longitude = location.longitude.toString().takeLast(4)
                onSuccess(latitude, longitude)
            } else {
                // Fallback: Request a fresh location
                val currentLocation = fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    null
                ).await()
                if (currentLocation != null) {
                    val latitude = currentLocation.latitude.toString().takeLast(4)
                    val longitude = currentLocation.longitude.toString().takeLast(4)
                    onSuccess(latitude, longitude)
                } else {
                    onFailure("Unable to retrieve location")
                }
            }
        } catch (e: Exception) {
            onFailure("Error retrieving location: ${e.message}")
        }
    }

    private fun hasLocationPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    fun isLocationEnabled(): Boolean {
        return systemLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                systemLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}