package com.example.restaurantapp.fragments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.restaurantapp.data.CitiesResponseData
import com.example.restaurantapp.data.RestaurantResponseData
import com.example.restaurantapp.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response

class ListViewModel(private val repository: Repository): ViewModel() {

    val myResponse: MutableLiveData<Response<RestaurantResponseData>> = MutableLiveData()
    val cities: MutableLiveData<Response<CitiesResponseData>> = MutableLiveData()

    fun getRestaurants(options: Map<String, String>){
        viewModelScope.launch {
            val response = repository.getRestaurant(options)
            myResponse.value = response
        }
    }

    fun getCities(){
        viewModelScope.launch {
            val response = repository.getCities()
            cities.value = response
        }
    }

}