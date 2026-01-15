package com.anton.shopcoffeapp.presentation.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.anton.shopcoffeapp.R
import com.anton.shopcoffeapp.domain.model.CartItem
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView

class CartAdapter(
    private val onQuantityChanged: (Int, Int) -> Unit,
    private val onItemRemoved: (Int) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewholder_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productImage: ShapeableImageView = itemView.findViewById(R.id.productImage)
        private val productTitle: TextView = itemView.findViewById(R.id.productTitle)
        private val unitPrice: TextView = itemView.findViewById(R.id.unitPrice)
        private val quantityText: TextView = itemView.findViewById(R.id.quantityText)
        private val totalPrice: TextView = itemView.findViewById(R.id.totalPrice)
        private val decreaseButton: TextView = itemView.findViewById(R.id.decreaseButton)
        private val increaseButton: TextView = itemView.findViewById(R.id.increaseButton)
        private val closeButton: ImageView = itemView.findViewById(R.id.closeButton)

        fun bind(cartItem: CartItem) {
            val item = cartItem.item

            productTitle.text = item.title
            unitPrice.text = "$${"%.2f".format(item.price)}"
            quantityText.text = cartItem.quantity.toString()
            totalPrice.text = "$${"%.2f".format(cartItem.totalPrice)}"

            Glide.with(itemView.context)
                .load(item.picUrl)
                .into(productImage)

            decreaseButton.setOnClickListener {
                val newQuantity = cartItem.quantity - 1
                if (newQuantity >= 0) {
                    onQuantityChanged(item.id, newQuantity)
                }
                Log.d("Clickable", "Minus item")
            }

            increaseButton.setOnClickListener {
                val newQuantity = cartItem.quantity + 1
                onQuantityChanged(item.id, newQuantity)
                Log.d("Clickable", "Plus item")
            }

            closeButton.setOnClickListener {
                onItemRemoved(item.id)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.item.id == newItem.item.id
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem == newItem
        }
    }
}