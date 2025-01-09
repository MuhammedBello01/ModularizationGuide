package com.example.search.ui.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.common.navigation.FeatureApi
import com.example.common.navigation.NavigationRoute
import com.example.common.navigation.NavigationSubGraphRoute
import com.example.search.ui.screens.recipe_details.RecipeDetailScreen
import com.example.search.ui.screens.recipe_details.RecipeDetailsHandler
import com.example.search.ui.screens.recipe_details.RecipeDetailsViewModel
import com.example.search.ui.screens.recipe_list.RecipeListHandler
import com.example.search.ui.screens.recipe_list.RecipeListScreen
import com.example.search.ui.screens.recipe_list.RecipeListViewModel

interface SearchFeatureApi : FeatureApi

class SearchFeatureApiImpl : SearchFeatureApi{
    override fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navHostController: NavHostController
    ) {
        navGraphBuilder.navigation(
            route = NavigationSubGraphRoute.Search.route,
            startDestination = NavigationRoute.RecipeList.route
        ){
            composable(NavigationRoute.RecipeList.route){
                val viewModel = hiltViewModel<RecipeListViewModel>()
                RecipeListScreen(viewModel = viewModel, navHostController = navHostController){ mealId ->
                    viewModel.onEvent(RecipeListHandler.Event.GotoRecipeDetails(mealId))
                }
            }
            composable(NavigationRoute.RecipeDetails.route){
                val viewModel = hiltViewModel<RecipeDetailsViewModel>()
                val mealId = it.arguments?.getString("id")
                LaunchedEffect (key1 = mealId){
                    mealId.let { viewModel.onEvent(RecipeDetailsHandler.Event.FetchRecipeDetails(mealId.toString())) }
                }
                RecipeDetailScreen(
                    viewModel = viewModel,
                    navHostController = navHostController,
                    onNavigationClicked = {
                        viewModel.onEvent(RecipeDetailsHandler.Event.GotoRecipeListScreen)
                    },
                    onDelete = {  },
                    onFavorite = { },
                )
            }


        }
    }

}