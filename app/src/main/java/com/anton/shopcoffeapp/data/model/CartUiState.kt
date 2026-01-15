package com.anton.shopcoffeapp.data.model

import com.anton.shopcoffeapp.domain.model.CartItem

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val subtotal: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val tax: Double = 0.0,
    val discount: Double = 0.0,
    val total: Double = 0.0,
    val promoCode: String? = null,
    val isPromoApplied: Boolean = false,
    val promoError: String? = null,
    val orderStatus: OrderStatus = OrderStatus.None
)