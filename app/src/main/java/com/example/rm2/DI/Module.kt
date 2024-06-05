package com.example.rm2.DI

import com.example.network.KtorClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class Module{

    @Provides
    @Singleton
    fun ProvidesKtorClient():KtorClient{
        return KtorClient()
    }
}