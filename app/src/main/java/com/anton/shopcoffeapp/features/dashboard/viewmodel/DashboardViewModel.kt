package com.anton.shopcoffeapp.features.dashboard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anton.shopcoffeapp.domain.model.Banner
import com.anton.shopcoffeapp.domain.model.Category
import com.anton.shopcoffeapp.domain.model.Item
import com.anton.shopcoffeapp.domain.model.Popular
import com.anton.shopcoffeapp.domain.usecase.GetAllItemsUseCase
import com.anton.shopcoffeapp.domain.usecase.GetBannersUseCase
import com.anton.shopcoffeapp.domain.usecase.GetCategoriesUseCase
import com.anton.shopcoffeapp.domain.usecase.GetPopularItemsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class DashboardViewModel @Inject constructor(
    private val getBannersUseCase: GetBannersUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getPopularItemsUseCase: GetPopularItemsUseCase,
    private val getAllItemsUseCase: GetAllItemsUseCase
) : ViewModel() {

    private val _banner = MutableStateFlow<List<Banner>>(emptyList())
    val banner: StateFlow<List<Banner>> get() = _banner

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> get() = _categories

    private val _popularItems = MutableStateFlow<List<Popular>>(emptyList())
    val popularItems: StateFlow<List<Popular>> get() = _popularItems

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadDashboard() = viewModelScope.launch {
        _isLoading.value = true
        try {
            getBannersUseCase.invoke().onSuccess { _banner.value = it }
            getCategoriesUseCase.invoke().onSuccess { _categories.value = it }
            getPopularItemsUseCase.invoke().onSuccess { _popularItems.value = it }
        } finally {
            _isLoading.value = false
        }
    }

    suspend fun findItemByQuery(query: String): Item? {
        val result = getAllItemsUseCase()
        return result.getOrNull()?.find { it.title.contains(query, ignoreCase = true) }
    }
}