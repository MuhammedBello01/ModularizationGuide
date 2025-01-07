package com.example.search.domain.model

//class DomainModelFile {
//}

data class Recipe(
    val idMeal: String,
    val strArea: String,
    val strMeal: String,
    val strMealThumb: String,
    val strCategory: String,
    val strTags: String,
    val strYoutube: String,
    val strInstructions: String,
)

data class RecipeDetails(
    val idMeal: String,
    val strArea: String,
    val strMeal: String,
    val strMealThumb: String,
    val strCategory: String,
    val strTags: String,
    val strYoutube: String,
    val strInstructions: String,
    val ingredientsPair: List<Pair<String, String>>
)