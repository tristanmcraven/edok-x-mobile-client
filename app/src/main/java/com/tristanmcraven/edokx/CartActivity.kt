package com.tristanmcraven.edokx

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.tristanmcraven.edok.model.CartItem
import com.tristanmcraven.edok.model.Restaurant

class CartActivity : AppCompatActivity() {

    private lateinit var buttonGoBack: Button
    private lateinit var textViewRestName: TextView
    private lateinit var buttonNext: Button

    private lateinit var rest: Restaurant
    private lateinit var cartItems: List<CartItem>

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

    }

    private fun initViews()
    {
        buttonGoBack = findViewById(R.id.buttonGoBack)
        buttonNext = findViewById(R.id.buttonNext)
        textViewRestName = findViewById(R.id.textViewRestName)
        textViewRestName.text = rest.name
    }

    private fun retrieveIntentData()
    {
        rest = intent.getParcelableExtra<Restaurant>("rest")!!
        cartItems = intent.getParcelableArrayListExtra<CartItem>("cartItems")?.toList()!!
    }
}