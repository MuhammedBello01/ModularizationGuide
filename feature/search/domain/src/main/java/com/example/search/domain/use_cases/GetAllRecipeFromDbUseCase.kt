package com.example.search.domain.use_cases

import com.example.search.domain.model.Recipe
import com.example.search.domain.repository.SearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetAllRecipeFromDbUseCase @Inject constructor(private val searchRepository: SearchRepository) {
    operator fun invoke() = searchRepository.getAllRecipes()
}