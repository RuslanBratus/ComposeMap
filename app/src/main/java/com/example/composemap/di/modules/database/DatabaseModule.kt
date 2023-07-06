package com.example.composemap.di.modules.database

import android.content.Context
import androidx.room.Room
import com.example.composemap.data.database.ComposeMapDatabase
import com.example.composemap.data.database.dao.MarkersDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


private const val DATABASE_NAME ="compose.map.db"

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Provides
    @Singleton
    fun provideComposeMapDatabase(@ApplicationContext applicationContext: Context): ComposeMapDatabase {
                return Room.databaseBuilder(
                    applicationContext, ComposeMapDatabase::class.java, DATABASE_NAME
                ).build()
    }

    @Provides
    fun provideChannelDao(composeMapDatabase: ComposeMapDatabase): MarkersDao {
        return composeMapDatabase.markersDao()
    }
}