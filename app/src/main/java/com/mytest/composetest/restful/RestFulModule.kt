package com.mytest.composetest.restful

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RestFulModule {
    @Singleton
    @Provides
    fun provideRestFulTextRepository(): RestFulTestRepository = RestFulTestRepository()
}


