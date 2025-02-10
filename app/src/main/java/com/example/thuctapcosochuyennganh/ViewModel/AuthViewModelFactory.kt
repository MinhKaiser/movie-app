package com.example.thuctapcosochuyennganh.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.thuctapcosochuyennganh.API.LoginAPIService
import com.example.thuctapcosochuyennganh.DataClass.Episode

class AuthViewModelFactory(private val authService: LoginAPIService.AuthAPIService) : ViewModelProvider.NewInstanceFactory() {

    // Tạo một đối tượng AuthViewModel
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            AuthViewModel(authService) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

