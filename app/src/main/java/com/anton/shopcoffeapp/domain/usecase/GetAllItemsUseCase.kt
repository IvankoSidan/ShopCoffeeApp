package com.anton.shopcoffeapp.domain.usecase

import com.anton.shopcoffeapp.domain.model.Item
import com.anton.shopcoffeapp.domain.repository.CoffeeRepository
import javax.inject.Inject

class GetAllItemsUseCase @Inject constructor(
    private val repository: CoffeeRepository
) {
    suspend operator fun invoke(): Result<List<Item>> {
        return repository.getAllItems()
    }
}