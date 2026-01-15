package com.anton.shopcoffeapp.features.dashboard.di

import com.anton.shopcoffeapp.presentation.fragments.DashboardFragment
import dagger.Subcomponent

@DashboardScope
@Subcomponent
interface DashboardComponent {
    fun inject(dashboardFragment: DashboardFragment)

    @Subcomponent.Factory
    interface Factory {
        fun create(): DashboardComponent
    }
}