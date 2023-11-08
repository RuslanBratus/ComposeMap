package com.example.composemap.di.modules.preferences

import com.example.composemap.data.datasource.Preferences
import com.example.composemap.domain.preferences.IPreferences
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
interface PreferencesModule {

    @Binds
    fun providePreferences(preferences: Preferences): IPreferences
}