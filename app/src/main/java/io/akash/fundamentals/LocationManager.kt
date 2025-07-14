package io.akash.fundamentals

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.location.Location
import android.os.Looper
import android.widget.Toast
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

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

    /*Get updates every second better using a Flow*/
    fun trackLocation(): Flow<Location> {
        /*return flow {
            val locationCallback = locationCallback { location ->
                emit(location)
            }

            //location update every second or given interval
            val request = LocationRequest
                .Builder(1000)
                .build()

            fusedLocationClient.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )

            //problem is there is no function that tell me this flow is finished
            fusedLocationClient.removeLocationUpdates(locationCallback)

        }*//*.onCompletion {
            //can not access locationCallback
            fusedLocationClient.removeLocationUpdates(locationCallback)
        } */

        //problem solution callbackFlow
        return callbackFlow {
            val locationCallback = locationCallback { location ->
                launch {
                    send(location)
                }
            }

            //location update every second or given interval
            val request = LocationRequest
                .Builder(1000)
                .build()

            fusedLocationClient.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )

            awaitClose {
                fusedLocationClient.removeLocationUpdates(locationCallback)
            }

        }
    }

    private fun locationCallback(
        onResult: (location: Location) -> Unit
    ): LocationCallback {

        return object: LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                result.locations.lastOrNull()?.let { location ->
                    onResult(location)
                }
            }
        }
    }

}