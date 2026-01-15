package com.anton.shopcoffeapp.domain.usecase

import com.anton.shopcoffeapp.domain.model.Category
import com.anton.shopcoffeapp.domain.repository.CoffeeRepository
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val repository: CoffeeRepository
) {
    suspend operator fun invoke(): Result<List<Category>> {
        return repository.getCategories()
    }
}