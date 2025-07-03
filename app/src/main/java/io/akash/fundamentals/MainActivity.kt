package io.akash.fundamentals

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.akash.fundamentals.ui.theme.AndroidFundamentalsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         val alarmManager = AndroidAlarmManager(this)
        setContent {
            AndroidFundamentalsTheme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            alarmManager.scheduleAlarm()
                        }
                    ) { Text(text = "Set Alarm") }

                    Spacer(modifier = Modifier.height(30.dp))

                    Button(
                        onClick = {
                            alarmManager.cancelAlarm()
                        }
                    ) { Text(text = "Cancel Alarm") }
                }
            }
        }
    }
}