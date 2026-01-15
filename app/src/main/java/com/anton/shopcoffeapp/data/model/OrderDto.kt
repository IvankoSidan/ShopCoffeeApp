package com.anton.shopcoffeapp.data.model

import java.math.BigDecimal

data class OrderDto(
    val id: Int,
    val customerName: String,
    val createdAt: String,
    val status: String,
    val totalPrice: BigDecimal,
    val orderItems: List<OrderItemDto>
)