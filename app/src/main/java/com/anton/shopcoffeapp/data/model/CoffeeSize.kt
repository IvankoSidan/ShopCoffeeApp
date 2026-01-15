package com.anton.shopcoffeapp.data.model

enum class CoffeeSize(val displayName: String, val priceMultiplier: Double) {
    SMALL("Small", 0.9),
    MEDIUM("Medium", 1.0),
    LARGE("Large", 1.2)
}