package com.anton.shopcoffeapp.data.model

import com.anton.shopcoffeapp.domain.model.Item

data class ItemDto(
    val id: Int,
    val categoryId: Int,
    val title: String,
    val description: String,
    val extra: String,
    val price: Double,
    val rating: Double,
    val picUrl: String
) {
    fun toDomain(): Item = Item(
        id = id,
        categoryId = categoryId,
        title = title,
        description = description,
        extra = extra,
        price = price,
        rating = rating,
        picUrl = picUrl
    )
}