package com.anton.shopcoffeapp.domain.model

data class CartItem(
    val item: Item,
    val quantity: Int
) {
    val totalPrice: Double get() = item.price * quantity
}