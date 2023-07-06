package com.example.composemap.di.modules.repository

import com.example.composemap.data.repository.ConfigurationMarkerRepository
import com.example.composemap.domain.repository.IConfigurationMarkerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindAuthorizationRepository(repository: ConfigurationMarkerRepository): IConfigurationMarkerRepository

}