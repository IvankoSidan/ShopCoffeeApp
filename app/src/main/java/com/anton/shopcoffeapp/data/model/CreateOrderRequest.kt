package com.anton.shopcoffeapp.data.model

data class CreateOrderRequest(
    val customerName: String,
    val items: List<OrderItemDto>,
    val promoCode: String? = null,
    val discount: Double = 0.0,
    val total: Double
)