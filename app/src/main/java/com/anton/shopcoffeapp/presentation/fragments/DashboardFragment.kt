package com.anton.shopcoffeapp.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.anton.shopcoffeapp.R
import com.anton.shopcoffeapp.databinding.FragmentDashboardBinding
import com.anton.shopcoffeapp.di.ViewModelFactory
import com.anton.shopcoffeapp.domain.model.Item
import com.anton.shopcoffeapp.domain.model.Popular
import com.anton.shopcoffeapp.features.dashboard.viewmodel.DashboardViewModel
import com.anton.shopcoffeapp.presentation.adapters.CategoryAdapter
import com.anton.shopcoffeapp.presentation.adapters.PopularAdapter
import com.anton.shopcoffeapp.presentation.ui.MainActivity
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch
import javax.inject.Inject

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: DashboardViewModel by viewModels { viewModelFactory }

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var popularAdapter: PopularAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as MainActivity).dashboardComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bannerProgressBar.isVisible = true
        binding.bannerImageView.isVisible = false
        binding.categoryProgressBar.isVisible = true
        binding.categoryRecyclerView.isVisible = false
        binding.popularCoffeeProgressBar.isVisible = true
        binding.popularCoffeeRecyclerView.isVisible = false

        setupAdapters()
        setupObservers()
        setupListeners()
        viewModel.loadDashboard()
    }

    private fun setupAdapters() {
        categoryAdapter = CategoryAdapter { category ->
            val bundle = bundleOf("categoryId" to category.id)
            findNavController().navigate(R.id.action_dashboard_to_itemList, bundle)
        }

        popularAdapter = PopularAdapter(
            onItemClick = { popular ->
                val item = popular.toItem()
                val bundle = bundleOf("item" to item)
                findNavController().navigate(R.id.action_dashboard_to_itemDetail, bundle)
            },
            onAddToCart = { popular ->
                val item = popular.toItem()
                val bundle = bundleOf("item" to item)
                findNavController().navigate(R.id.action_dashboard_to_itemDetail, bundle)
            }
        )

        binding.categoryRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }

        binding.popularCoffeeRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = popularAdapter
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isLoading.collect { isLoading ->
                        if (!isLoading) {
                            binding.bannerProgressBar.isVisible = false
                            binding.categoryProgressBar.isVisible = false
                            binding.popularCoffeeProgressBar.isVisible = false
                            binding.bannerImageView.isVisible = true
                            binding.categoryRecyclerView.isVisible = true
                            binding.popularCoffeeRecyclerView.isVisible = true
                        }
                    }
                }
                launch {
                    viewModel.banner.collect { banners ->
                        if (banners.isNotEmpty()) {
                            Glide.with(requireContext())
                                .load(banners.first().url)
                                .into(binding.bannerImageView)
                        }
                    }
                }
                launch {
                    viewModel.categories.collect { categories ->
                        categoryAdapter.submitList(categories)
                    }
                }
                launch {
                    viewModel.popularItems.collect { items ->
                        popularAdapter.submitList(items)
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.seeAllTextView.setOnClickListener {
            val bundle = bundleOf("categoryId" to -1)
            findNavController().navigate(R.id.action_dashboard_to_itemList, bundle)
        }

        binding.searchInputEditText.onSearchClick = {
            val query = binding.searchInputEditText.text.toString().trim()
            if (query.isNotEmpty()) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val matchedItem = viewModel.findItemByQuery(query)
                    if (matchedItem != null) {
                        val bundle = bundleOf("item" to matchedItem)
                        findNavController().navigate(R.id.action_dashboard_to_itemDetail, bundle)
                    } else {
                        showToast("Item not found")
                    }
                }
            } else {
                showToast("Enter the search text")
            }
            binding.searchInputEditText.setText("")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun Popular.toItem() = Item(
        id = id,
        categoryId = 0,
        title = title,
        description = description,
        extra = extra,
        price = price,
        rating = rating,
        picUrl = picUrl
    )
}