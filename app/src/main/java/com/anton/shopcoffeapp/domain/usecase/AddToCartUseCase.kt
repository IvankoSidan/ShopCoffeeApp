package com.anton.shopcoffeapp.domain.usecase

import com.anton.shopcoffeapp.domain.model.Item
import com.anton.shopcoffeapp.domain.repository.CartRepository
import javax.inject.Inject

class AddToCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(item: Item) {
        cartRepository.addToCart(item)
    }
}