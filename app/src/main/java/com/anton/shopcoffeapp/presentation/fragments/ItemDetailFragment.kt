package com.anton.shopcoffeapp.presentation.fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.anton.shopcoffeapp.R
import com.anton.shopcoffeapp.data.model.CoffeeSize
import com.anton.shopcoffeapp.databinding.FragmentDetailBinding
import com.anton.shopcoffeapp.di.ViewModelFactory
import com.anton.shopcoffeapp.domain.model.Item
import com.anton.shopcoffeapp.features.itemdetail.viewmodel.ItemDetailViewModel
import com.anton.shopcoffeapp.presentation.ui.MainActivity
import com.bumptech.glide.Glide
import javax.inject.Inject
import kotlinx.coroutines.launch

class ItemDetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: ItemDetailViewModel by viewModels { viewModelFactory }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as MainActivity).itemDetailComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val item = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("item", Item::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable("item")
        }

        if (item != null) {
            viewModel.setInitialItem(item)
        } else {
            findNavController().popBackStack()
            return
        }

        setupObservers()
        setupListeners()
        setupUI()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.quantity.collect { qty ->
                    binding.titleQuantity.text = qty.toString()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedSize.collect { size ->
                    updateSizeSelection(size)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.subtotal.collect { subtotal ->
                    binding.titleSubTotalPrice.text = "$${"%.2f".format(subtotal)}"
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedItem.collect { item ->
                    item?.let { bindItem(it) }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.backBtn.setOnClickListener { findNavController().popBackStack() }

        binding.titleMinus.setOnClickListener { viewModel.decreaseQuantity() }
        binding.titlePlus.setOnClickListener { viewModel.increaseQuantity() }

        binding.titleSmall.setOnClickListener { viewModel.changeSize(CoffeeSize.SMALL) }
        binding.titleMedium.setOnClickListener { viewModel.changeSize(CoffeeSize.MEDIUM) }
        binding.titleLarge.setOnClickListener { viewModel.changeSize(CoffeeSize.LARGE) }

        binding.titleAddToCart.setOnClickListener {
            viewModel.addToCart()
            showToast("The product has been added to the cart!")
            findNavController().navigate(R.id.cartFragment)
        }

        binding.picAddLover.setOnClickListener {
            showToast("Added to favorites")
        }
    }

    private fun setupUI() {
        viewModel.selectedItem.value?.let { bindItem(it) }
    }

    private fun bindItem(item: Item) {
        binding.titleCoffee.text = item.title
        binding.extraDescription.text = item.description
        binding.titleStar.text = "%.1f".format(item.rating)
        Glide.with(requireContext()).load(item.picUrl).into(binding.picMain)
    }

    private fun updateSizeSelection(selectedSize: CoffeeSize) {
        val sizes = listOf(
            binding.titleSmall to CoffeeSize.SMALL,
            binding.titleMedium to CoffeeSize.MEDIUM,
            binding.titleLarge to CoffeeSize.LARGE
        )

        sizes.forEach { (textView, size) ->
            if (size == selectedSize) {
                textView.setBackgroundResource(R.drawable.stroke_brown_bg)
            } else {
                textView.setBackgroundResource(0)
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}