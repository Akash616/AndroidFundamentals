package io.akash.fundamentals

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class CounterReceiver: BroadcastReceiver() {

    private lateinit var counterNotification: CounterNotification
    private var counterJob: Job? = null

    override fun onReceive(context: Context?, intent: Intent?) {

        context?.let {
            counterNotification = CounterNotification(it)
        }

        when (intent?.action) {
            CounterNotification.CounterActions.START.name -> start()
            CounterNotification.CounterActions.STOP.name -> stop()
        }
    }

    private fun start() {
        stop()
        counterJob = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            Counter.start().collect { counterValue ->
                Log.d("CounterReceiver", counterValue.toString())
                counterNotification.counterNotification(counterValue)
            }
        }
    }

    private fun stop() {
        counterJob?.cancel()
        Counter.stop()
        counterNotification.counterNotification(0)
    }

}