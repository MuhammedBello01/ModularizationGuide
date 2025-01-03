package com.example.search.ui.navigation

import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.common.navigation.FeatureApi
import com.example.common.navigation.NavigationRoute
import com.example.common.navigation.NavigationSubGraphRoute

interface SearchFeatureApi : FeatureApi

class SearchFeatureApiImpl : SearchFeatureApi{
    override fun registerGraph(
        navGraphBuilder: androidx.navigation.NavGraphBuilder,
        navController: androidx.navigation.NavController
    ) {
        navGraphBuilder.navigation(
            route = NavigationSubGraphRoute.Search.route,
            startDestination = NavigationRoute.RecipeList.route
        ){
            composable(NavigationRoute.RecipeList.route){

            }
            composable(NavigationRoute.RecipeDetails.route){

            }


        }
    }

}