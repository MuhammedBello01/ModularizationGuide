package com.example.search.data.repository

import com.example.search.data.local.RecipeDao
import com.example.search.data.mappers.toDomain
import com.example.search.data.models.RecipeDTO
import com.example.search.data.models.RecipeDetailsResponse
import com.example.search.data.models.RecipeResponse
import com.example.search.data.remote.SearchApiService
import com.example.search.domain.model.Recipe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import retrofit2.Response

class SearchRepositoryImplTest{

    private val searchApiService: SearchApiService = mock()
    private val recipeDao: RecipeDao = mock()


    @Test
    fun test_success() = runTest {

        `when`(searchApiService.getRecipes("chicken"))
            .thenReturn(Response.success(200, getRecipeResponse()))

        val repo = SearchRepositoryImpl(searchApiService, recipeDao)

        val response = repo.getRecipes("chicken")

        assertTrue(response.isSuccess)
        assertEquals(getRecipeResponse().meals?.toDomain(), response.getOrThrow())
    }

    @Test
    fun test_nullMealFromBackend() = runTest {
        `when`(searchApiService.getRecipes("chicken"))
            .thenReturn(Response.success(200, RecipeResponse()))

        val repo = SearchRepositoryImpl(searchApiService, recipeDao)
        val response = repo.getRecipes("chicken")

        val message = "No recipes found"
        assertTrue(response.isFailure)
        assertEquals(message, response.exceptionOrNull()?.message)
    }

    @Test
    fun test_backend_response_fails() = runTest {
        `when`(searchApiService.getRecipes("chicken"))
            .thenReturn(Response.error(404, ResponseBody.create(null, "")))
        val repo = SearchRepositoryImpl(searchApiService, recipeDao)
        val response = repo.getRecipes("chicken")
        assertEquals("error occurred", response.exceptionOrNull()?.message)
    }

    @Test
    fun test_backend_will_throw_exception() = runTest {
        `when`(searchApiService.getRecipes("chicken"))
            .thenThrow(RuntimeException("error"))
        val repo = SearchRepositoryImpl(searchApiService, recipeDao)

        val response = repo.getRecipes("chicken")

        assertEquals("error", response.exceptionOrNull()?.message)
    }

    @Test
    fun test_success_recipe_details() = runTest {
        `when`(searchApiService.getRecipeDetails("id"))
            .thenReturn(Response.success(200, getRecipeDetails()))

        val repo = SearchRepositoryImpl(searchApiService, recipeDao)
        val response = repo.getRecipeDetails("id")

        assertEquals(getRecipeDetails().meals?.first()?.toDomain(), response.getOrThrow())
    }

    @Test
    fun test_success_with_empty_list() = runTest {
        `when`(searchApiService.getRecipeDetails("id"))
            .thenReturn(Response.success(200, RecipeDetailsResponse(meals = emptyList())))

        val repo = SearchRepositoryImpl(searchApiService, recipeDao)
        val response = repo.getRecipeDetails("id")
        assertEquals("No recipe details found", response.exceptionOrNull()?.message)
    }

    @Test
    fun test_success_with_null_meal() = runTest {
        `when`(searchApiService.getRecipeDetails("id")).thenReturn(
            Response.success(200, RecipeDetailsResponse())
        )
        val repo = SearchRepositoryImpl(searchApiService, recipeDao)
        val response = repo.getRecipeDetails("id")
        assertEquals("No recipe details found", response.exceptionOrNull()?.message)
    }

    @Test
    fun test_failed_from_backend() = runTest {
        `when`(searchApiService.getRecipeDetails("id"))
            .thenReturn(Response.error(404, ResponseBody.create(null, "")))
        val repo = SearchRepositoryImpl(searchApiService, recipeDao)

        val response = repo.getRecipeDetails("id")

        assertEquals("error occurred", response.exceptionOrNull()?.message)
    }

    @Test
    fun test_backend_throw_ex() = runTest {
        `when`(searchApiService.getRecipeDetails("id")).thenThrow(RuntimeException("error"))
        val repo = SearchRepositoryImpl(searchApiService, recipeDao)
        val response = repo.getRecipeDetails("id")
        assertEquals("error", response.exceptionOrNull()?.message)

    }

    @Test
    fun test_insert() = runTest {
        val repo = SearchRepositoryImpl(searchApiService, FakeRecipeDao())
        val recipe = getRecipeResponse().meals?.toDomain()?.first()
        repo.insertRecipe(recipe!!)
        assertEquals(recipe, repo.getAllRecipes().first().first())
    }

    @Test
    fun test_delete() = runTest {
        val repo = SearchRepositoryImpl(searchApiService, FakeRecipeDao())
        val recipe = getRecipeResponse().meals?.toDomain()?.first()!!
        repo.insertRecipe(recipe)
        val list = repo.getAllRecipes().first().first()
        assertEquals(recipe,list)

        repo.deleteRecipe(recipe)
        assertEquals(emptyList<Recipe>(),repo.getAllRecipes().last())
    }


}

private fun getRecipeResponse(): RecipeResponse {
    return RecipeResponse(
        meals = listOf(
            RecipeDTO(
                dateModified = null,
                idMeal = "idMeal",
                strArea = "India",
                strCategory = "category",
                strYoutube = "strYoutube",
                strTags = "tag1,tag2",
                strMeal = "Chicken",
                strSource = "strSource",
                strMealThumb = "strMealThumb",
                strInstructions = "strInstructions",
                strCreativeCommonsConfirmed = null,
                strIngredient1 = null,
                strIngredient2 = null,
                strIngredient3 = null,
                strIngredient4 = null,
                strIngredient5 = null,
                strIngredient6 = null,
                strIngredient7 = null,
                strIngredient8 = null,
                strIngredient9 = null,
                strIngredient10 = null,
                strIngredient11 = null,
                strIngredient12 = null,
                strIngredient13 = null,
                strIngredient14 = null,
                strIngredient15 = null,
                strIngredient16 = null,
                strIngredient17 = null,
                strIngredient18 = null,
                strIngredient19 = null,
                strIngredient20 = null,
                strMeasure1 = null,
                strMeasure2 = null,
                strMeasure3 = null,
                strMeasure4 = null,
                strMeasure5 = null,
                strMeasure6 = null,
                strMeasure7 = null,
                strMeasure8 = null,
                strMeasure9 = null,
                strMeasure10 = null,
                strMeasure11 = null,
                strMeasure12 = null,
                strMeasure13 = null,
                strMeasure14 = null,
                strMeasure15 = null,
                strMeasure16 = null,
                strMeasure17 = null,
                strMeasure18 = null,
                strMeasure19 = null,
                strMeasure20 = null,
                strDrinkAlternate = null,
                strImageSource = "empty"
            )
        )
    )
}

private fun getRecipeDetails(): RecipeDetailsResponse {
    return RecipeDetailsResponse(
        meals = listOf(
            getRecipeResponse().meals?.first()!!
        )
    )
}