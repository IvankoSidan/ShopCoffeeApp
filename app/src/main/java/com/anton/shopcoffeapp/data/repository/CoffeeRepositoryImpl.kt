package com.anton.shopcoffeapp.data.repository

import com.anton.shopcoffeapp.data.api.CoffeeApiService
import com.anton.shopcoffeapp.data.model.CreateOrderRequest
import com.anton.shopcoffeapp.data.model.OrderItemDto
import com.anton.shopcoffeapp.domain.model.Banner
import com.anton.shopcoffeapp.domain.model.CartItem
import com.anton.shopcoffeapp.domain.model.Category
import com.anton.shopcoffeapp.domain.model.Item
import com.anton.shopcoffeapp.domain.model.Popular
import com.anton.shopcoffeapp.domain.repository.CoffeeRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CoffeeRepositoryImpl @Inject constructor(
    private val apiService: CoffeeApiService
) : CoffeeRepository {

    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun getBanners(): Result<List<Banner>> = withContext(dispatcher) {
        runCatching { apiService.getBanners().map { it.toDomain() } }
    }

    override suspend fun getCategories(): Result<List<Category>> = withContext(dispatcher) {
        runCatching { apiService.getCategories().map { it.toDomain() } }
    }

    override suspend fun getPopularItems(): Result<List<Popular>> = withContext(dispatcher) {
        runCatching { apiService.getPopulars().map { it.toDomain() } }
    }

    override suspend fun getItemsByCategory(categoryId: Int): Result<List<Item>> = withContext(dispatcher) {
        runCatching { apiService.getItemsByCategory(categoryId).map { it.toDomain() } }
    }

    override suspend fun getAllItems(): Result<List<Item>> = withContext(dispatcher) {
        runCatching { apiService.getItems().map { it.toDomain() } }
    }

    override suspend fun createOrder(
        customerName: String,
        items: List<CartItem>,
        promoCode: String?,
        discount: Double,
        total: Double
    ): Result<Boolean> = withContext(dispatcher) {
        runCatching {
            val orderItems = items.map {
                OrderItemDto(it.item.id, it.quantity, it.item.price)
            }
            val request = CreateOrderRequest(
                customerName = customerName,
                items = orderItems,
                promoCode = promoCode,
                discount = discount,
                total = total
            )
            apiService.createOrder(request)
            true
        }
    }
}