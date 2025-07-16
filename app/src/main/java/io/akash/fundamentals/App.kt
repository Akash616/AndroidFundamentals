package io.akash.fundamentals

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        //Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val counterChannel = NotificationChannel(
                CounterNotification.CHANNEL_ID,
                "Counter Channel",
                NotificationManager.IMPORTANCE_LOW //i do not loud sound
            )

            //register a channel
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(counterChannel)
        }
    }
}