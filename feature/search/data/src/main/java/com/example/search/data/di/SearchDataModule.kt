package com.example.search.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton


const val BASE_URL = "https://www.themealdb.com/"

@InstallIn(SingletonComponent::class)
@Module
object SearchDataModule {

//    @Provides
//    @Singleton
//    fun provideRetrofit(): Retrofit{
//        return Retrofit.Builder().baseUrl(BASE_URL)
//    }
}