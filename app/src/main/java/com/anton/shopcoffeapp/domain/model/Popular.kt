package com.anton.shopcoffeapp.domain.model

data class Popular(
    val id: Int,
    val title: String,
    val description: String,
    val extra: String,
    val price: Double,
    val rating: Double,
    val picUrl: String
)