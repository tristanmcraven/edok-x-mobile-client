package com.tristanmcraven.edok.utility

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tristanmcraven.edok.model.Restaurant
import com.tristanmcraven.edok.model.User
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import java.lang.reflect.Type

object ApiClient {
    private const val API_PATH = "http://10.0.2.2:5224/api/"
    private val gson = Gson()
    private val httpClient = OkHttpClient()

    fun <T> sendRequest(url: String, httpMethod: String, responseType: Type, body: Any? = null): T?
    {
//        return try
//        {
            val request = buildRequest(url, httpMethod, body)
            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) return null
            return response.body?.string()?.let { gson.fromJson(it, responseType) }
//        }
//        catch (e: Exception)
//        {
//            null
//        }
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
        val builder = Request.Builder()
            .url(API_PATH + url)
            .method(httpMethod, body?.let {
                val jsonContent = gson.toJson(it)
                RequestBody.create(("application/json; charset=utf-8").toMediaType(), jsonContent)
            })

        return builder.build()
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
    }
}