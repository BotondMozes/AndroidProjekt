package com.example.restaurantapp.api

import android.util.Log
import com.example.restaurantapp.utils.Constants.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: SimpleTableApi by lazy {
        retrofit.create(SimpleTableApi::class.java)
    }
}