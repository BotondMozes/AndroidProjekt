package com.example.restaurantapp.data.restaurant

import androidx.lifecycle.LiveData

class RestaurantRepository(private val restaurantDao: RestaurantDao) {

    val readAllData: LiveData<List<Restaurant>> = restaurantDao.readAllData()

    suspend fun addRestaurant(restaurant: Restaurant){
        restaurantDao.addRestaurant(restaurant)
    }

    fun getRestaurantById(id: Long): LiveData<Restaurant>{
        return restaurantDao.getRestaurantById(id)
    }

    suspend fun deleteRestaurant(restaurant: Restaurant){
        restaurantDao.deleteRestaurant(restaurant)
    }

    fun getFavoriteRestaurants(page: Int, per_page: Int): LiveData<List<Restaurant>>{
        return restaurantDao.getFavoriteRestaurants(page, per_page)
    }


}