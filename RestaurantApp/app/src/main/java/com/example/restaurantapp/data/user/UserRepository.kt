package com.example.restaurantapp.data.user

import androidx.lifecycle.LiveData

class UserRepository(private val userDao: UserDao) {
    suspend fun addUser(user: User){
        userDao.addUser(user)
    }

    fun getUser(): LiveData<User> {
        return userDao.getUser()
    }
}