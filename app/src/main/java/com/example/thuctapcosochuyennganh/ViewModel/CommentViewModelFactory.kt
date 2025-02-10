package com.example.thuctapcosochuyennganh.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CommentViewModelFactory(private val authViewModel: AuthViewModel) : ViewModelProvider.Factory{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CommentViewModel::class.java)) {
            return CommentViewModel(authViewModel) as T
        }
        throw IllegalArgumentException("Unknow ViewModel class")
    }
}
