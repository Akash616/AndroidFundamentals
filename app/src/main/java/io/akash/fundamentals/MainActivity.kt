package io.akash.fundamentals

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import io.akash.fundamentals.ui.theme.AndroidFundamentalsTheme

//Notification with functionality
//Channel is just a way to describe a notification
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                111
            )
        }

        val intent = Intent(this, CounterReceiver::class.java)
        intent.action = CounterNotification.CounterActions.START.name

        setContent {
            AndroidFundamentalsTheme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Button(
                        onClick = {
                            sendBroadcast(intent)
                        }
                    ) {
                        Text(text = "Show Counter Notification")
                    }

                }
            }
        }
    }
}