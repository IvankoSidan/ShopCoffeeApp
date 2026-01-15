package com.anton.shopcoffeapp.data.api

import com.anton.shopcoffeapp.data.model.BannerDto
import com.anton.shopcoffeapp.data.model.CategoryDto
import com.anton.shopcoffeapp.data.model.CreateOrderRequest
import com.anton.shopcoffeapp.data.model.ItemDto
import com.anton.shopcoffeapp.data.model.OrderDto
import com.anton.shopcoffeapp.data.model.PopularDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CoffeeApiService {
    @GET("api/banners")
    suspend fun getBanners(): List<BannerDto>

    @GET("api/categories")
    suspend fun getCategories(): List<CategoryDto>

    @GET("api/populars")
    suspend fun getPopulars(): List<PopularDto>

    @GET("api/items")
    suspend fun getItems(): List<ItemDto>

    @GET("api/items/category/{categoryId}")
    suspend fun getItemsByCategory(@Path("categoryId") categoryId: Int): List<ItemDto>

    @POST("api/orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): OrderDto
}