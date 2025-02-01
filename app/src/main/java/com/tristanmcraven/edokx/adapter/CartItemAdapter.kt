package com.tristanmcraven.edokx.adapter

import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.tristanmcraven.edok.model.CartItem
import com.tristanmcraven.edokx.R

class CartItemAdapter(private val cartItems: List<CartItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_SEPARATOR = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 0) VIEW_TYPE_ITEM else VIEW_TYPE_SEPARATOR
    }

    override fun getItemCount(): Int {
        return cartItems.size * 2 - 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_food_cart, parent, false)
            CartItemViewHolder
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    class CartItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgViewFood: ImageView = itemView.findViewById(R.id.imgFood)
        private val textViewFoodName: TextView = itemView.findViewById(R.id.textViewFoodName)
        private val textViewFoodPrice: TextView = itemView.findViewById(R.id.textViewFoodPrice)
        private val textViewFoodCount: TextView = itemView.findViewById(R.id.textViewFoodCount)

        fun bind(cartItem: CartItem) {
            imgViewFood.setImageBitmap(Base64.decode())
        }
    }
}