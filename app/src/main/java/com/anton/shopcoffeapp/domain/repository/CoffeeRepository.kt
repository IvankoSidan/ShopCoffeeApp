package com.anton.shopcoffeapp.domain.repository

import com.anton.shopcoffeapp.domain.model.Banner
import com.anton.shopcoffeapp.domain.model.CartItem
import com.anton.shopcoffeapp.domain.model.Category
import com.anton.shopcoffeapp.domain.model.Item
import com.anton.shopcoffeapp.domain.model.Popular

interface CoffeeRepository {
    suspend fun getBanners(): Result<List<Banner>>
    suspend fun getCategories(): Result<List<Category>>
    suspend fun getPopularItems(): Result<List<Popular>>
    suspend fun getItemsByCategory(categoryId: Int): Result<List<Item>>
    suspend fun getAllItems(): Result<List<Item>>

    suspend fun createOrder(
        customerName: String,
        items: List<CartItem>,
        promoCode: String? = null,
        discount: Double = 0.0,
        total: Double
    ): Result<Boolean>
}