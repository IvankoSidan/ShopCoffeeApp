package com.anton.shopcoffeapp.features.itemlist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anton.shopcoffeapp.domain.model.Category
import com.anton.shopcoffeapp.domain.model.Item
import com.anton.shopcoffeapp.domain.usecase.GetAllItemsUseCase
import com.anton.shopcoffeapp.domain.usecase.GetCategoriesUseCase
import com.anton.shopcoffeapp.domain.usecase.GetItemsByCategoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class ItemListViewModel @Inject constructor(
    private val getAllItemsUseCase: GetAllItemsUseCase,
    private val getItemsByCategoryUseCase: GetItemsByCategoryUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory

    private val _items = MutableStateFlow<List<Item>>(emptyList())
    val items: StateFlow<List<Item>> = _items

    init {
        loadCategories()
    }

    private fun loadCategories() = viewModelScope.launch {
        getCategoriesUseCase().onSuccess {
        }
    }

    fun loadItems(categoryId: Int? = null, searchQuery: String? = null) = viewModelScope.launch {
        val result = when {
            searchQuery != null -> getAllItemsUseCase().map { list ->
                list.filter { it.title.contains(searchQuery, ignoreCase = true) }
            }
            categoryId != null -> {
                getCategoriesUseCase().onSuccess { cats ->
                    _selectedCategory.value = cats.find { it.id == categoryId }
                }
                getItemsByCategoryUseCase(categoryId)
            }
            else -> {
                _selectedCategory.value = null
                getAllItemsUseCase()
            }
        }
        result.onSuccess { _items.value = it }
    }
}