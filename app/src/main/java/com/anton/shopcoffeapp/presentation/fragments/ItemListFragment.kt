package com.anton.shopcoffeapp.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.anton.shopcoffeapp.R
import com.anton.shopcoffeapp.databinding.FragmentItemListBinding
import com.anton.shopcoffeapp.di.ViewModelFactory
import com.anton.shopcoffeapp.features.itemlist.viewmodel.ItemListViewModel
import com.anton.shopcoffeapp.presentation.adapters.ItemListAdapter
import com.anton.shopcoffeapp.presentation.ui.MainActivity
import javax.inject.Inject
import kotlinx.coroutines.launch

class ItemListFragment : Fragment() {
    private var _binding: FragmentItemListBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: ItemListViewModel by viewModels { viewModelFactory }

    private lateinit var adapter: ItemListAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity() as MainActivity).itemListComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progressBar.isVisible = true
        binding.recyclerView.isVisible = false

        setupAdapter()
        setupObservers()
        setupListeners()

        val categoryId = arguments?.getInt("categoryId", -1) ?: -1
        if (categoryId != -1) {
            viewModel.loadItems(categoryId = categoryId)
        } else {
            viewModel.loadItems()
        }
    }

    private fun setupAdapter() {
        adapter = ItemListAdapter(
            onItemClick = { item ->
                val bundle = bundleOf("item" to item)
                findNavController().navigate(R.id.itemDetailFragment, bundle)
            },
            onAddToCart = { item ->
                val bundle = bundleOf("item" to item)
                findNavController().navigate(R.id.itemDetailFragment, bundle)
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.items.collect { items ->
                        val isLoading = items.isEmpty()
                        binding.progressBar.isVisible = isLoading
                        binding.recyclerView.isVisible = !isLoading
                        adapter.submitList(items)
                    }
                }
                launch {
                    viewModel.selectedCategory.collect { category ->
                        binding.categoryTxt.text = category?.title ?: getString(R.string.all_products)
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.backImage.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}