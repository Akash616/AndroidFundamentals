package io.akash.fundamentals

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

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

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            // Permissions granted, check location services
            if (!locationManager.isLocationEnabled()) {
                locationEnableLauncher.launch(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        } else {
            // Handle permission denial
            scope.launch {
                locationText = "Location permissions denied"
            }
        }
    }

    private var locationText by mutableStateOf("")
    private val locationEnableLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        // Retry fetching location after user returns from settings
        scope.launch {
            locationManager.getLocation(
                onSuccess = { latitude, longitude ->
                    locationText = "Location: ..$latitude / ..$longitude"
                },
                onFailure = { error ->
                    locationText = error
                }
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionLauncher.launch(permissions)
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
            Text(text = locationText)

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    scope.launch {
                        if (!locationManager.isLocationEnabled()) {
                            locationEnableLauncher.launch(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                        } else {
                            locationManager.getLocation(
                                onSuccess = { latitude, longitude ->
                                    locationText = "Location: ..$latitude / ..$longitude"
                                },
                                onFailure = { error ->
                                    locationText = error
                                }
                            )
                        }
                    }
                }
            ) {
                Text(text = "Get Location")
            }

            Spacer(modifier = Modifier.height(50.dp))

            Button(
                onClick = {

                }
            ) {
                Text(text = "Start Tracking")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {

                }
            ) {
                Text(text = "Stop Tracking")
            }
        }
    }
}