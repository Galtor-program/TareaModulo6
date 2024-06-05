package com.example.useraddreessbdbootcamp.viewmodels.user

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.useraddreessbdbootcamp.repository.MainRepository
import com.example.useraddreessbdbootcamp.viewmodels.address.AddressViewModel

class MyViewModelFactory(private val application: Application, private val repository: MainRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(application, repository) as T
        } else if (modelClass.isAssignableFrom(AddressViewModel::class.java)) {
            return AddressViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}