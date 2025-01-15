package com.example.search.ui.screens.recipe_details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.common.navigation.NavigationRoute
import com.example.search.domain.model.RecipeDetails
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: RecipeDetailsViewModel,
    navHostController: NavHostController,
    onNavigationClicked: () -> Unit,
    onDelete: (RecipeDetails) -> Unit,
    onFavoriteClick: (RecipeDetails) -> Unit
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val recipeDetailsState by viewModel.getRecipeDetailsState.collectAsStateWithLifecycle()
    //var pageTitle = rememberSaveable { mutableStateOf(" ") }

    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(key1 = viewModel.navigation) {
        viewModel.navigation.flowWithLifecycle(lifecycleOwner.lifecycle)
            .collectLatest { navigation ->
                when (navigation) {
                    is RecipeDetailsHandler.Navigation.GotoRecipeListScreen -> navHostController.popBackStack()
                    is RecipeDetailsHandler.Navigation.GotoMediaPlayer -> {
                        val videoId = navigation.youtubeUrl.split("v=").last()
                        navHostController.navigate(NavigationRoute.MediaPlayer.sendUrl(videoId))
                    }
                }
            }
    }

    Scaffold (modifier = Modifier.padding(horizontal = 12.dp), topBar = {
        TopAppBar(
            title = {
                Text(text = uiState.recipeDetail?.strMeal.toString(), style = MaterialTheme.typography.bodyLarge)
            }, navigationIcon = {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.clickable {
                    onNavigationClicked.invoke()
                })

            }, actions = {
                IconButton(onClick = {
                    uiState.recipeDetail?.let { onFavoriteClick.invoke(it) }
                }) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = null)
                }
                IconButton(onClick = {
                    uiState.recipeDetail?.let { onDelete.invoke(it) }
                }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                }
            })

    }){ it ->
//        if (uiState.isLoading){
//            Box(modifier = Modifier
//                .padding(it)
//                .fillMaxSize(), contentAlignment = Alignment.Center){
//                CircularProgressIndicator()
//            }
//        }

        when(val state = recipeDetailsState){
            is GetRecipeDetailsState.Loading -> {
                Box(modifier = Modifier
                    .padding(it)
                    .fillMaxSize(), contentAlignment = Alignment.Center){
                    CircularProgressIndicator()
                }
            }

            GetRecipeDetailsState.Default -> Unit
            is GetRecipeDetailsState.Failure -> {
                Box(modifier = Modifier
                    .padding(it)
                    .fillMaxSize(), contentAlignment = Alignment.Center){
                    Text(text = state.errorMessage)
                    //Text(text = (searchRecipeState as SearchRecipeState.Failure).errorMessage)
                }
            }
            is GetRecipeDetailsState.Success -> {
                state.data?.let {
                    //pageTitle.value = it.strMeal
                    Column (
                        modifier = Modifier
                            .padding()
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        AsyncImage(
                            model = it.strMealThumb, contentDescription = null, modifier = Modifier.padding(horizontal = 16.dp)
                            .fillMaxWidth()
                            .height(300.dp).clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop

                        )
                        Column (modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)){
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(text = it.strInstructions, style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(12.dp))
                            it.ingredientsPair.forEach {
                                if (it.first.isNotEmpty() && it.second.isNotEmpty()){
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                horizontal = 12.dp
                                            ),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AsyncImage(
                                            modifier = Modifier
                                                .background(
                                                    color = Color.White,
                                                    shape = CircleShape
                                                )
                                                .size(60.dp)
                                                .clip(CircleShape),
                                            model = getIngredientImageUrl(it.first),
                                            contentDescription = null
                                        )
                                        Text(text = it.second, style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            if (it.strYoutube.isNotEmpty()){
                                Text(modifier = Modifier.clickable {
                                    viewModel.onEvent(RecipeDetailsHandler.Event.GotoMediaPlayer(it.strYoutube))
                                }, text = "Watch Youtube Video", style = MaterialTheme.typography.bodySmall)
                                Spacer(modifier = Modifier.height(32.dp))
                            }

                        }

                    }
                }
            }
        }
    }
}

fun getIngredientImageUrl(name: String) =  "https://www.themealdb.com/images/ingredients/${name}.png"