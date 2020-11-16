package com.example.restaurantapp.data

import android.util.Log
import androidx.lifecycle.LiveData

class RestaurantRepository(private val restaurantDao: RestaurantDao) {

    val readAllData: LiveData<List<Restaurant>> = restaurantDao.readAllData()
    val getFavoriteRestaurants: LiveData<List<Restaurant>> = restaurantDao.getFavoriteRestaurants()

    suspend fun addRestaurant(restaurant: Restaurant){
        restaurantDao.addRestaurant(restaurant)
    }

    fun getRestaurantById(id: Int): Restaurant{
        return restaurantDao.getRestaurantById(id)
    }

    suspend fun deleteRestaurant(restaurant: Restaurant){
        restaurantDao.deleteRestaurant(restaurant)
    }
}