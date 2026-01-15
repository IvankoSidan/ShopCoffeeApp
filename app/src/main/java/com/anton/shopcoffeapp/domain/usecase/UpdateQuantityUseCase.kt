package com.anton.shopcoffeapp.domain.usecase

import com.anton.shopcoffeapp.domain.repository.CartRepository
import javax.inject.Inject

class UpdateQuantityUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    fun invoke(itemId: Int, quantity: Int) = cartRepository.updateQuantity(itemId, quantity)
}