package com.example.modularizationguide.navigation

import com.example.media_player.navigation.MediaPlayerFeatureAPi
import com.example.search.ui.navigation.SearchFeatureApi

data class NavigationSubGraphs(
    val searchFeatureApi: SearchFeatureApi,
    val MediaPlayerApi: MediaPlayerFeatureAPi

)
