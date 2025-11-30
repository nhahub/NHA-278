package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.repository.SettingsRepository

class SettingsViewModelFactory (private val repository: SettingsRepository)
    : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(SettingsViewModel::class.java)){
            SettingsViewModel(repository) as T

        }
        else{
            throw IllegalArgumentException("SettingsViewModelClass not found")
        }
    }

}