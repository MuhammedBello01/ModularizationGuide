package com.example.search.ui.navigation

import androidx.navigation.compose.navigation
import com.example.common.navigation.FeatureApi

interface SearchFeatureApi : FeatureApi

class SearchFeatureApiImpl : SearchFeatureApi{
    override fun registerGraph(
        navGraphBuilder: androidx.navigation.NavGraphBuilder,
        navController: androidx.navigation.NavController
    ) {
        navGraphBuilder.navigation(route = "", startDestination = ""){

        }
    }

}