package com.example.thuctapcosochuyennganh.API

import com.example.thuctapcosochuyennganh.API.MovieAPIService.movieAPIService
import com.example.thuctapcosochuyennganh.DataClass.ChangePassword
import com.example.thuctapcosochuyennganh.DataClass.Episode
import com.example.thuctapcosochuyennganh.DataClass.JwtToken
import com.example.thuctapcosochuyennganh.DataClass.Message
import com.example.thuctapcosochuyennganh.DataClass.Movie
import com.example.thuctapcosochuyennganh.DataClass.SignInRequest
import com.example.thuctapcosochuyennganh.DataClass.SignUpRequest
import com.example.thuctapcosochuyennganh.DataClass.User
import com.example.thuctapcosochuyennganh.DataClass.UserDetailRespone
import com.example.thuctapcosochuyennganh.ViewModel.AuthViewModel
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

class LoginAPIService() {


    interface AuthAPIService {

        @POST("signup")
        suspend fun registerUser(@Body signup: SignUpRequest): Message

        @POST("signin")
        suspend fun loginUser(@Body signin: SignInRequest): JwtToken

        @POST("signout")
        suspend fun signOutUser(): String
    }
}
