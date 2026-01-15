package com.anton.shopcoffeapp.data.model

import com.anton.shopcoffeapp.domain.model.Category

data class CategoryDto(
    val id: Int,
    val title: String
) {
    fun toDomain(): Category = Category(id, title)
}