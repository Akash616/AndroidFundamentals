package io.akash.fundamentals

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class LocationTrackerService: Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    /*SupervisorJob(): A job that allows child coroutines to fail independently
    without cancelling the entire scope. This is useful for tasks where one failure
    (e.g., a single location update failing) shouldn’t stop other tasks.
    */
    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )

    override fun onStartCommand(
        intent: Intent?, flags: Int, startId: Int
    ): Int {

        when (intent?.action) {
            Action.START.name -> start()
            Action.STOP.name -> stop()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {

        val locationManager = LocationManager(applicationContext)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat
            .Builder(this, LOCATION_CHANNEL)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Location Tracker")
            .setStyle(NotificationCompat.BigTextStyle())
            //.build() not here

        startForeground(1, notification.build())

        scope.launch {
            locationManager.trackLocation().collect { location ->
                val latitude = location.latitude.toString().takeLast(4)
                val longitude = location.longitude.toString().takeLast(4)

                //now update to notification
                //every notification has id
                notificationManager.notify(
                    1,
                    notification.setContentText(
                        "Location: ..$latitude / ..$longitude"
                    ).build()
                )
            }
        }
    }

    private fun stop() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    enum class Action {
        START, STOP
    }

    companion object {
        const val LOCATION_CHANNEL = "location_channel"
    }
}