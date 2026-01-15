package com.anton.shopcoffeapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Item(
    val id: Int,
    val categoryId: Int,
    val title: String,
    val description: String,
    val extra: String,
    val price: Double,
    val rating: Double,
    val picUrl: String
) : Parcelable