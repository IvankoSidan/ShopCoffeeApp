package com.anton.shopcoffeapp.di

import androidx.lifecycle.ViewModel
import com.anton.shopcoffeapp.features.cart.viewmodel.CartViewModel
import com.anton.shopcoffeapp.features.dashboard.viewmodel.DashboardViewModel
import com.anton.shopcoffeapp.features.itemdetail.viewmodel.ItemDetailViewModel
import com.anton.shopcoffeapp.features.itemlist.viewmodel.ItemListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(CartViewModel::class)
    abstract fun bindCartViewModel(viewModel: CartViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DashboardViewModel::class)
    abstract fun bindDashboardViewModel(viewModel: DashboardViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ItemListViewModel::class)
    abstract fun bindItemListViewModel(viewModel: ItemListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ItemDetailViewModel::class)
    abstract fun bindItemDetailViewModel(viewModel: ItemDetailViewModel): ViewModel
}