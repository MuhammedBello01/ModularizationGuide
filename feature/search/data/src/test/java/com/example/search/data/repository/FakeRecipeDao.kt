package com.example.search.data.repository

import com.example.search.data.local.RecipeDao
import com.example.search.domain.model.Recipe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeRecipeDao: RecipeDao {
    val list = mutableListOf<Recipe>()
    override suspend fun insertRecipe(recipe: Recipe) {
        list.add(recipe)
    }

    override suspend fun deleteRecipe(recipe: Recipe) {
        list.remove(recipe)
    }

    override fun getAllRecipes(): Flow<List<Recipe>> {
        return flowOf(list)
    }

    override suspend fun updateRecipe(recipe: Recipe) {

    }
}