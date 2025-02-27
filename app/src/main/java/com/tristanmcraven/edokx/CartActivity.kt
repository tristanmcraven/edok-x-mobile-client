package com.tristanmcraven.edokx

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tristanmcraven.edok.model.CartItem
import com.tristanmcraven.edok.model.Restaurant
import com.tristanmcraven.edok.utility.ApiClient
import com.tristanmcraven.edokx.adapter.CartItemAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CartActivity : AppCompatActivity() {

    private lateinit var buttonGoBack: ImageButton
    private lateinit var textViewRestName: TextView
    private lateinit var cardViewNext: CardView
    private lateinit var recyclerViewCartItems: RecyclerView

    private lateinit var rest: Restaurant
    private lateinit var cartItems: List<CartItem>
    private lateinit var adapter: CartItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cart)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        retrieveIntentData()
        initViews()
        setupRecyclerView()
    }

    private fun initViews()
    {
        buttonGoBack = findViewById(R.id.buttonGoBack)
        buttonGoBack.setOnClickListener {
            onBackPressed()
        }
        cardViewNext = findViewById(R.id.cardViewNext)
        cardViewNext.setOnClickListener {
            val intent = Intent(this, CheckoutActivity::class.java)
            intent.putExtra("rest", rest)
            intent.putParcelableArrayListExtra("cartItems", ArrayList(cartItems))
            startActivity(intent)
        }

        textViewRestName = findViewById(R.id.textViewRestName)
        textViewRestName.text = rest.name
        recyclerViewCartItems = findViewById(R.id.recyclerViewCartItems)
    }

    private fun retrieveIntentData()
    {
        rest = intent.getParcelableExtra<Restaurant>("rest")!!
        cartItems = intent.getParcelableArrayListExtra<CartItem>("cartItems")?.toList()!!
    }

    private fun setupRecyclerView() {
        recyclerViewCartItems.layoutManager = LinearLayoutManager(this)
        adapter = CartItemAdapter(cartItems.toMutableList(),
            onIncrease = {increaseQuantity(it)},
            onDecrease = {decreaseQuantity(it)})
        recyclerViewCartItems.adapter = adapter
    }

    private fun increaseQuantity(item: CartItem) {
        item.foodQuantity++
        adapter.updateItem(item)

        CoroutineScope(Dispatchers.IO).launch {
            ApiClient.ICart.addItem(cartItems.first().cartId, item.id)
        }
    }

    private fun decreaseQuantity(item: CartItem) {
        if (item.foodQuantity > 1u) {
            item.foodQuantity--
            adapter.updateItem(item)
        } else {
            adapter.removeItem(item)
        }
        CoroutineScope(Dispatchers.IO).launch {
            ApiClient.ICart.decreaseQuantity(cartItems.first().cartId, item.id)
        }
    }
}