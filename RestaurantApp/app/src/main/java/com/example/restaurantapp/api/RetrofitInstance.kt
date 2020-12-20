package com.example.restaurantapp.api

import com.example.restaurantapp.utils.Constants.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://ratpark-api.imok.space/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: SimpleTableApi by lazy {
        retrofit.create(SimpleTableApi::class.java)
    }
}