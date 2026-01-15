package com.anton.shopcoffeapp.domain.usecase

import com.anton.shopcoffeapp.domain.repository.CartRepository
import javax.inject.Inject

class GetCartItemsUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    fun invoke() = cartRepository.getCartItems()
}