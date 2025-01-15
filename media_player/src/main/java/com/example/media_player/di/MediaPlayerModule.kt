package com.example.media_player.di

import com.example.media_player.navigation.MediaPlayerFeatureAPi
import com.example.media_player.navigation.MediaPlayerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@InstallIn(SingletonComponent::class)
@Module
object MediaPlayerModule {

    @Provides
    fun provideMediaPlayerFeatureApi():MediaPlayerFeatureAPi{
        return MediaPlayerImpl()
    }
}