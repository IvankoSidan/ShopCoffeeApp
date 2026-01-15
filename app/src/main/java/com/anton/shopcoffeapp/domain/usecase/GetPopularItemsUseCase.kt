package com.anton.shopcoffeapp.domain.usecase

import com.anton.shopcoffeapp.domain.model.Popular
import com.anton.shopcoffeapp.domain.repository.CoffeeRepository
import javax.inject.Inject

class GetPopularItemsUseCase @Inject constructor(
    private val repository: CoffeeRepository
) {
    suspend operator fun invoke(): Result<List<Popular>> {
        return repository.getPopularItems()
    }
}