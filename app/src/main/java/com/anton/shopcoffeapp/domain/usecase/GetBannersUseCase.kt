package com.anton.shopcoffeapp.domain.usecase

import com.anton.shopcoffeapp.domain.model.Banner
import com.anton.shopcoffeapp.domain.repository.CoffeeRepository
import javax.inject.Inject

class GetBannersUseCase @Inject constructor(
    private val repository: CoffeeRepository
) {
    suspend operator fun invoke(): Result<List<Banner>> {
        return repository.getBanners()
    }
}