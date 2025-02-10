package com.example.thuctapcosochuyennganh.API

import com.example.thuctapcosochuyennganh.DataClass.ChangePassword
import com.example.thuctapcosochuyennganh.DataClass.Message
import com.example.thuctapcosochuyennganh.DataClass.MessageRespone
import com.example.thuctapcosochuyennganh.DataClass.Movie
import com.example.thuctapcosochuyennganh.DataClass.MoviePage
import com.example.thuctapcosochuyennganh.DataClass.UserDetailRespone
import com.example.thuctapcosochuyennganh.ViewModel.AuthViewModel
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

class PersonalAPIService(private val authViewModel: AuthViewModel) {
    private val retrofit: Retrofit

    init {
        // Tạo Retrofit với Interceptor tự động gắn token
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = authViewModel.getToken() // Lấy token từ AuthViewModel
                val requestBuilder = chain.request().newBuilder()

                // Gắn token vào header Authorization nếu có
                if (!token.isNullOrEmpty()) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }
                chain.proceed(requestBuilder.build())
            }
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl("https://movie.nxqdev.io.vn/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    // Tạo API interface từ Retrofit
    val personalService: personalAPIService = retrofit.create(personalAPIService::class.java)

    interface personalAPIService {
        @GET("user")
        suspend fun getUserDetail(): UserDetailRespone

        @POST("changepasswd")
        suspend fun changePassword(@Body changePassword: ChangePassword): MessageRespone
    }
}