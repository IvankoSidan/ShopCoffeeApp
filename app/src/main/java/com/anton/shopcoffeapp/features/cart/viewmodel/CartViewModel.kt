package com.anton.shopcoffeapp.features.cart.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anton.shopcoffeapp.data.model.OrderStatus
import com.anton.shopcoffeapp.domain.model.CartItem
import com.anton.shopcoffeapp.domain.usecase.ApplyPromoCodeUseCase
import com.anton.shopcoffeapp.domain.usecase.CreateOrderUseCase
import com.anton.shopcoffeapp.domain.usecase.GetCartItemsUseCase
import com.anton.shopcoffeapp.domain.usecase.RemoveFromCartUseCase
import com.anton.shopcoffeapp.domain.usecase.UpdateQuantityUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max

class CartViewModel @Inject constructor(
    private val updateQuantityUseCase: UpdateQuantityUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase,
    private val getCartItemsUseCase: GetCartItemsUseCase,
    private val createOrderUseCase: CreateOrderUseCase,
    private val applyPromoCodeUseCase: ApplyPromoCodeUseCase
) : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> get() = _cartItems

    private val _promoCode = MutableStateFlow<String?>(null)

    private val _discountAmount = MutableStateFlow(0.0)
    val discountAmount: StateFlow<Double> get() = _discountAmount

    private val _orderStatus = MutableStateFlow<OrderStatus>(OrderStatus.None)
    val orderStatus: StateFlow<OrderStatus> get() = _orderStatus

    private val _isPromoApplied = MutableStateFlow(false)
    val isPromoApplied: StateFlow<Boolean> get() = _isPromoApplied

    private val _promoError = MutableStateFlow<String?>(null)
    val promoError: StateFlow<String?> get() = _promoError

    init {
        loadCartItems()
    }

    private fun loadCartItems() = viewModelScope.launch {
        getCartItemsUseCase.invoke().collect { items ->
            _cartItems.value = items
        }
    }

    val cartSubtotal: StateFlow<Double> = _cartItems
        .map { it.sumOf { cart -> cart.totalPrice } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val deliveryFee: StateFlow<Double> = cartSubtotal.map {
        if (it > 0 && it < 50) 2.0 else 0.0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val tax: StateFlow<Double> = combine(cartSubtotal, deliveryFee) { sub, delivery ->
        (sub + delivery) * 0.1
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cartTotal: StateFlow<Double> = combine(cartSubtotal, deliveryFee, tax, discountAmount) {
            sub, delivery, tax, discount ->
        max(0.0, sub + delivery + tax - discount)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun updateQuantity(itemId: Int, newQuantity: Int) {
        updateQuantityUseCase.invoke(itemId, newQuantity)
    }

    fun removeItem(itemId: Int) {
        removeFromCartUseCase.invoke(itemId)
    }

    fun createOrder(customerName: String? = null) = viewModelScope.launch {
        if (_cartItems.value.isEmpty()) {
            _orderStatus.value = OrderStatus.Error("Cart is empty")
            return@launch
        }
        _orderStatus.value = OrderStatus.Loading
        try {
            val orderName = customerName ?: "Guest"
            createOrderUseCase(
                customerName = orderName,
                items = _cartItems.value,
                promoCode = _promoCode.value,
                discount = _discountAmount.value,
                total = cartTotal.value
            ).onSuccess {
                clearCart()
                removePromoCode()
                _orderStatus.value = OrderStatus.Success("Order created successfully!")
            }.onFailure { exception ->
                _orderStatus.value = OrderStatus.Error(exception.message ?: "Failed to create order")
            }
        } catch (e: Exception) {
            _orderStatus.value = OrderStatus.Error(e.message ?: "Unknown error")
        }
    }

    fun applyPromoCode(code: String) = viewModelScope.launch {
        _promoError.value = null
        val result = applyPromoCodeUseCase(code, cartSubtotal.value)
        result.onSuccess { discount ->
            _promoCode.value = code
            _discountAmount.value = discount
            _isPromoApplied.value = true
        }.onFailure { exception ->
            _promoError.value = exception.message
            _promoCode.value = null
            _discountAmount.value = 0.0
            _isPromoApplied.value = false
        }
    }

    fun removePromoCode() {
        _promoCode.value = null
        _discountAmount.value = 0.0
        _isPromoApplied.value = false
        _promoError.value = null
    }

    fun clearPromoError() {
        _promoError.value = null
    }

    fun clearOrderStatus() {
        _orderStatus.value = OrderStatus.None
    }

    private fun clearCart() {
        _cartItems.value = emptyList()
    }
}