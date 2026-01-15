package com.anton.shopcoffeapp.features.itemdetail.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anton.shopcoffeapp.data.model.CoffeeSize
import com.anton.shopcoffeapp.domain.model.Item
import com.anton.shopcoffeapp.domain.usecase.AddToCartUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ItemDetailViewModel @Inject constructor(
    private val addToCartUseCase: AddToCartUseCase
) : ViewModel() {

    private val _selectedItem = MutableStateFlow<Item?>(null)
    val selectedItem: StateFlow<Item?> = _selectedItem

    private val _quantity = MutableStateFlow(1)
    val quantity: StateFlow<Int> = _quantity

    private val _selectedSize = MutableStateFlow(CoffeeSize.MEDIUM)
    val selectedSize: StateFlow<CoffeeSize> = _selectedSize

    private val _subtotal = MutableStateFlow(0.0)
    val subtotal: StateFlow<Double> = _subtotal

    fun setInitialItem(item: Item) {
        _selectedItem.value = item
        calculateSubtotal()
    }

    fun increaseQuantity() {
        _quantity.value += 1
        calculateSubtotal()
    }

    fun decreaseQuantity() {
        if (_quantity.value > 1) {
            _quantity.value -= 1
            calculateSubtotal()
        }
    }

    fun changeSize(size: CoffeeSize) {
        _selectedSize.value = size
        calculateSubtotal()
    }

    private fun calculateSubtotal() {
        val item = _selectedItem.value ?: return
        val price = item.price * _selectedSize.value.priceMultiplier
        _subtotal.value = price * _quantity.value
    }

    fun addToCart() = viewModelScope.launch {
        val item = _selectedItem.value ?: return@launch
        val adjustedItem = item.copy(
            price = item.price * _selectedSize.value.priceMultiplier,
            extra = "${item.extra} (${_selectedSize.value.displayName})"
        )
        repeat(_quantity.value) {
            addToCartUseCase(adjustedItem)
        }
    }
}