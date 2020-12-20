package com.example.restaurantapp.data.restaurant

data class RestaurantResponseData (
        val total_entries: Int,
        val per_page: Int,
        val page: Int,
        val restaurants: List<Restaurant>
)

data class CitiesResponseData(
        //val count: Int,
        val cities: List<String>
)