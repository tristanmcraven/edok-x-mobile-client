package com.tristanmcraven.edok.utility

import android.service.voice.VoiceInteractionSession.ActivityId
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.adapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tristanmcraven.edok.model.Cart
import com.tristanmcraven.edok.model.CartItem
import com.tristanmcraven.edok.model.Category
import com.tristanmcraven.edok.model.Food
import com.tristanmcraven.edok.model.FoodCategory
import com.tristanmcraven.edok.model.Order
import com.tristanmcraven.edok.model.Restaurant
import com.tristanmcraven.edok.model.RestaurantCategory
import com.tristanmcraven.edok.model.User
import com.tristanmcraven.edokx.utility.GlobalVM
import com.tristanmcraven.edokx.utility.UIntJsonAdapter
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.reflect.Type


object ApiClient {
    private const val API_PATH = "http://10.0.2.2:5224/api/"
    private val moshi = Moshi.Builder().add(UIntJsonAdapter()).add(KotlinJsonAdapterFactory()).build()
    private val httpClient = OkHttpClient()

    private fun <T> sendRequest(url: String, httpMethod: String, responseType: Type, body: Any? = null): T?
    {
        val request = buildRequest(url, httpMethod, body)
        val response = httpClient.newCall(request).execute()
        if (!response.isSuccessful) return null

        return response.body?.string()?.let { responseBody ->
            val adapter = moshi.adapter<T>(responseType)
            return adapter.fromJson(responseBody)
        }
    }

    private fun sendRequest(url: String, httpMethod: String, body: Any? = null): Boolean?
    {
        return try {
            val request = buildRequest(url, httpMethod, body)
            val response = httpClient.newCall(request).execute()
            return response.isSuccessful
        }
        catch (e: Exception)
        {
            null
        }

    }

    private fun buildRequest(url: String, httpMethod: String, body: Any?): Request
    {
        val requestBuilder = Request.Builder().url(API_PATH + url)

        if (body != null) {
            val jsonAdapter = moshi.adapter(Any::class.java)
            val json = jsonAdapter.toJson(body)
            val requestBody = json.toRequestBody("application/json".toMediaType())
            requestBuilder.method(httpMethod, requestBody)
        } else {
            requestBuilder.method(httpMethod, null)
        }

        return requestBuilder.build()
    }

    object IUser
    {
        fun login(login: String, password: String) : User?
        {
            val dto = mapOf(
                "Login" to login,
                "Password" to password
            )
            return sendRequest("user/login", "POST", User::class.java, dto)
        }

        fun getCarts(userId: UInt): List<Cart>?
        {
            val type = object: TypeToken<List<Cart>>() {}.type
            return sendRequest("user/$userId/carts", "GET", type)
        }

        fun getActiveCartByRestId(userId: UInt, restId: UInt): Cart?
        {
            return sendRequest("user/$userId/activecart?restid=$restId", "POST", Cart::class.java)
        }

        fun register(lastName: String, name: String, phone: String, email: String, login: String, password: String): User? {
            val dto = mapOf(
                "LastName" to lastName,
                "Name" to name,
                "Phone" to phone,
                "Email" to email,
                "Login" to login,
                "Password" to password
            )
            return sendRequest("user/register", "POST", User::class.java, dto)
        }
    }

    object IRestaurant
    {
        fun get(): List<Restaurant>?
        {
            val type = object : TypeToken<List<Restaurant>>() {}.type
            return sendRequest("restaurant", "GET", type, body = null)
        }

        fun getById(restId: UInt): Restaurant? = sendRequest("restaurant/$restId", "GET", Restaurant::class.java)

        fun getCategoriesById(id: UInt): List<RestaurantCategory>?
        {
            val type = object: TypeToken<List<RestaurantCategory>>() {}.type
            return sendRequest("restaurant/$id/categories", "GET", type, body = null)
        }

        fun getFood(id: UInt): List<Food>?
        {
            val type = object: TypeToken<List<Food>>() {}.type
            return sendRequest("restaurant/$id/food", "GET", type)
        }
    }

    object ICategory
    {
        fun getCategoryById(id: UInt): Category?
        {
            return sendRequest("category/$id", "GET", Category::class.java)
        }
    }

    object IFoodCategory
    {
        fun getById(id: UInt): FoodCategory?
        {
            return sendRequest("foodcategory/$id", "GET", FoodCategory::class.java)
        }
    }

    object ICart
    {
        fun addItem(cartId: UInt, foodId: UInt): Boolean?
        {
            return sendRequest("cart/$cartId/additem?foodId=$foodId", "POST", "")
        }

        fun decreaseQuantity(cartId: UInt, foodId: UInt): Boolean?
        {
            return sendRequest("cart/$cartId/decreasequantity?foodId=$foodId", "POST", "")
        }

        fun getItemsByCartId(cartId: UInt): List<CartItem>?
        {
            val type = object: TypeToken<List<CartItem>>() {}.type
            return sendRequest("cart/$cartId/items", "GET", type)
        }

        fun getTotal(cartId: UInt): UInt? {
            return sendRequest("cart/$cartId/total", "GET", UInt::class.java)
        }
    }

    object ICartItem
    {
        fun addItem(cartId: UInt, foodId: UInt): Boolean?
        {
//            val result = sendRequest("cart/$cartId/additem?foodId=$foodId", "POST")
//            if (result!!) {
//                GlobalVM.carts = IUser.getCarts(GlobalVM.currentUser!!.id)
//            }
//            return result
            return sendRequest("cart/$cartId/additem?foodId=$foodId", "POST", "")
        }
    }

    object IFood
    {
        fun getById(foodId: UInt): Food?
        {
            return sendRequest("food/$foodId", "GET", Food::class.java)
        }
    }

    object IOrder
    {
        fun create(userId: UInt, restId: UInt, cartId: UInt, address: String, total: UInt): Order?
        {
            val dto = mapOf(
                "UserId" to userId,
                "RestId" to restId,
                "CartId" to cartId,
                "Address" to address,
                "Total" to total
            )
            return sendRequest("order", "POST", Order::class.java, dto)
        }

        fun getById(orderId: UInt): Order? {
            return sendRequest("order/$orderId", "GET", Order::class.java)
        }

        fun getCartItems(orderId: UInt): List<CartItem>? {
            val type = object: TypeToken<List<CartItem>>() {}.type
            return sendRequest("order/$orderId/get_cart_items", "GET", type)
        }
    }
}