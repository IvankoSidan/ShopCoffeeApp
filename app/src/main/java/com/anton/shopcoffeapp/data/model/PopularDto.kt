package com.anton.shopcoffeapp.data.model

import com.anton.shopcoffeapp.domain.model.Popular

data class PopularDto(
    val id: Int,
    val title: String,
    val description: String,
    val extra: String,
    val price: Double,
    val rating: Double,
    val picUrl: String
) {
    fun toDomain(): Popular = Popular(
        id = id,
        title = title,
        description = description,
        extra = extra,
        price = price,
        rating = rating,
        picUrl = picUrl
    )
}