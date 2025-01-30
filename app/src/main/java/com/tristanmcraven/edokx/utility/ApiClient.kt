package com.tristanmcraven.edok.utility

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.adapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tristanmcraven.edok.model.Category
import com.tristanmcraven.edok.model.Food
import com.tristanmcraven.edok.model.FoodCategory
import com.tristanmcraven.edok.model.Restaurant
import com.tristanmcraven.edok.model.RestaurantCategory
import com.tristanmcraven.edok.model.User
import com.tristanmcraven.edokx.utility.UIntJsonAdapter
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.reflect.Type


object ApiClient {
    private const val API_PATH = "http://10.0.2.2:5224/api/"
    private val moshi = Moshi.Builder().add(UIntJsonAdapter()).add(KotlinJsonAdapterFactory()).build()
    private val httpClient = OkHttpClient()

    fun <T> sendRequest(url: String, httpMethod: String, responseType: Type, body: Any? = null): T?
    {
        val request = buildRequest(url, httpMethod, body)
        val response = httpClient.newCall(request).execute()
        if (!response.isSuccessful) return null

        return response.body?.string()?.let { responseBody ->
            val adapter = moshi.adapter<T>(responseType)
            return adapter.fromJson(responseBody)
        }
    }

    fun sendRequest(url: String, httpMethod: String, body: Any? = null): Boolean?
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
    }

    object IRestaurant
    {
        fun get(): List<Restaurant>?
        {
            val type = object : TypeToken<List<Restaurant>>() {}.type
            return sendRequest("restaurant", "GET", type, body = null)
        }

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
}