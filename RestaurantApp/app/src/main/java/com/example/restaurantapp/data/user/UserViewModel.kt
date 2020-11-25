package com.example.restaurantapp.data.user

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application): AndroidViewModel(application) {

    private val repository: UserRepository

    init{
        val userDao = UserDatabase.getUserDatabase(application).userDao()
        repository = UserRepository(userDao)
    }

    fun addUser(user: User){
        viewModelScope.launch (Dispatchers.IO){
            repository.addUser(user)
        }
    }

    fun getUser(): LiveData<User> {
        return repository.getUser()
    }

}