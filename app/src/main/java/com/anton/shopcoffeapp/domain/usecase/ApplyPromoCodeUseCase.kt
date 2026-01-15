package com.anton.shopcoffeapp.domain.usecase

import javax.inject.Inject

class ApplyPromoCodeUseCase @Inject constructor() {
    suspend operator fun invoke(code: String, subtotal: Double): Result<Double> {
        return try {
            val discount = when (code.uppercase()) {
                "DISCOUNT10" -> subtotal * 0.1
                "FREESHIP" -> if (subtotal > 20) 2.0 else 0.0
                else -> throw IllegalArgumentException("Invalid promo code")
            }
            Result.success(discount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}