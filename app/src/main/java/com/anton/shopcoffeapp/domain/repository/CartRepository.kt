package com.anton.shopcoffeapp.domain.repository

import com.anton.shopcoffeapp.domain.model.CartItem
import com.anton.shopcoffeapp.domain.model.Item
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun addToCart(item: Item)
    fun removeFromCart(itemId: Int)
    fun updateQuantity(itemId: Int, quantity: Int)
    fun getCartItems(): Flow<List<CartItem>>
    fun clearCart()
}