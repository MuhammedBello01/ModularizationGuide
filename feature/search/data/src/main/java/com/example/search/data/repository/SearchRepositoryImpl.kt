package com.example.search.data.repository

import com.example.search.data.local.RecipeDao
import com.example.search.data.mappers.toDomain
import com.example.search.data.remote.SearchApiService
import com.example.search.domain.model.Recipe
import com.example.search.domain.model.RecipeDetails
import com.example.search.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow

class SearchRepositoryImpl(
    private val searchApiService: SearchApiService,
    private val recipeDao: RecipeDao
): SearchRepository {
    override suspend fun getRecipes(s: String): Result<List<Recipe>> {
        return try {
            val response = searchApiService.getRecipes(s)
             if (response.isSuccessful) {
                val meals = response.body()?.meals
                if (meals != null && meals.any()) {
                    Result.success(meals.toDomain())
                } else {
                    Result.failure(Exception("No recipes found"))
                }
            } else {
                Result.failure(Exception("Failed to fetch recipes: ${response.code()}"))
            }
        }catch (e : Exception){
            Result.failure(e)
        }

    }

    override suspend fun getRecipeDetails(id: String): Result<RecipeDetails> {
       return try {
           val response = searchApiService.getRecipeDetails(id)
           return if (response.isSuccessful){
               val meals = response.body()?.meals
               if (meals != null) {
                   Result.success(meals.first().toDomain())
               }else{
                   Result.failure(Exception("No recipe details found"))
               }
           }else {
               Result.failure(Exception("Failed to fetch recipes: ${response.code()}"))
           }
       }catch (e : Exception){
           Result.failure(e)
       }
    }

    override suspend fun insertRecipe(recipe: Recipe) {
        recipeDao.insertRecipe(recipe)
    }

    override suspend fun deleteRecipe(recipe: Recipe) {
        recipeDao.deleteRecipe(recipe)
    }

    override fun getAllRecipes(): Flow<List<Recipe>> {
        return recipeDao.getAllRecipes()
    }

}