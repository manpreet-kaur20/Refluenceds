package com.example.refluenceds.di

import com.example.refluenceds.data.repository.AppRepositoryImpl
import com.example.refluenceds.data.repository.CampaignRepositoryImpl
import com.example.refluenceds.domain.repository.AppRepository
import com.example.refluenceds.domain.repository.CampaignRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAppRepository(
        appRepositoryImpl: AppRepositoryImpl
    ): AppRepository

    @Binds
    @Singleton
    abstract fun bindCampaignRepository(
        campaignRepositoryImpl: CampaignRepositoryImpl
    ): CampaignRepository
}
