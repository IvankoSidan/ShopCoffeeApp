package com.anton.shopcoffeapp.data.model

import com.anton.shopcoffeapp.domain.model.Banner

data class BannerDto(
    val id: Int,
    val url: String
) {
    fun toDomain(): Banner = Banner(id, url)
}