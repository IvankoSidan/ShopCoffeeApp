package com.anton.shopcoffeapp.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.anton.shopcoffeapp.R
import com.anton.shopcoffeapp.domain.model.Item
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView

class ItemListAdapter(
    private val onItemClick: (Item) -> Unit,
    private val onAddToCart: (Item) -> Unit
) : ListAdapter<Item, RecyclerView.ViewHolder>(DiffCallback) {

    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 0) VIEW_TYPE_LEFT else VIEW_TYPE_RIGHT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_LEFT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.viewholder_item_pic_left, parent, false)
                ItemLeftViewHolder(view)
            }
            VIEW_TYPE_RIGHT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.viewholder_item_pic_right, parent, false)
                ItemRightViewHolder(view)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ItemLeftViewHolder -> holder.bind(getItem(position))
            is ItemRightViewHolder -> holder.bind(getItem(position))
        }
    }

    inner class ItemLeftViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ShapeableImageView = itemView.findViewById(R.id.imageView)
        private val titleTextView: TextView = itemView.findViewById(R.id.textView)
        private val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        private val priceTextView: TextView = itemView.findViewById(R.id.textView2)

        fun bind(item: Item) {
            titleTextView.text = item.title
            priceTextView.text = "$${item.price}"
            ratingBar.rating = item.rating.toFloat()

            Glide.with(itemView.context)
                .load(item.picUrl)
                .into(imageView)

            itemView.setOnClickListener {
                onItemClick(item)
            }

            itemView.setOnLongClickListener {
                onAddToCart(item)
                true
            }
        }
    }

    inner class ItemRightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ShapeableImageView = itemView.findViewById(R.id.imageView)
        private val titleTextView: TextView = itemView.findViewById(R.id.textView)
        private val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        private val priceTextView: TextView = itemView.findViewById(R.id.textView2)

        fun bind(item: Item) {
            titleTextView.text = item.title
            priceTextView.text = "$${item.price}"
            ratingBar.rating = item.rating.toFloat()

            Glide.with(itemView.context)
                .load(item.picUrl)
                .into(imageView)

            itemView.setOnClickListener {
                onItemClick(item)
            }

            itemView.setOnLongClickListener {
                onAddToCart(item)
                true
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_LEFT = 0
        private const val VIEW_TYPE_RIGHT = 1
        val DiffCallback = object : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem == newItem
            }
        }
    }
}