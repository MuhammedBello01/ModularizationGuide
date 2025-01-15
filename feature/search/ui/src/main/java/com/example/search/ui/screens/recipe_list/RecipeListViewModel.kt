package com.example.search.ui.screens.recipe_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.utils.NetworkResult
import com.example.common.utils.UiText
import com.example.search.domain.model.Recipe
import com.example.search.domain.use_cases.GetAllRecipeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeListViewModel @Inject constructor(
    private val getAllRecipeUseCase: GetAllRecipeUseCase
): ViewModel() {

//    private val _searchRecipeState = MutableStateFlow(RecipeList.UiState())
//    val uistate: StateFlow<RecipeList.UiState> get() = _searchRecipeState.asStateFlow()
        private val _searchRecipeState = MutableStateFlow<SearchRecipeState>(SearchRecipeState.Default)
        val searchRecipeState: StateFlow<SearchRecipeState> = _searchRecipeState//.asStateFlow()

    private val _uiState = MutableStateFlow(SearchRecipeUiState())
    val uiState: StateFlow<SearchRecipeUiState> = _uiState.asStateFlow()

    private val _navigation = Channel<RecipeListHandler.Navigation>()
    val navigation: Flow<RecipeListHandler.Navigation> = _navigation.receiveAsFlow()

    private var searchRecipeJob: Job? = null

    fun onEvent(event: RecipeListHandler.Event){
        when(event){
            is RecipeListHandler.Event.SearchRecipe -> { search(event.q) }
            is RecipeListHandler.Event.GotoRecipeDetails -> {
                viewModelScope.launch {
                    _navigation.send(RecipeListHandler.Navigation.GotoRecipeDetails(event.id))
                }
            }
            is RecipeListHandler.Event.FavoriteScreen -> {
               viewModelScope.launch {
                    _navigation.send(RecipeListHandler.Navigation.GoToFavoriteScreen)
                }
            }
        }
    }

     private fun search(q: String) = viewModelScope.launch {
        getAllRecipeUseCase.invoke(q)
            .collectLatest { result ->
            when(result){
                is NetworkResult.Loading -> {
                    _searchRecipeState.value = SearchRecipeState.Loading
                    _uiState.update {
                        SearchRecipeUiState(isLoading = true)
                    }
                }
                is NetworkResult.Error -> {
                    _searchRecipeState.value = SearchRecipeState.Failure(errorMessage = result.message ?: "Something went wrong", title = "Failed")
                    _uiState.update {
                        SearchRecipeUiState(isFailure = true, errorMessage = result.message ?: "Something went wrong")
                    }
                }
                is NetworkResult.Success -> {
                    _searchRecipeState.value = SearchRecipeState.Success(data = result.data)
                    _uiState.update{
                        SearchRecipeUiState(recipes = result.data)
                    }

                }

            }
        }
    }

    private fun searchV2(query: String) {
        if (searchRecipeJob != null) return
        searchRecipeJob = viewModelScope.launch {
            getAllRecipeUseCase.invoke(query)
                .collectLatest { result ->
                    when (result) {
                        is NetworkResult.Loading -> {
                            _searchRecipeState.value = SearchRecipeState.Loading
                            _uiState.update { it.copy(isLoading = true) }
                        }
                        is NetworkResult.Error -> {
                            _searchRecipeState.value = SearchRecipeState.Failure(
                                errorMessage = result.message.toString(),
                                title = "Failed"
                            )
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    isFailure = true,
                                    errorMessage = result.message ?: "Something went wrong"
                                )
                            }
                        }
                        is NetworkResult.Success -> {
                            _searchRecipeState.value = SearchRecipeState.Success(data = result.data)
                            _uiState.update {
                                it.copy(
                                    recipes = result.data,
                                    isLoading = false,
                                    isFailure = false,
                                    errorMessage = null
                                )
                            }
                        }
                    }
                }
            searchRecipeJob = null
        }
    }

}


sealed interface SearchRecipeState {
    data class Success(val data: List<Recipe>?) : SearchRecipeState
    data class Failure(val errorMessage: String, val title: String) : SearchRecipeState
    data object Loading : SearchRecipeState
    data object Default : SearchRecipeState

}

data class SearchRecipeUiState(
    val isLoading: Boolean = false,
    val isFailure: Boolean = false,
    val errorMessage: String? = null,
    val recipes: List<Recipe>? = null
)

object RecipeListHandler{
    data class UiState(
        val isLoading: Boolean = false,
        val error: UiText = UiText.Idle,
        val data: List<Recipe>? = null
    )

    sealed interface Navigation{
        data class GotoRecipeDetails(val id: String): Navigation
        data object GoToFavoriteScreen:Navigation
    }

    sealed interface Event{
        data class SearchRecipe(val q: String): Event
        data class  GotoRecipeDetails(val id: String): Event
        data object FavoriteScreen:Event
    }

}