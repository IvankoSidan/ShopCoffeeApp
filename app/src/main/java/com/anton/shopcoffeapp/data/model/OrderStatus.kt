package com.anton.shopcoffeapp.data.model

sealed class OrderStatus {
    object None : OrderStatus()
    object Loading : OrderStatus()
    data class Success(val message: String) : OrderStatus()
    data class Error(val message: String) : OrderStatus()
}