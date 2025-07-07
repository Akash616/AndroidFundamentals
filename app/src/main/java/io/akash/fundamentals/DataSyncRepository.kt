package io.akash.fundamentals

import android.app.Application
import kotlinx.coroutines.delay

class DataSyncRepository(
    application: Application
) {

    suspend fun syncData() {
        delay(3000)
        //throw Exception()
        println("data is synced")
    }
}