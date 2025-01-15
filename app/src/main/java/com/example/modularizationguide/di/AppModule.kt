package com.example.modularizationguide.di

import android.content.Context
import com.example.media_player.navigation.MediaPlayerFeatureAPi
import com.example.modularizationguide.local.AppDatabase
import com.example.modularizationguide.navigation.NavigationSubGraphs
import com.example.search.data.local.RecipeDao
import com.example.search.ui.navigation.SearchFeatureApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideNavigationSubGraphs(searchFeatureApi: SearchFeatureApi, mediaPlayerFeatureAPi: MediaPlayerFeatureAPi): NavigationSubGraphs{
        return NavigationSubGraphs(searchFeatureApi, mediaPlayerFeatureAPi)
    }

    @Provides
    @Singleton
    fun providesAppDatabase(@ApplicationContext context: Context ) = AppDatabase.getInstance(context)

    @Provides
    @Singleton
    fun provideRecipeDao(database: AppDatabase): RecipeDao {
        return database.getRecipeDao()
    }

}