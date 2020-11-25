package com.example.restaurantapp.api

import com.example.restaurantapp.data.restaurant.CitiesResponseData
import com.example.restaurantapp.data.restaurant.RestaurantResponseData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface SimpleTableApi {

    @GET("restaurants")
    suspend fun getRestaurants(
            @QueryMap options: Map<String, String>
    ): Response<RestaurantResponseData>

    @GET("cities")
    suspend fun getCities(): Response<CitiesResponseData>
}