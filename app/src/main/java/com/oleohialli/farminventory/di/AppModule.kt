package com.oleohialli.farminventory.di

import android.app.Application
import androidx.room.Room
import com.oleohialli.farminventory.Constants
import com.oleohialli.farminventory.data.FarmerDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(application: Application,
    callback: FarmerDatabase.Callback) =
        Room.databaseBuilder(application, FarmerDatabase::class.java, Constants.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .addCallback(callback)
            .build()

    @Provides
    fun provideFarmerDao(database: FarmerDatabase) = database.farmerDao()

     @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())

}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope