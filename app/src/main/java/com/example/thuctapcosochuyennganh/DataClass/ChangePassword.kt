package com.example.thuctapcosochuyennganh.DataClass

data class ChangePassword(
    val oldPassword: String,
    val newPassword: String
)

data class MessageRespone(
    val message: String,
)