package com.example.search.data.Repository

import com.example.search.data.mappers.toDomain
import com.example.search.data.models.RecipeResponse
import com.example.search.data.remote.SearchApiService
import com.example.search.domain.model.Recipe
import com.example.search.domain.model.RecipeDetails
import com.example.search.domain.repository.SearchRepository
import retrofit2.Response

class SearchRepositoryImpl(private  val searchApiService: SearchApiService): SearchRepository {
    override suspend fun getRecipes(s: String): Result<List<Recipe>> {
        val response = searchApiService.getRecipes(s)
        return if (response.isSuccessful) {
            val meals = response.body()?.meals
            if (meals != null && meals.any()) {
                Result.success(meals.toDomain())
            } else {
                Result.failure(Exception("No recipes found"))
            }
        } else {
            Result.failure(Exception("Failed to fetch recipes: ${response.code()}"))
        }
    }

    override suspend fun getRecipeDetails(id: String): Result<RecipeDetails> {
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
    }

}