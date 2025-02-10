package com.example.thuctapcosochuyennganh.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class EpisodeViewModelFactory(private val authViewModel: AuthViewModel) : ViewModelProvider.Factory{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EpisodeViewModel::class.java)) {
            return EpisodeViewModel(authViewModel) as T
        }
        throw IllegalArgumentException("Unknow ViewModel class")
    }
}
