package com.example.search.ui.screens.recipe_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.utils.NetworkResult
import com.example.common.utils.UiText
import com.example.search.domain.model.Recipe
import com.example.search.domain.use_cases.GetAllRecipeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class RecipeListViewModel @Inject constructor(
    private val getAllRecipeUseCase: GetAllRecipeUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow(RecipeList.UiState())
    val uistate: StateFlow<RecipeList.UiState> get() = _uiState.asStateFlow()

    fun onEvent(event: RecipeList.Event){
        when(event){
            is RecipeList.Event.SearchRecipe -> { search(event.q) }
        }
    }

    private fun search(q: String) = viewModelScope.launch {
        getAllRecipeUseCase.invoke(q)
            .collectLatest { result ->
            when(result){
                is NetworkResult.Loading -> {
                    _uiState.update { RecipeList.UiState(isLoading = true) }
                }
                is NetworkResult.Error -> {
                    _uiState.update { RecipeList.UiState(error = UiText.RemoteString(result.message.toString()))
                        // it.copy(
                        // isLoading = false,
                        //error = UiText.RemoteString(result.message.toString())
                        //)
                     }
                }
                is NetworkResult.Success -> { _uiState.update { RecipeList.UiState(data = result.data) } }
            }
        }
    }
}





object RecipeList{
    data class UiState(
        val isLoading: Boolean = false,
        val error: UiText = UiText.Idle,
        val data: List<Recipe>? = null
    )

    sealed interface Navigation{
        data class GotoRecipeDetails(val id: String): Navigation
    }

    sealed interface Event{
        data class SearchRecipe(val q: String): Event
    }

}