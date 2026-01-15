package com.anton.shopcoffeapp.features.cart.di

import com.anton.shopcoffeapp.presentation.fragments.CartFragment
import dagger.Subcomponent

@CartScope
@Subcomponent
interface CartComponent {
    fun inject(cartFragment: CartFragment)

    @Subcomponent.Factory
    interface Factory {
        fun create(): CartComponent
    }
}