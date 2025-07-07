package io.akash.fundamentals

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

//Hilt Factory in my app class: Inject my repository with dagger hilt, i need to
//extend Configuration.Provider Add provider in manifest file
@HiltAndroidApp
class App: Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory //INJECT BY DAGGER HILT

    override lateinit var workManagerConfiguration: Configuration

    override fun onCreate() {
        super.onCreate()

        workManagerConfiguration = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

}