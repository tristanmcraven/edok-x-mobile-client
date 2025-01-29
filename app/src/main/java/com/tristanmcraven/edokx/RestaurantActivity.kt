package com.tristanmcraven.edokx

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.tristanmcraven.edok.model.Restaurant
import com.tristanmcraven.edok.utility.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RestaurantActivity : AppCompatActivity() {

    private lateinit var textViewRestName: TextView
    private lateinit var containerCategories: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_restaurant)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        textViewRestName = findViewById(R.id.textViewRestName)
        containerCategories = findViewById(R.id.containerCategories)

        val rest = intent.getParcelableExtra<Restaurant>("restaurant")!! //pass class as a func argument for newer APIS (33+)
        textViewRestName.text = rest.name

        CoroutineScope(Dispatchers.IO).launch {
            val foods = ApiClient.IRestaurant.getFood(rest.id)!!
            val categories = foods.map { food ->
                ApiClient.IFoodCategory.getById(food.foodCategoryId)
            }.distinctBy { it!!.id }

            withContext(Dispatchers.Main) {
                // Add a default "Full Menu" button
                addCategoryButton("Full Menu")

                // Add buttons for each unique category
                categories.forEach { category ->
                    addCategoryButton(category!!.name)
                }
            }
        }
    }

    private fun addCategoryButton(categoryName: String) {
        val button = Button(this).apply {
            text = categoryName
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8, 0, 8, 0)
            }
        }
        button.setOnClickListener {
            // Handle category button clicks here
            Toast.makeText(this, "Clicked on $categoryName", Toast.LENGTH_SHORT).show()
        }
        containerCategories.addView(button)
    }
}