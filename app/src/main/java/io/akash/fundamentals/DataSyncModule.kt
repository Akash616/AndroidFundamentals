package io.akash.fundamentals

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataSyncModule {

    @Provides
    @Singleton
    fun provideDataSyncRepository(
        application: Application
    ): DataSyncRepository{
        return DataSyncRepository(application)
    }
}