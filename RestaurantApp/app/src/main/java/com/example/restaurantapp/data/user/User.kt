package com.example.restaurantapp.data.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
        @PrimaryKey(autoGenerate = true)
        val id: Int,
        var name: String = "",
        var address: String = "",
        var phone: String = "",
        var email: String = "",
        var image: String = ""
)