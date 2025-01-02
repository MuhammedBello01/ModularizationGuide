package com.example.search.ui.screens.recipe_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.utils.NetworkResult
import com.example.common.utils.UiText
import com.example.search.domain.model.RecipeDetails
import com.example.search.domain.use_cases.GetRecipeDetailsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class RecipeDetailsViewModel @Inject constructor(
    private val getRecipeDetailsUseCase: GetRecipeDetailsUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow(RecipeDetailsState.UiState())
    val uistate: StateFlow<RecipeDetailsState.UiState> get() = _uiState.asStateFlow()


    fun onEvent(event: RecipeDetailsState.Event){
        when(event){
            is RecipeDetailsState.Event.FetchRecipeDetails -> recipeDetails(event.id)
        }
    }


    private fun recipeDetails(id: String) = getRecipeDetailsUseCase.invoke(id)
        .onEach { result ->
            when(result){
                is NetworkResult.Loading -> {
                    _uiState.update { RecipeDetailsState.UiState(isLoading = true) }
                }
                is NetworkResult.Error -> {
                    _uiState.update { RecipeDetailsState.UiState(error = UiText.RemoteString(result.message.toString())) }
                }
                is NetworkResult.Success -> {
                    _uiState.update { RecipeDetailsState.UiState(data = result.data) }
                }
            }
        }.launchIn(viewModelScope)
}


object RecipeDetailsState{
    data class UiState(
        val isLoading: Boolean = false,
        val error: UiText = UiText.Idle,
        val data: RecipeDetails? = null
    )

    sealed interface Navigation{


    }

    sealed interface Event{
        data class FetchRecipeDetails(val id: String) : Event
    }

}