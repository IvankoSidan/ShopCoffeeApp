package com.anton.shopcoffeapp.features.itemlist.di

import com.anton.shopcoffeapp.presentation.fragments.ItemListFragment
import dagger.Subcomponent

@ItemListScope
@Subcomponent
interface ItemListComponent {
    fun inject(itemListFragment: ItemListFragment)

    @Subcomponent.Factory
    interface Factory {
        fun create(): ItemListComponent
    }
}