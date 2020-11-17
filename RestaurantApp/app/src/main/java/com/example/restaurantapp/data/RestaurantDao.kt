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
    fun getRestaurantById(id: Int): LiveData<Restaurant>

    @Query("SELECT * FROM restaurants WHERE favorite == 1 LIMIT :per_page OFFSET :page*:per_page")
    fun getFavoriteRestaurants(page: Int, per_page: Int): LiveData<List<Restaurant>>

    @Delete()
    suspend fun deleteRestaurant(restaurant: Restaurant)
}