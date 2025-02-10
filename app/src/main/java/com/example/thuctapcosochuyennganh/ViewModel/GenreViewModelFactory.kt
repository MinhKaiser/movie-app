package com.example.thuctapcosochuyennganh.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class GenreViewModelFactory(private val authViewModel: AuthViewModel) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GenreViewModel::class.java)) {
            return GenreViewModel(authViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}