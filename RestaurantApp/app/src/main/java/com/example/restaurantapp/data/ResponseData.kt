package com.example.restaurantapp.data

data class RestaurantResponseData (
        val total_entries: Int,
        val per_page: Int,
        val current_page: Int,
        val restaurants: List<Restaurant>
)

data class CitiesResponseData(
        val count: Int,
        val cities: List<String>
)