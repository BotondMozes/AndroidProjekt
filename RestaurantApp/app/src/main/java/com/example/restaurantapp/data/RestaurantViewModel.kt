package com.example.restaurantapp.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RestaurantViewModel(application: Application): AndroidViewModel(application) {

    private val repository: RestaurantRepository

    init{
        val restaurantDao = RestaurantDatabase.getRestaurantDatabase(application).restaurantDao()
        repository = RestaurantRepository(restaurantDao)
    }

    fun addRestaurant(restaurant: Restaurant){
        viewModelScope.launch (Dispatchers.IO){
            repository.addRestaurant(restaurant)
        }
    }

    fun getRestaurantById(id: Int): Restaurant{
        return repository.getRestaurantById(id)
    }

    fun deleteRestaurant(restaurant: Restaurant){
        viewModelScope.launch (Dispatchers.IO){
            repository.deleteRestaurant(restaurant)
        }
    }

    fun getFavoriteRestaurants():LiveData<List<Restaurant>>{
        return repository.getFavoriteRestaurants
    }

    fun getAllRestaurants():LiveData<List<Restaurant>>{
        return repository.readAllData
    }
}