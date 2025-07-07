package io.akash.fundamentals

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay

/*@HiltWorker
class DataSyncWorker(
    context: Context,
    workerParameters: WorkerParameters
): Worker(context, workerParameters) {

    override fun doWork(): Result {
        delay(3000) //this worker is just a normal work manager, use suspend function like delay
        //we won't able to do do because we do not have coroutine scope.
        return Result.success()
        return Result.failure()
        return Result.retry()
    }
}*/

@HiltWorker
class DataSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val dataSyncRepository: DataSyncRepository //created by myself
): CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        return try {
            dataSyncRepository.syncData()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
