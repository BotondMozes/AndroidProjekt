package com.example.restaurantapp.data.user

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(restaurant: User)

    @Query("SELECT * FROM users")
    fun getUser(): LiveData<User>
}