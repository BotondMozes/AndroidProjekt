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

    fun getRestaurantById(id: Int): LiveData<Restaurant>{
        return repository.getRestaurantById(id)
    }

    fun deleteRestaurant(restaurant: Restaurant){
        viewModelScope.launch (Dispatchers.IO){
            repository.deleteRestaurant(restaurant)
        }
    }

    fun getFavoriteRestaurants(page: Int, per_page: Int):LiveData<List<Restaurant>>{
        return repository.getFavoriteRestaurants(page, per_page)
    }

    fun getAllRestaurants():LiveData<List<Restaurant>>{
        return repository.readAllData
    }
}