package com.crownedjester.soft.evaluationtask.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.crownedjester.soft.evaluationtask.common.Constants
import com.crownedjester.soft.evaluationtask.data.data_source.ImagesDatabase
import com.crownedjester.soft.evaluationtask.domain.repository.DatabaseRepository
import com.crownedjester.soft.evaluationtask.domain.repository.DatabaseRepositoryImpl
import com.crownedjester.soft.evaluationtask.features.repository.DataStoreManager
import com.crownedjester.soft.evaluationtask.features.repository.DataStoreRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Singleton
    @Provides
    fun provideImageDatabase(application: Application): ImagesDatabase =
        Room.databaseBuilder(
            application,
            ImagesDatabase::class.java,
            Constants.DATABASE_NAME
        ).build()

    @Singleton
    @Provides
    fun provideDatabaseRepository(
        database: ImagesDatabase
    ): DatabaseRepository =
        DatabaseRepositoryImpl(database.photoDao)

    @Singleton
    @Provides
    fun provideDataStore(@ApplicationContext context: Context): DataStoreRepository =
        DataStoreManager(context)
}