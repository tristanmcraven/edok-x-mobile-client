package com.tristanmcraven.edokx.adapter

import android.app.Application
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.tristanmcraven.edok.model.Food
import com.tristanmcraven.edok.utility.ApiClient
import com.tristanmcraven.edokx.OnFoodAddedListener
import com.tristanmcraven.edokx.R
import com.tristanmcraven.edokx.utility.GlobalVM.carts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FoodAdapter(private var foodList: MutableList<Food>, private val listener: OnFoodAddedListener) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgFood: ImageView = itemView.findViewById(R.id.imgFood)
        val textViewPrice: TextView = itemView.findViewById(R.id.textViewPrice)
        val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        val buttonAdd: Button = itemView.findViewById(R.id.buttonAdd)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = foodList[position]
        holder.textViewName.text = food.name
        holder.textViewPrice.text = food.price.toString() + " ₽"
        val rawImage = Base64.decode(food.photo, Base64.DEFAULT)
        holder.imgFood.setImageBitmap(BitmapFactory.decodeByteArray(rawImage, 0, rawImage.size))

        holder.buttonAdd.setOnClickListener {
            listener.onFoodAdded(food)
        }
    }

    override fun getItemCount() = foodList.size

    fun setFoodList(newFoodList: List<Food>) {
        foodList.clear()
        foodList.addAll(newFoodList)
        notifyDataSetChanged()
    }
}