package com.anton.shopcoffeapp.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.anton.shopcoffeapp.R
import com.anton.shopcoffeapp.domain.model.Popular
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView

class PopularAdapter(
    private val onItemClick: (Popular) -> Unit,
    private val onAddToCart: (Popular) -> Unit
) : ListAdapter<Popular, PopularAdapter.PopularViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewholder_popular, parent, false)
        return PopularViewHolder(view)
    }

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PopularViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val coffeeImageView: ShapeableImageView = itemView.findViewById(R.id.coffeeImageView)
        private val titleTextView: TextView = itemView.findViewById(R.id.titlePopularCoffee)
        private val priceTextView: TextView = itemView.findViewById(R.id.titlePriceCoffee)
        private val addButton: AppCompatImageButton = itemView.findViewById(R.id.button)

        fun bind(popular: Popular) {
            titleTextView.text = popular.title
            priceTextView.text = "$${popular.price}"

            Glide.with(itemView.context)
                .load(popular.picUrl)
                .into(coffeeImageView)

            itemView.setOnClickListener {
                onItemClick(popular)
            }

            addButton.setOnClickListener {
                onAddToCart(popular)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Popular>() {
        override fun areItemsTheSame(oldItem: Popular, newItem: Popular): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Popular, newItem: Popular): Boolean {
            return oldItem == newItem
        }
    }
}