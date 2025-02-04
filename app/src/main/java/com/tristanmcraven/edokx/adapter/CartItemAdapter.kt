package com.tristanmcraven.edokx.adapter

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tristanmcraven.edok.model.CartItem
import com.tristanmcraven.edok.model.Food
import com.tristanmcraven.edok.utility.ApiClient
import com.tristanmcraven.edokx.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartItemAdapter(
    private val cartItems: List<CartItem>,
    private val onIncrease: (CartItem) -> Unit,
    private val onDecrease: (CartItem) -> Unit
) : RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder>() {

    class CartItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgViewFood: ImageView = view.findViewById(R.id.imgFood)
        val textViewFoodName: TextView = view.findViewById(R.id.textViewFoodName)
        val textViewFoodPrice: TextView = view.findViewById(R.id.textViewFoodPrice)
        val textViewFoodCount: TextView = view.findViewById(R.id.textViewFoodCount)
        val buttonIncreaseCount: Button = view.findViewById(R.id.buttonIncreaseCount)
        val buttonReduceCount: Button = view.findViewById(R.id.buttonReduceCount)
        val separator: View = view.findViewById(R.id.separator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food_cart, parent, false)
        return CartItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val item = cartItems[position]
        lateinit var food: Food
        CoroutineScope(Dispatchers.IO).launch {
            food = ApiClient.IFood.getById(item.foodId)!!
            withContext(Dispatchers.Main) {
                holder.textViewFoodName.text = food.name
                holder.textViewFoodPrice.text = "${food.price} ₽"
                val rawImage = (Base64.decode(food.photo, Base64.DEFAULT))
                holder.imgViewFood.setImageBitmap(BitmapFactory.decodeByteArray(rawImage, 0, rawImage.size))
                holder.textViewFoodCount.text = item.foodQuantity.toString()

                holder.separator.visibility = if (position == cartItems.lastIndex) View.GONE else View.VISIBLE

                holder.buttonIncreaseCount.setOnClickListener { onIncrease(item) }
                holder.buttonReduceCount.setOnClickListener { onDecrease(item) }
            }
        }
    }

    override fun getItemCount(): Int = cartItems.size

}