package com.example.restaurantapp.data

data class ResponseData (
        val total_entries: Int,
        val per_page: Int,
        val current_page: Int,
        val restaurants: List<Restaurant>
)