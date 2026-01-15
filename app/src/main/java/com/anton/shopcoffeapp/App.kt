package com.anton.shopcoffeapp

import android.app.Application
import com.anton.shopcoffeapp.di.AppComponent
import com.anton.shopcoffeapp.di.DaggerAppComponent

class App : Application() {
    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(applicationContext)
    }
}