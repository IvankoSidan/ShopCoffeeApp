package com.anton.shopcoffeapp.domain.usecase

import com.anton.shopcoffeapp.domain.model.Item
import com.anton.shopcoffeapp.domain.repository.CoffeeRepository
import javax.inject.Inject

class GetItemsByCategoryUseCase @Inject constructor(
    private val repository: CoffeeRepository
) {
    suspend operator fun invoke(categoryId: Int): Result<List<Item>> {
        return repository.getItemsByCategory(categoryId)
    }
}