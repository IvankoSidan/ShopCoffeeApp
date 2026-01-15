package com.anton.shopcoffeapp.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.anton.shopcoffeapp.App
import com.anton.shopcoffeapp.R
import com.anton.shopcoffeapp.databinding.ActivityMainBinding
import com.anton.shopcoffeapp.features.cart.di.CartComponent
import com.anton.shopcoffeapp.features.dashboard.di.DashboardComponent
import com.anton.shopcoffeapp.features.itemdetail.di.ItemDetailComponent
import com.anton.shopcoffeapp.features.itemlist.di.ItemListComponent

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    lateinit var cartComponent: CartComponent
    lateinit var itemListComponent: ItemListComponent
    lateinit var dashboardComponent: DashboardComponent
    lateinit var itemDetailComponent: ItemDetailComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initComponent()
        setupNavigation()
    }

    private fun initComponent() {
        val appComponent = (application as App).appComponent
        cartComponent = appComponent.cartComponent().create()
        itemListComponent = appComponent.itemListComponent().create()
        dashboardComponent = appComponent.dashBoardComponent().create()
        itemDetailComponent = appComponent.itemDetailComponent().create()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationBar.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigationBar.visibility = if (destination.id == R.id.splashFragment) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }
    }
}