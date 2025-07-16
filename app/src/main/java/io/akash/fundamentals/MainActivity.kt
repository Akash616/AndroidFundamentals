package io.akash.fundamentals

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import io.akash.fundamentals.ui.theme.AndroidFundamentalsTheme

class MainActivity : ComponentActivity() {

    private val mainViewModel by viewModels<MainViewModel>()

    private val permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidFundamentalsTheme {

                val showDialog =
                    mainViewModel.showDialog.collectAsState().value //	Gets current value for UI and listens for updates

                val launchAppSettings =
                    mainViewModel.launchAppSettings.collectAsState().value

                //permission dialog
                val permissionsResultActivityLauncher = rememberLauncherForActivityResult(
                    //contract = ActivityResultContracts.RequestPermission => Single permission
                    contract = ActivityResultContracts.RequestMultiplePermissions(),
                    onResult = { result ->
                        permissions.forEach { permission ->
                            if (result[permission] == false) { //permission is declined by the user
                                if (!shouldShowRequestPermissionRationale(permission)) { //2 times permission is declined by the user
                                    mainViewModel.updateLaunchAppSettings(true)
                                }
                                mainViewModel.updateShowDialog(true) //1 time permission is declined by the user
                            }
                        }
                    }
                )

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(onClick = {
                        permissions.forEach { permission ->
                            //first check already have permissions
                            val isGranted = checkSelfPermission(permission) ==
                                    PackageManager.PERMISSION_GRANTED

                            if (!isGranted) {
                                //shouldShowRequestPermissionRationale this fun tells me if i can actually
                                //ask for the permissions or not
                                if (shouldShowRequestPermissionRationale(permission)) {
                                    mainViewModel.updateShowDialog(true)
                                } else {
                                    permissionsResultActivityLauncher.launch(permissions)
                                }
                            }
                        }
                    }) {
                        Text(text = "Request Permission")
                    }
                }

                if (showDialog) {
                    PermissionDialog(
                        onDismiss = {
                            mainViewModel.updateShowDialog(false)
                        },
                        onConfirm = {
                            mainViewModel.updateShowDialog(false)

                            if (launchAppSettings) {
                                Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", packageName, null)
                                ).also {
                                    startActivity(it)
                                }
                                mainViewModel.updateLaunchAppSettings(false)
                            } else {
                                permissionsResultActivityLauncher.launch(permissions)
                            }

                        }
                    )
                }

            }
        }
    }

    @Composable
    fun PermissionDialog(
        onDismiss: () -> Unit,
        onConfirm: () -> Unit
    ) {
        AlertDialog(
            modifier = Modifier.fillMaxSize(),
            onDismissRequest = onDismiss,
            confirmButton = {
                Button(
                    onClick = onConfirm
                ) {
                    Text(text = "Ok")
                }
            },
            title = {
                Text(
                    text = "Camera and Microphone permissions are needed",
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Text(
                    text = "This app needs access to your camera and microphone"
                )
            }
        )
    }
}