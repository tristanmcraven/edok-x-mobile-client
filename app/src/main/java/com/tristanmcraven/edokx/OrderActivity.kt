package com.tristanmcraven.edokx

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.card.MaterialCardView
import com.tristanmcraven.edok.model.CartItem
import com.tristanmcraven.edok.model.Order
import com.tristanmcraven.edok.model.Restaurant
import com.tristanmcraven.edok.utility.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class OrderActivity : AppCompatActivity() {

    private lateinit var textViewOrderNumber: TextView
    private lateinit var textViewRestName: TextView
    private lateinit var textViewRestAddress: TextView
    private lateinit var textViewStatus: TextView
    private lateinit var textViewStatusDescription: TextView
    private lateinit var textViewDeliveryTime: TextView
    private lateinit var MCVCheck: MaterialCardView
    private lateinit var MCVCooking: MaterialCardView
    private lateinit var MCVDelivering: MaterialCardView
    private lateinit var MCVDelivered: MaterialCardView
    private lateinit var linearLayoutOrderContents: LinearLayout

    private var orderNumber: UInt = 0u

    private lateinit var order: Order
    private lateinit var rest: Restaurant
    private lateinit var cartItems: List<CartItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_order)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        retrieveIntentData()
        initViews()

    }

    fun initViews() {
        textViewOrderNumber = findViewById(R.id.textViewOrderNumber)
        textViewRestName = findViewById(R.id.textViewRestName)
        textViewRestAddress = findViewById(R.id.textViewRestAddress)
        textViewStatus = findViewById(R.id.textViewStatus)
        textViewStatusDescription = findViewById(R.id.textViewStatusDescription)
        textViewDeliveryTime = findViewById(R.id.textViewDeliveryTime)
        MCVCheck = findViewById(R.id.MCVCheck)
        MCVCooking = findViewById(R.id.MCVCooking)
        MCVDelivering = findViewById(R.id.MCVDelivering)
        MCVDelivered = findViewById(R.id.MCVDelivered)
        linearLayoutOrderContents = findViewById(R.id.linearLayoutOrderContents)

        CoroutineScope(Dispatchers.IO).launch {
            order = ApiClient.IOrder.getById(orderNumber)!!
            rest = ApiClient.IRestaurant.getById(order.restaurantId)!!
            cartItems = ApiClient.IOrder.getCartItems(order.id)!!

            val views = mutableListOf<View>()

            cartItems.forEach {
                val food = ApiClient.IFood.getById(it.foodId)!!
                views.add(TextView(this@OrderActivity).apply {
                    text = "${it.foodQuantity} x ${food.name} (${it.foodQuantity} x ${food.price}₽)"
                })
            }
            views.add(View(this@OrderActivity).apply {
                setBackgroundColor(Color.BLACK)
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1)
            })

            views.addAll(
                listOf(
                    createTextView("Стоимость корзины: ${order.total - 178u}₽"),
                    createTextView("Стоимость доставки: 109₽"),
                    createTextView("Сервисный сбор: 69₽"),
                    createTextView("Общая сумма заказа: ${order.total}₽")
                )
            )

            withContext(Dispatchers.Main) {
                views.forEach {linearLayoutOrderContents.addView(it)}
                textViewOrderNumber.text = "Заказ №${order.id}"
                textViewRestName.text = "${rest.name}"
                textViewRestAddress.text = rest.address

                when (order.kitchenStatusId) {
                    1u -> {
                        textViewStatus.text = "Принято"
                        textViewStatusDescription.text = "Ресторан начнет готовить заказ, когда найдется свободный курьер"
                        textViewDeliveryTime.text = "Ориентировочное время доставки: ${getEstimatedDeliveryTime(order.createdAt)}"
                        MCVCheck.setCardBackgroundColor(Color.YELLOW)
                    }
                }
            }
        }
    }

    fun retrieveIntentData() {
        orderNumber = intent.getIntExtra("orderNumber", 0).toUInt()
    }

    fun createTextView(text: String) = TextView(this@OrderActivity).apply {
        this.text = text
    }

    private fun getEstimatedDeliveryTime(createdAt: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val orderTime = LocalDateTime.parse(createdAt, formatter)
        val estimatedTime = orderTime.plusMinutes(45)
        return estimatedTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    }
}