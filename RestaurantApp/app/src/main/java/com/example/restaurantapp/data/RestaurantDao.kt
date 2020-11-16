package com.example.restaurantapp.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RestaurantDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addRestaurant(restaurant: Restaurant)

    @Query("SELECT * FROM restaurants")
    fun readAllData(): LiveData<List<Restaurant>>

    @Query("SELECT * FROM restaurants WHERE id == :id")
    fun getRestaurantById(id: Int): Restaurant

    @Query("SELECT * FROM restaurants WHERE favorite == 1")
    fun getFavoriteRestaurants(): LiveData<List<Restaurant>>

    @Delete()
    suspend fun deleteRestaurant(restaurant: Restaurant)
}