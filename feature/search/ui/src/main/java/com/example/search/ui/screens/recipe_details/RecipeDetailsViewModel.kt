package com.example.search.ui.screens.recipe_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.utils.NetworkResult
import com.example.common.utils.UiText
import com.example.search.domain.model.Recipe
import com.example.search.domain.model.RecipeDetails
import com.example.search.domain.use_cases.DeleteRecipeUseCase
import com.example.search.domain.use_cases.GetAllRecipeFromDbUseCase
import com.example.search.domain.use_cases.GetRecipeDetailsUseCase
import com.example.search.domain.use_cases.InsertRecipeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeDetailsViewModel @Inject constructor(
    private val getRecipeDetailsUseCase: GetRecipeDetailsUseCase,
    private val deleteRecipeUseCase: DeleteRecipeUseCase,
    private val insertRecipeUseCase: InsertRecipeUseCase,
    private val getAllRecipeFromDbUseCase: GetAllRecipeFromDbUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow(RecipeDetailsUiState())
    val uiState: StateFlow<RecipeDetailsUiState> get() = _uiState.asStateFlow()

    private val _getRecipeDetailState = MutableStateFlow<GetRecipeDetailsState>(GetRecipeDetailsState.Default)
    val getRecipeDetailsState: StateFlow<GetRecipeDetailsState> = _getRecipeDetailState.asStateFlow()

    private val _navigation = Channel<RecipeDetailsHandler.Navigation>()
    val navigation: Flow<RecipeDetailsHandler.Navigation> = _navigation.receiveAsFlow()

    fun onEvent(event: RecipeDetailsHandler.Event){
        when(event){
            is RecipeDetailsHandler.Event.FetchRecipeDetails -> recipeDetails(event.id)
            is RecipeDetailsHandler.Event.GotoRecipeListScreen -> {
                viewModelScope.launch {
                    _navigation.send(RecipeDetailsHandler.Navigation.GotoRecipeListScreen)
                }
            }

            is RecipeDetailsHandler.Event.DeleteRecipe -> {
                deleteRecipeUseCase.invoke(event.recipeDetail.toRecipe())
                    .launchIn(viewModelScope)
            }
            is RecipeDetailsHandler.Event.InsertRecipe -> {
                insertRecipeUseCase.invoke(event.recipeDetail.toRecipe())
                    .launchIn(viewModelScope)
            }

            is RecipeDetailsHandler.Event.GotoMediaPlayer -> {
                viewModelScope.launch {
                    _navigation.send(RecipeDetailsHandler.Navigation.GotoMediaPlayer(event.youtubeUrl))
                }
            }
        }
    }


    private fun recipeDetails(id: String) = getRecipeDetailsUseCase.invoke(id)
        .onEach { result ->
            when(result){
                is NetworkResult.Loading -> {
                    _getRecipeDetailState.value = GetRecipeDetailsState.Loading
                    _uiState.update { RecipeDetailsUiState(isLoading = true) }
                }
                is NetworkResult.Error -> {
                    _getRecipeDetailState.value = GetRecipeDetailsState.Failure(errorMessage = result.message.toString())
                    _uiState.update { RecipeDetailsUiState(isFailure = true, errorMessage = result.message) }
                }
                is NetworkResult.Success -> {
                    _getRecipeDetailState.value = GetRecipeDetailsState.Success(data = result.data)
                    _uiState.update { RecipeDetailsUiState(isSuccessful = true, recipeDetail = result.data) }
                }
            }
        }.launchIn(viewModelScope)

}

fun RecipeDetails.toRecipe(): Recipe {
    return Recipe(
        idMeal,
        strArea,
        strMeal,
        strMealThumb,
        strCategory,
        strTags,
        strYoutube,
        strInstructions
    )
}


sealed interface GetRecipeDetailsState {
    data class Success(val data: RecipeDetails?) : GetRecipeDetailsState
    data class Failure(val errorMessage: String, val title: String = "") : GetRecipeDetailsState
    data object Loading : GetRecipeDetailsState
    data object Default : GetRecipeDetailsState

}

data class RecipeDetailsUiState(
    val isLoading: Boolean = false,
    val isFailure: Boolean = false,
    val isSuccessful: Boolean = true,
    val errorMessage: String? = null,
    val recipeDetail: RecipeDetails? = null
)


object RecipeDetailsHandler{
    data class UiState(
        val isLoading: Boolean = false,
        val error: UiText = UiText.Idle,
        val data: RecipeDetails? = null
    )

    sealed interface Navigation{
        data object GotoRecipeListScreen : Navigation
        data class GotoMediaPlayer(val youtubeUrl: String): Navigation
    }

    sealed interface Event{
        data class FetchRecipeDetails(val id: String) : Event
        data object GotoRecipeListScreen: Event
        data class InsertRecipe(val recipeDetail: RecipeDetails) : Event
        data class DeleteRecipe(val recipeDetail: RecipeDetails) : Event
        data class GotoMediaPlayer(val youtubeUrl: String): Event
    }

}