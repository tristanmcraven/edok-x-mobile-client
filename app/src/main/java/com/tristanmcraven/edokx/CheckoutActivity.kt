package com.tristanmcraven.edokx

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.tristanmcraven.edok.model.Cart
import com.tristanmcraven.edok.model.CartItem
import com.tristanmcraven.edok.model.Restaurant
import com.tristanmcraven.edok.utility.ApiClient
import com.tristanmcraven.edokx.utility.GlobalVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CheckoutActivity : AppCompatActivity() {

    private lateinit var buttonPay: Button
    private lateinit var textViewOrderTotal: TextView
    private lateinit var editTextAddress: EditText
    private lateinit var buttonGoBack: ImageButton

    private lateinit var rest: Restaurant
    private lateinit var cartItems: List<CartItem>
    private var cartTotal: UInt = 0u
    private lateinit var cart: Cart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_checkout)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        retrieveIntentData()
        initViews()
        CoroutineScope(Dispatchers.IO).launch {
            cartTotal = ApiClient.ICart.getTotal(GlobalVM.carts!!.first { it.restaurantId == rest.id }.id)!!
            withContext(Dispatchers.Main) {
                textViewOrderTotal.text = "${cartTotal + 178u} ₽"
            }

        }
    }

    fun initViews() {
        textViewOrderTotal = findViewById(R.id.textViewOrderTotal)

        editTextAddress = findViewById(R.id.editTextAddress)

        buttonPay = findViewById(R.id.buttonPay)
        buttonPay.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val result = ApiClient.IOrder.create(GlobalVM.currentUser!!.id, rest.id, cart.id, editTextAddress.text.toString(), cartTotal + 178u)
                if (result != null) {
                    withContext(Dispatchers.Main) {
                        val intent = Intent(this@CheckoutActivity, OrderActivity::class.java)
                            .apply {
                                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                        intent.putExtra("orderNumber", result.id.toInt())
                        startActivity(intent)
                        finish()
                    }
                }
            }


        }

        buttonGoBack = findViewById(R.id.buttonGoBack)
        buttonGoBack.setOnClickListener {
            onBackPressed()
        }

    }

    fun retrieveIntentData() {
        rest = intent.getParcelableExtra<Restaurant>("rest")!!
        cartItems = intent.getParcelableArrayListExtra<CartItem>("cartItems")?.toList()!!
        cart = GlobalVM.carts!!.first { it.restaurantId == rest.id }
    }
}