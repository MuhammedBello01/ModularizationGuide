package com.example.search.ui.screens.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.utils.UiText
import com.example.search.domain.model.Recipe
import com.example.search.domain.use_cases.DeleteRecipeUseCase
import com.example.search.domain.use_cases.GetAllRecipeFromDbUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val getAllRecipesFromLocalDbUseCase: GetAllRecipeFromDbUseCase,
    private val deleteRecipeUseCase: DeleteRecipeUseCase
): ViewModel() {

    private var originalList = mutableListOf<Recipe>()

    private val _uiState = MutableStateFlow(FavoriteUiState())
    val uiState: StateFlow<FavoriteUiState> = _uiState.asStateFlow()

    private val _navigation = Channel<FavoriteHandler.Navigation>()
    val navigation: Flow<FavoriteHandler.Navigation> = _navigation.receiveAsFlow()

    init {
        getRecipeList()
    }

    fun onEvent(event: FavoriteHandler.Event) {
        when (event) {
            is FavoriteHandler.Event.AlphabeticalSort -> alphabeticalSort()
            is FavoriteHandler.Event.LessIngredientsSort -> lessIngredientsSort()
            is FavoriteHandler.Event.ResetSort -> resetSort()
            is FavoriteHandler.Event.ShowDetails -> viewModelScope.launch {
                _navigation.send(FavoriteHandler.Navigation.GoToRecipeDetailsScreen(event.id))
            }

            is FavoriteHandler.Event.DeleteRecipe -> deleteRecipe(event.recipe)
            is FavoriteHandler.Event.GoToDetails -> viewModelScope.launch {
                _navigation.send(FavoriteHandler.Navigation.GoToRecipeDetailsScreen(event.id))
            }
        }
    }

    private fun deleteRecipe(recipe: Recipe) = deleteRecipeUseCase.invoke(recipe)
        .launchIn(viewModelScope)

    private fun getRecipeList() {
        viewModelScope.launch {
            getAllRecipesFromLocalDbUseCase.invoke().collectLatest { list ->
                originalList = list.toMutableList()
                _uiState.update { FavoriteUiState(data = list) }
            }
        }
    }

    private fun alphabeticalSort() =
        _uiState.update { FavoriteUiState(data = originalList.sortedBy { it.strMeal }) }

    private fun lessIngredientsSort() =
        _uiState.update { FavoriteUiState(data = originalList.sortedBy { it.strInstructions.length }) }

    private fun resetSort() {
        _uiState.update { FavoriteUiState(data = originalList) }
    }
}


data class FavoriteUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val error: String = "",
    val data: List<Recipe>? = null
)

object FavoriteHandler {
    data class UiState(
        val isLoading: Boolean = false,
        val error: UiText = UiText.Idle,
        val data: List<Recipe>? = null
    )

    sealed interface Navigation {
        data class GoToRecipeDetailsScreen(val id: String) : Navigation
    }

    sealed interface Event {
        data object AlphabeticalSort : Event
        data object LessIngredientsSort : Event
        data object ResetSort : Event
        data class ShowDetails(val id: String) : Event
        data class DeleteRecipe(val recipe: Recipe) : Event
        data class GoToDetails(val id:String):Event
    }

}