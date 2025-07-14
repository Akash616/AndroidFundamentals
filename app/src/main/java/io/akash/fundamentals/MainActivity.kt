package io.akash.fundamentals

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import io.akash.fundamentals.ui.theme.AndroidFundamentalsTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val locationManager by lazy {
        LocationManager(applicationContext)
    }
    /*Why by lazy? At this point this applicationContext won't be available yet except
    *in the onCreate so by lazy make sure that this instance does not get initialized
    *until we do have application context*/

    private val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.POST_NOTIFICATIONS
        )
    } else {
        arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private var onLocationReadyAction: (() -> Unit)? = null

    private lateinit var locationResolutionLauncher: ActivityResultLauncher<IntentSenderRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ActivityCompat.requestPermissions(this, permissions, 100)

        // Register activity result launcher
        locationResolutionLauncher =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    onLocationReadyAction?.invoke()
                } else {
                    Toast.makeText(this, "Location not enabled", Toast.LENGTH_SHORT).show()
                }
            }

        setContent {
            AndroidFundamentalsTheme {
                Screen()
            }
        }
    }

    @Composable
    fun Screen() {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            var locationText by remember {
                mutableStateOf("")
            }

            Text(text = locationText)

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    onLocationReadyAction = {
                        locationManager.getLocation { latitude, longitude ->
                            locationText = "Location: ..$latitude / ..$longitude"
                        }
                    }
                    checkLocationSettingsAndLaunch()
                }
            ) {
                Text(text = "Get Location")
            }

            Spacer(modifier = Modifier.height(50.dp))

            Button(
                onClick = {
                    Intent(
                        applicationContext, LocationTrackerService::class.java
                    ).also {
                        it.action = LocationTrackerService.Action.START.name
                        startService(it)
                    }
                }
            ) {
                Text(text = "Start Tracking")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    Intent(
                        applicationContext, LocationTrackerService::class.java
                    ).also {
                        it.action = LocationTrackerService.Action.STOP.name
                        startService(it)
                    }
                }
            ) {
                Text(text = "Stop Tracking")
            }
        }
    }

    /*automatically by Google Play services — specifically by:
    LocationSettingsRequest via FusedLocationProviderClient*/
    private fun checkLocationSettingsAndLaunch() {
        val locationRequest = LocationRequest.create().apply {
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        val settingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)
            .build()

        val client = LocationServices.getSettingsClient(this)

        client.checkLocationSettings(settingsRequest)
            .addOnSuccessListener {
                // Already satisfied
                onLocationReadyAction?.invoke()
            }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        val intentSenderRequest = IntentSenderRequest.Builder(
                            exception.resolution
                        ).build()

                        locationResolutionLauncher.launch(intentSenderRequest)
                    } catch (e: IntentSender.SendIntentException) {
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(this, "Can't change location settings", Toast.LENGTH_SHORT).show()
                }
            }
    }
}