package com.example.restaurantapp.data

import androidx.lifecycle.LiveData

class RestaurantRepository(private val restaurantDao: RestaurantDao) {

    val readAllData: LiveData<List<Restaurant>> = restaurantDao.readAllData()

    suspend fun addRestaurant(restaurant: Restaurant){
        restaurantDao.addRestaurant(restaurant)
    }

    fun getRestaurantById(id: Int): LiveData<Restaurant>{
        return restaurantDao.getRestaurantById(id)
    }

    suspend fun deleteRestaurant(restaurant: Restaurant){
        restaurantDao.deleteRestaurant(restaurant)
    }

    fun getFavoriteRestaurants(page: Int, per_page: Int): LiveData<List<Restaurant>>{
        return restaurantDao.getFavoriteRestaurants(page, per_page)
    }


}