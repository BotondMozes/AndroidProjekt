package com.example.restaurantapp.repository

import com.example.restaurantapp.api.RetrofitInstance
import com.example.restaurantapp.data.restaurant.CitiesResponseData
import com.example.restaurantapp.data.restaurant.RestaurantResponseData
import retrofit2.Response

class Repository {

    suspend fun getRestaurant(options: Map<String, String>): Response<RestaurantResponseData>{
        return RetrofitInstance.api.getRestaurants(options)
    }

    suspend fun getCities():Response<CitiesResponseData>{
        return RetrofitInstance.api.getCities()
    }
}