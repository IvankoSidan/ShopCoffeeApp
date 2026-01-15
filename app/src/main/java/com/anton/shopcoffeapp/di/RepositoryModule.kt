package com.anton.shopcoffeapp.di

import com.anton.shopcoffeapp.data.repository.CartRepositoryImpl
import com.anton.shopcoffeapp.data.repository.CoffeeRepositoryImpl
import com.anton.shopcoffeapp.domain.repository.CartRepository
import com.anton.shopcoffeapp.domain.repository.CoffeeRepository
import dagger.Binds
import dagger.Module

@Module
abstract class RepositoryModule {
    @Binds
    abstract fun bindCartRepository(
        impl : CartRepositoryImpl
    ) : CartRepository

    @Binds
    abstract fun bindCoffeeRepository(
        impl: CoffeeRepositoryImpl
    ) : CoffeeRepository
}