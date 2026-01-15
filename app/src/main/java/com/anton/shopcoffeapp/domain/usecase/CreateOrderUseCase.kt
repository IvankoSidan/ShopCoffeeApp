package com.anton.shopcoffeapp.domain.usecase

import com.anton.shopcoffeapp.domain.model.CartItem
import com.anton.shopcoffeapp.domain.repository.CoffeeRepository
import javax.inject.Inject

class CreateOrderUseCase @Inject constructor(
    private val repository: CoffeeRepository
) {
    suspend operator fun invoke(
        customerName: String,
        items: List<CartItem>,
        promoCode: String? = null,
        discount: Double = 0.0,
        total: Double
    ): Result<Boolean> {
        return repository.createOrder(customerName, items, promoCode, discount, total)
    }
}