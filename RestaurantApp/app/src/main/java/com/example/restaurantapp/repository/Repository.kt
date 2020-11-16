package com.example.restaurantapp.repository

import com.example.restaurantapp.api.RetrofitInstance
import com.example.restaurantapp.data.ResponseData
import retrofit2.Response

class Repository {

    suspend fun getRestaurant(options: Map<String, String>): Response<ResponseData>{
        return RetrofitInstance.api.getRestaurants(options)
    }
}