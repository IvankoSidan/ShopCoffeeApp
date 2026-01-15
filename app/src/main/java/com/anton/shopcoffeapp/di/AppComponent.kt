package com.anton.shopcoffeapp.di

import android.content.Context
import com.anton.shopcoffeapp.features.cart.di.CartComponent
import com.anton.shopcoffeapp.features.dashboard.di.DashboardComponent
import com.anton.shopcoffeapp.features.itemdetail.di.ItemDetailComponent
import com.anton.shopcoffeapp.features.itemlist.di.ItemListComponent
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        NetworkModule::class,
        RepositoryModule::class,
        ViewModelModule::class,
        ViewModelFactoryModule::class
    ]
)
interface AppComponent {
    fun cartComponent(): CartComponent.Factory
    fun dashBoardComponent(): DashboardComponent.Factory
    fun itemListComponent(): ItemListComponent.Factory
    fun itemDetailComponent(): ItemDetailComponent.Factory

    fun viewModelFactory(): ViewModelFactory

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}