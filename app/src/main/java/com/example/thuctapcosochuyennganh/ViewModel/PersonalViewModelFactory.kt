package com.example.thuctapcosochuyennganh.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PersonalViewModelFactory(private val authViewModel: AuthViewModel) : ViewModelProvider.Factory{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PersonalViewModel::class.java)) {
            return PersonalViewModel(authViewModel) as T
        }
        throw IllegalArgumentException("Unknow ViewModel class")
    }
}