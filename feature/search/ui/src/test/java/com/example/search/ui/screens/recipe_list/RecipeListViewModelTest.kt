package com.example.search.ui.screens.recipe_list

import com.example.common.utils.NetworkResult
import com.example.search.domain.model.Recipe
import com.example.search.domain.model.RecipeDetails
import com.example.search.domain.use_cases.GetAllRecipeUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class RecipeListViewModelTest{

//    @get:Rule(order = 1)
//    val mainDispatcherRule = MainDispatcherRule()


    private val testDispatcher = StandardTestDispatcher()
    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    private val getAllRecipeUseCase: GetAllRecipeUseCase = mock()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_success() = runTest {
        `when`(getAllRecipeUseCase.invoke("chicken"))
            .thenReturn(flowOf(NetworkResult.Success(data = getRecipeResponse())))
        val viewModel = RecipeListViewModel(getAllRecipeUseCase)
        viewModel.onEvent(RecipeListHandler.Event.SearchRecipe("chicken"))
        advanceUntilIdle()
        assertEquals(getRecipeResponse(), viewModel.uiState.value.recipes)

//        val expectedResponse = SearchRecipeState.Success(data = getRecipeResponse())
//        assertEquals(expectedResponse, viewModel.searchRecipeState.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_failed() = runTest {
        `when`(getAllRecipeUseCase.invoke("chicken"))
            .thenReturn(flowOf(NetworkResult.Error(message = "Something went wrong")))
        val viewModel = RecipeListViewModel(getAllRecipeUseCase)
        viewModel.onEvent(RecipeListHandler.Event.SearchRecipe("chicken"))
        advanceUntilIdle()
        assertEquals("Something went wrong",viewModel.uiState.value.errorMessage)
    }

    @Test
    fun test_navigate_recipe_details() = runTest {
        val viewModel = RecipeListViewModel(getAllRecipeUseCase)
        viewModel.onEvent(RecipeListHandler.Event.GotoRecipeDetails("id"))
        val list = mutableListOf<RecipeListHandler.Navigation>()
        backgroundScope.launch(StandardTestDispatcher()) {
            viewModel.navigation.collectLatest {
                list.add(it)
            }
        }
        assert(list.first() is RecipeListHandler.Navigation.GotoRecipeDetails)
    }

    @Test
    fun test_navigate_to_favorite_screen()= runTest {
        val viewModel = RecipeListViewModel(getAllRecipeUseCase)
        viewModel.onEvent(RecipeListHandler.Event.FavoriteScreen)
        val list = mutableListOf<RecipeListHandler.Navigation>()
        backgroundScope.launch (StandardTestDispatcher()){
            viewModel.navigation.collectLatest {
                list.add(it)
            }
        }
        assert(list.first() is RecipeListHandler.Navigation.GoToFavoriteScreen)

    }
}

class MainDispatcherRule(private val testDispatcher: TestDispatcher = StandardTestDispatcher()) :  TestWatcher(){
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun starting(description: Description?) {
        Dispatchers.setMain(testDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun finished(description: Description?) {
        Dispatchers.resetMain()
    }
}

private fun getRecipeResponse(): List<Recipe> {
    return listOf(
        Recipe(
            idMeal = "idMeal",
            strArea = "India",
            strCategory = "category",
            strYoutube = "strYoutube",
            strTags = "tag1,tag2",
            strMeal = "Chicken",
            strMealThumb = "strMealThumb",
            strInstructions = "12",
        ),
        Recipe(
            idMeal = "idMeal",
            strArea = "India",
            strCategory = "category",
            strYoutube = "strYoutube",
            strTags = "tag1,tag2",
            strMeal = "Chicken",
            strMealThumb = "strMealThumb",
            strInstructions = "123",
        )
    )

}

private fun getRecipeDetails(): RecipeDetails {
    return RecipeDetails(
        idMeal = "idMeal",
        strArea = "India",
        strCategory = "category",
        strYoutube = "strYoutube",
        strTags = "tag1,tag2",
        strMeal = "Chicken",
        strMealThumb = "strMealThumb",
        strInstructions = "strInstructions",
        ingredientsPair = listOf(Pair("Ingredients", "Measure"))
    )
}