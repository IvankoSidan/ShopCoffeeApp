package com.anton.shopcoffeapp.presentation.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.anton.shopcoffeapp.R
import com.anton.shopcoffeapp.data.model.OrderStatus
import com.anton.shopcoffeapp.databinding.FragmentCartBinding
import com.anton.shopcoffeapp.di.ViewModelFactory
import com.anton.shopcoffeapp.features.cart.viewmodel.CartViewModel
import com.anton.shopcoffeapp.features.itemlist.viewmodel.ItemListViewModel
import com.anton.shopcoffeapp.presentation.adapters.CartAdapter
import com.anton.shopcoffeapp.presentation.ui.MainActivity
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: CartViewModel by viewModels { viewModelFactory }

    private lateinit var cartAdapter: CartAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val mainActivity = requireActivity() as MainActivity
        mainActivity.cartComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        setupObservers()
        setupListeners()
    }

    private fun setupAdapter() {
        cartAdapter = CartAdapter(
            onQuantityChanged = { itemId, quantity ->
                viewModel.updateQuantity(itemId, quantity)
            },
            onItemRemoved = { itemId ->
                showRemoveConfirmationDialog(itemId)
            }
        )

        binding.recyclerView.apply {
            adapter = cartAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cartItems.collect { cartItems ->
                    cartAdapter.submitList(cartItems)
                    updateEmptyState(cartItems.isEmpty())
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    viewModel.cartSubtotal,
                    viewModel.deliveryFee,
                    viewModel.tax,
                    viewModel.discountAmount,
                    viewModel.cartTotal
                ) { subtotal, delivery, tax, discount, total ->
                    updatePriceDisplay(subtotal, delivery, tax, discount, total)
                }.collect { }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isPromoApplied.collect { isApplied ->
                    updatePromoCodeUI(isApplied)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.promoError.collect { error ->
                    error?.let {
                        showToast(it)
                        viewModel.clearPromoError()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.orderStatus.collect { status ->
                    handleOrderStatus(status)
                }
            }
        }
    }

    private fun setupListeners() {
        binding.backImage.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.applyDiscountButton.setOnClickListener {
            handlePromoCodeAction()
        }

        binding.discountCodeEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                handlePromoCodeAction()
                true
            } else false
        }

        binding.checkoutBtn.setOnClickListener {
            proceedToCheckout()
        }
    }

    private fun handlePromoCodeAction() {
        if (viewModel.isPromoApplied.value) {
            viewModel.removePromoCode()
            binding.discountCodeEditText.text.clear()
            showToast("Promo code removed")
        } else {
            val code = binding.discountCodeEditText.text.toString().trim()
            if (code.isNotEmpty()) {
                viewModel.applyPromoCode(code)
                hideKeyboard()
            } else {
                showToast("Please enter a promo code")
            }
        }
    }

    private fun proceedToCheckout() {
        if (viewModel.cartItems.value.isEmpty()) {
            showToast("Your cart is empty")
            return
        }
        showCheckoutDialog()
    }

    private fun showCheckoutDialog() {
        val editText = EditText(requireContext()).apply {
            hint = "Your name (optional)"
            setTextColor(ContextCompat.getColor(requireContext(), R.color.darkBrown))
            setHintTextColor(ContextCompat.getColor(requireContext(), R.color.darkBrown))
        }

        val dialog = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Checkout")
            .setMessage("Enter your name for the order:")
            .setView(editText)
            .setPositiveButton("Place Order") { _, _ ->
                val customerName = editText.text.toString().trim()
                viewModel.createOrder(if (customerName.isNotEmpty()) customerName else null)
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun showRemoveConfirmationDialog(itemId: Int) {
        AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Remove Item")
            .setMessage("Are you sure you want to remove this item from cart?")
            .setPositiveButton("Remove") { _, _ ->
                viewModel.removeItem(itemId)
                showToast("Item removed from cart")
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updatePriceDisplay(subtotal: Double, delivery: Double, tax: Double, discount: Double, total: Double) {
        binding.subtotalValue.text = formatPrice(subtotal)
        binding.deliveryValue.text = formatPrice(delivery)
        binding.taxValue.text = formatPrice(tax)
        binding.totalValue.text = formatPrice(total)

        if (discount > 0) {
            binding.discountRow.visibility = View.VISIBLE
            binding.discountValue.text = "-${formatPrice(discount)}"
        } else {
            binding.discountRow.visibility = View.GONE
        }

        binding.deliveryLabel.text = if (delivery > 0) "Delivery Fee" else "Free Delivery"
    }

    private fun updatePromoCodeUI(isApplied: Boolean) {
        if (isApplied) {
            binding.applyDiscountButton.text = "Remove"
            binding.discountCodeEditText.isEnabled = false
            binding.discountCodeEditText.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
        } else {
            binding.applyDiscountButton.text = "Apply"
            binding.discountCodeEditText.isEnabled = true
            binding.discountCodeEditText.setTextColor(ContextCompat.getColor(requireContext(), R.color.darkBrown))
        }
    }

    private fun handleOrderStatus(status: OrderStatus) {
        when (status) {
            is OrderStatus.Loading -> {
                binding.checkoutBtn.isEnabled = false
                binding.checkoutBtn.text = "Processing..."
            }
            is OrderStatus.Success -> {
                binding.checkoutBtn.isEnabled = true
                binding.checkoutBtn.text = "Proceed to Checkout"
                showToast(status.message)
                findNavController().popBackStack()
                viewModel.clearOrderStatus()
            }
            is OrderStatus.Error -> {
                binding.checkoutBtn.isEnabled = true
                binding.checkoutBtn.text = "Proceed to Checkout"
                showToast(status.message)
                viewModel.clearOrderStatus()
            }
            else -> {
                binding.checkoutBtn.isEnabled = true
                binding.checkoutBtn.text = "Proceed to Checkout"
            }
        }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.recyclerView.visibility = View.GONE
            binding.footerLayout.visibility = View.GONE
            binding.discountLayout.visibility = View.GONE
            binding.emptyStateLayout.visibility = View.VISIBLE
        } else {
            binding.recyclerView.visibility = View.VISIBLE
            binding.footerLayout.visibility = View.VISIBLE
            binding.discountLayout.visibility = View.VISIBLE
            binding.emptyStateLayout.visibility = View.GONE
        }
    }

    private fun formatPrice(price: Double): String = "$${"%.2f".format(price)}"

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.discountCodeEditText.windowToken, 0)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}