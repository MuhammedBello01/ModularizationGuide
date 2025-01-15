package com.example.search.ui.screens.recipe_list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.common.navigation.NavigationRoute
import com.example.search.domain.model.Recipe
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecipeListScreen(
    modifier: Modifier = Modifier,
    viewModel: RecipeListViewModel,
    navHostController: NavHostController,
    onClick: (String) -> Unit
){
    val searchRecipeState by viewModel.searchRecipeState.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val query = rememberSaveable {
        mutableStateOf("")
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(key1 = viewModel.navigation) {
        viewModel.navigation
            .flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .collectLatest {
                when (it) {
                    is RecipeListHandler.Navigation.GotoRecipeDetails -> {
                        navHostController.navigate(NavigationRoute.RecipeDetails.sendId(it.id))
                    }

                    is RecipeListHandler.Navigation.GoToFavoriteScreen -> {
                        navHostController.navigate(NavigationRoute.FavoriteScreen.route)
                    }
                }
            }
    }


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.onEvent(RecipeListHandler.Event.FavoriteScreen)
            }) {
                Icon(imageVector = Icons.Default.Star, contentDescription = null)
            }
        },
        topBar = {
        TextField(
            placeholder = {
                Text(
                    text = "Search here...",
                    style = MaterialTheme.typography.bodySmall
                )
            },
            value = query.value, onValueChange = {
            query.value = it
                viewModel.onEvent(RecipeListHandler.Event.SearchRecipe(query.value))
                //viewModel.search(query.value)
        }, colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        ), modifier = Modifier.fillMaxWidth())
    }){ paddingValues ->
        when(searchRecipeState){
            is SearchRecipeState.Loading -> {
                Box(modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(), contentAlignment = Alignment.Center){
                    CircularProgressIndicator()
                }
            }
            is SearchRecipeState.Failure -> {
                Box(modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(), contentAlignment = Alignment.Center){
                    Text(text = uiState.errorMessage ?: "Error")
                    //Text(text = (searchRecipeState as SearchRecipeState.Failure).errorMessage)
                }
            }
            is SearchRecipeState.Success -> {
                //val recipe = searchRecipeState.data
                val recipe = uiState.recipes
                recipe?.let { item ->
                    LazyColumn(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()) {
                        items(item){
                            //RecipeListItemCard(it, onClick)
                            Card(modifier = Modifier
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                                .clickable { onClick.invoke(it.idMeal) },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                AsyncImage(
                                    model = it.strMealThumb,
                                    contentDescription = "Meal Image",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(250.dp),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                Column (modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth().padding(horizontal = 16.dp)){
                                    Text(
                                        text = it.strMeal,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = if (it.strMeal.isEmpty()) Color.Gray else Color.Black
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = it.strInstructions,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (it.strMeal.isEmpty()) Color.Gray else Color.Black,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 4
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    if(it.strTags.isNotEmpty()){
                                        FlowRow {
                                            it.strTags.split(",").forEach{
                                                Box(
                                                    modifier = Modifier
                                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                                        .wrapContentSize()
                                                        .background(
                                                            color = Color.White,
                                                            shape = RoundedCornerShape(20.dp)
                                                        )
                                                        .clip(
                                                            RoundedCornerShape(20 .dp)
                                                        )
                                                        .border(
                                                            width = 1.dp,
                                                            shape = RoundedCornerShape(12.dp),
                                                            color = Color.Red
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ){
                                                    Text(
                                                        text = it,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                            }
                        }
                    }
                }
            }
            SearchRecipeState.Default -> Unit
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecipeListItemCard(recipe: Recipe, onClick: (String) -> Unit){
    Card(modifier = Modifier
        .padding(horizontal = 12.dp, vertical = 4.dp)
        .clickable { },
        shape = RoundedCornerShape(12.dp)
    ) {
        AsyncImage(
            model = recipe.strMealThumb,
            contentDescription = "Meal Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = recipe.strMeal,
            style = MaterialTheme.typography.bodyLarge,
            color = if (recipe.strMeal.isEmpty()) Color.Gray else Color.Black
        )
        Spacer(modifier = Modifier.height(12.dp))

        if(recipe.strTags.isNotEmpty()){
            FlowRow {
                recipe.strTags.split(",").forEach{
                    Box(
                        modifier = Modifier
                            .wrapContentSize()
                            .background(color = Color.White, shape = RoundedCornerShape(12.dp))
                            .clip(
                                RoundedCornerShape(12.dp)
                            )
                            .border(
                                width = 1.dp,
                                shape = RoundedCornerShape(12.dp),
                                color = Color.Red
                            ),
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}