package com.anton.shopcoffeapp.features.itemdetail.di

import com.anton.shopcoffeapp.presentation.fragments.ItemDetailFragment
import dagger.Subcomponent

@ItemDetailScope
@Subcomponent
interface ItemDetailComponent {
    fun inject(fragment: ItemDetailFragment)

    @Subcomponent.Factory
    interface Factory {
        fun create(): ItemDetailComponent
    }
}