package com.example.useraddreessbdbootcamp.viewmodels.user

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.useraddreessbdbootcamp.entities.User
import com.example.useraddreessbdbootcamp.repository.MainRepository
import kotlinx.coroutines.launch

class UserViewModel(
    application: Application,
    private var repository: MainRepository
): AndroidViewModel(application) {

    private val _userList = MutableLiveData<MutableList<User>>()
            val usersLV: LiveData<MutableList<User>>
                get() = _userList

    init{
        //getAllUser()
        viewModelScope.launch {
            val users = repository.getAllUsers()
            _userList.value =users
        }
    }

    fun getAllUser(){
         viewModelScope.launch {
            val users = repository.getAllUsers()
            _userList.value =users
        }
    }

    fun insertUser(user: User, onInserted: (Long) -> Unit )  =
        viewModelScope.launch {
            val userId = repository.insertUser(user)
            onInserted(userId)
            val users = repository.getAllUsers()
            _userList.value = users
        }

    fun updateUser(user: User) =
        viewModelScope.launch {
            repository.updateUser(user)
            val users = repository.getAllUsers()
            _userList.value = users

        }

    fun deleteUser(user: User) =
        viewModelScope.launch {
            repository.deleteUser(user)
            val users = repository.getAllUsers()
            _userList.value = users
        }

}