package com.example.thuctapcosochuyennganh.DataClass

data class User(
    val userId: String,
    val userName: String,
    val email: String,
    val jwtToken: String? = null
)

data class SignUpRequest(
    val username: String,
    val email: String,
    val password: String,
)

data class SignInRequest(
    val username: String,
    val password: String,
    val fcmtoken: String,
)

//data class SignOutRequest(
//    val fcmtoken: String
//)

data class UserDetailRespone(
    val userName: String,
    val userId: String
)

data class JwtToken(
    val token: String,
)

data class Message(
    val messageLogin: String
)


