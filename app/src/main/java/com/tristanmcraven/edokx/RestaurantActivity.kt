package com.tristanmcraven.edokx

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.tristanmcraven.edok.model.Cart
import com.tristanmcraven.edok.model.CartItem
import com.tristanmcraven.edok.model.Food
import com.tristanmcraven.edok.model.FoodCategory
import com.tristanmcraven.edok.model.Restaurant
import com.tristanmcraven.edok.utility.ApiClient
import com.tristanmcraven.edokx.adapter.FoodAdapter
import com.tristanmcraven.edokx.utility.GlobalVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RestaurantActivity : AppCompatActivity(), OnFoodAddedListener {

    private lateinit var textViewRestName: TextView
    private lateinit var containerCategories: ChipGroup
    private lateinit var recyclerViewFood: RecyclerView
//    private lateinit var buttonOrder: Button
    private lateinit var cardViewOrder: CardView
    private lateinit var buttonGoBack: ImageButton

    private lateinit var foodList: List<Food>
    private lateinit var categories: List<FoodCategory?>

    private var cart: Cart? = null
    private var cartItems: List<CartItem>? = null

    private lateinit var rest: Restaurant

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_restaurant)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUI()

        rest = intent.getParcelableExtra<Restaurant>("restaurant")!! //pass class as a func argument for newer APIs (33+)
        textViewRestName.text = rest.name

        CoroutineScope(Dispatchers.IO).launch {
            cart = GlobalVM.carts!!.filter { it.restaurantId == rest.id }.firstOrNull()
            if (cart == null) {
                cart = ApiClient.IUser.getActiveCartByRestId(GlobalVM.currentUser!!.id, rest.id)
            }
            cartItems = ApiClient.ICart.getItemsByCartId(cart!!.id)

            foodList = ApiClient.IRestaurant.getFood(rest.id)!!
            categories = foodList.map { food ->
                ApiClient.IFoodCategory.getById(food.foodCategoryId)
            }.distinctBy { it!!.id }

            withContext(Dispatchers.Main) {
                addCategoryChip("Всё меню", 0U)

                categories.forEach { category ->
                    addCategoryChip(category!!.name, category.id)
                }
                val zxc = containerCategories.children.first() as Chip
                zxc.performClick()
            }
        }

    }

    override fun onFoodAdded(food: Food) {
        CoroutineScope(Dispatchers.IO).launch {
            val cart = GlobalVM.carts!!.first { it.restaurantId == food.restaurantId }
            ApiClient.ICart.addItem(cart.id, food.id)

            cartItems = ApiClient.ICart.getItemsByCartId(cart.id)
        }
    }

    private fun initUI()
    {
        textViewRestName = findViewById(R.id.textViewRestName)
        containerCategories = findViewById(R.id.containerCategories)
        recyclerViewFood = findViewById(R.id.recyclerViewFood)
//        buttonOrder = findViewById(R.id.buttonOrder)
        cardViewOrder = findViewById(R.id.cardViewOrder)
        recyclerViewFood.layoutManager = GridLayoutManager(this, 2)
        recyclerViewFood.addItemDecoration(GridSpacingItemDecoration(16))
        containerCategories.isSelectionRequired = true

        cardViewOrder.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            intent.putParcelableArrayListExtra("cartItems", ArrayList(cartItems))
            intent.putExtra("rest", rest)
            startActivity(intent)
        }

        buttonGoBack = findViewById(R.id.buttonGoBack)
        buttonGoBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun addCategoryChip(categoryName: String, categoryId: UInt) {

        val backgroundColorStateList = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked), // When chip is selected
                intArrayOf(-android.R.attr.state_checked) // When chip is not selected
            ),
            intArrayOf(
                getColor(R.color.selected_chip_color), // Selected background color
                getColor(R.color.transparent)  // Default background color
            )
        )

        // Create the ColorStateList for stroke colors
        val strokeColorStateList = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked), // When chip is selected
                intArrayOf(-android.R.attr.state_checked) // When chip is not selected
            ),
            intArrayOf(
                getColor(R.color.transparent), // Selected stroke color
                getColor(R.color.transparent)  // Default stroke color
            )
        )

        val chip = Chip(this).apply {
            text = categoryName
            isClickable = true
            isCheckable = true
            isCheckedIconVisible = false
            chipBackgroundColor = backgroundColorStateList
            chipStrokeColor = strokeColorStateList
            chipStrokeWidth = 1f
            setTag(R.id.category_id, categoryId)
            setOnClickListener {
                if (this.text.toString() == "Всё меню") {
                    updateRecyclerViewWithFoodData(foodList)
                }
                else {
                    val tag = getTag(R.id.category_id)
                    val newFoodList = foodList.filter { it -> it.foodCategoryId == tag}
                    updateRecyclerViewWithFoodData(newFoodList)
                }
            }
        }

        containerCategories.addView(chip)

    }

    private fun updateRecyclerViewWithFoodData(newFoodList: List<Food>) {
        recyclerViewFood.adapter = FoodAdapter(newFoodList.toMutableList(), this)
        val adapter = recyclerViewFood.adapter as FoodAdapter
        adapter.setFoodList(newFoodList)
    }

}

interface OnFoodAddedListener {
    fun onFoodAdded(food: Food)
}

class GridSpacingItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val spanCount = 2

        if (position % spanCount == 0) {
            outRect.right = space / 2
        }
        else {
            outRect.left = space / 2
        }

        if (position >= spanCount) {
            outRect.top = space
        }
    }
}