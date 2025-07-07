package io.akash.fundamentals

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import io.akash.fundamentals.ui.theme.AndroidFundamentalsTheme
import java.time.Duration
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /*OneTime task happen and finish and that's it except if we want
    to retry every 50 seconds in case we fail.*/
    /*Periodic work request happen every 13 minutes or 1 hour, etc.*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPeriodicWorker()
        setContent {
            AndroidFundamentalsTheme {

            }
        }
    }

    private fun initOneTimeWorker() {
        /*Returned a retry from my worker(if i fail), what will happen
        *setBackoffCriteria() LINEAR is that i will retry every 15 seconds,
        *Until i successfully do the work.
        *setBackoffCriteria() EXPONENTIAL is that i will retry 15 seconds
        *after 15 seconds i will try after 30 seconds, then 1 minutes...etc.*/

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            //.setRequiresCharging(false)
            //.setRequiresBatteryNotLow(true)
            .build()

        val workRequest =
            OneTimeWorkRequestBuilder<DataSyncWorker>()
                .setBackoffCriteria(
                    duration = Duration.ofSeconds(15),
                    backoffPolicy = BackoffPolicy.LINEAR
                )
                .setConstraints(constraints)
                //.setInitialDelay(Duration.ofSeconds(10)) //start after 10 sec
                .build()

        WorkManager.getInstance(this)
            .enqueue(workRequest) //we can enqueue more than 1 requests.

    }

    private fun initPeriodicWorker() {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest =
            PeriodicWorkRequestBuilder<DataSyncWorker>(1, TimeUnit.HOURS) //every 1 hour
                .setBackoffCriteria(
                    duration = Duration.ofSeconds(15),
                    backoffPolicy = BackoffPolicy.LINEAR
                )
                .setConstraints(constraints)
                //.setInitialDelay(Duration.ofSeconds(10)) //start after 10 sec
                .build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "DATA_SYNC_WORKER", //name of worker
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            ) //we can enqueue more than 1 requests.

    }

}
