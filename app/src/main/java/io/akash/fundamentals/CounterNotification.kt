package io.akash.fundamentals

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class CounterNotification(
    private val context: Context
) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    //public fun that we want to call outside this class from a broadcast receiver
    fun counterNotification(counter: Int) {

        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            PendingIntent.FLAG_IMMUTABLE
        else
            0

        val intent = Intent(context, MainActivity::class.java)
        val notificationClickPendingIntent = PendingIntent.getActivity(
            context, 1, intent, flag
        )

        val notification = NotificationCompat
            .Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Counter")
            .setContentText(counter.toString())
            .setStyle(NotificationCompat.BigTextStyle())
            .setOngoing(true) //notification can't go away/swipe
            .setContentIntent(notificationClickPendingIntent)
            .addAction(
                R.drawable.ic_launcher_foreground,
                "Start",
                getPendingIntentForAction(
                    CounterActions.START, flag, 2
                )
            )
            .addAction(
                R.drawable.ic_launcher_foreground,
                "Stop",
                getPendingIntentForAction(
                    CounterActions.STOP, flag, 3
                )
            )
            .build()

        //when we have multiple notifications then we send id(1)
        notificationManager.notify(1, notification)
    }

    private fun getPendingIntentForAction(
        action: CounterActions,
        flag: Int,
        requestCode: Int
    ): PendingIntent {
        val intent = Intent(context, CounterReceiver::class.java)

        when (action) {
            CounterActions.START -> intent.action = CounterActions.START.name
            CounterActions.STOP -> intent.action = CounterActions.STOP.name
        }

        return PendingIntent.getBroadcast(
            context, requestCode, intent, flag
        )
    }

    companion object {
        const val CHANNEL_ID = "counter_channel"
    }

    enum class CounterActions {
        START, STOP
    }

}