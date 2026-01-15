package com.anton.shopcoffeapp.data.repository

import com.anton.shopcoffeapp.domain.model.CartItem
import com.anton.shopcoffeapp.domain.model.Item
import com.anton.shopcoffeapp.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor() : CartRepository {
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())

    override fun addToCart(item: Item) {
        val currentItems = _cartItems.value
        val updated = if (currentItems.any { it.item.id == item.id }) {
            currentItems.map {
                if (it.item.id == item.id) it.copy(quantity = it.quantity + 1)
                else it
            }
        } else {
            currentItems + CartItem(item, 1)
        }
        _cartItems.value = updated
    }


    override fun removeFromCart(itemId: Int) {
        val currentItems = _cartItems.value.toMutableList()
        currentItems.removeAll { it.item.id == itemId }
        _cartItems.value = currentItems
    }

    override fun updateQuantity(itemId: Int, quantity: Int) {
        val updated = _cartItems.value.mapNotNull { cartItem ->
            if (cartItem.item.id == itemId) {
                if (quantity <= 0) null
                else cartItem.copy(quantity = quantity)
            } else cartItem
        }
        _cartItems.value = updated
    }


    override fun getCartItems(): Flow<List<CartItem>> = _cartItems

    override fun clearCart() {
        _cartItems.value = emptyList()
    }
}