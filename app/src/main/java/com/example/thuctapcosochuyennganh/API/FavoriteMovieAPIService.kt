package com.example.thuctapcosochuyennganh.API

import com.example.thuctapcosochuyennganh.DataClass.Movie
import com.example.thuctapcosochuyennganh.ViewModel.AuthViewModel
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

class FavoriteMovieAPIService(private val authViewModel: AuthViewModel) {
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
            .baseUrl("https://movie.nxqdev.io.vn/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }
val favoriteMovieService = retrofit.create(favoriteMovieAPIService::class.java)

interface favoriteMovieAPIService{
        @GET("public/movies/favorite")
        suspend fun getAllFavoriteMovie(): List<Movie>

        @POST("public/movies/favorite/{movieId}")
        suspend fun addFavoriteMovieById(
            @Path("movieId") movieId:String,
        ): Movie

        @DELETE("public/movies/favorite/{movieId}")
        suspend fun deleteFavoriteMovieById(
            @Path("movieId") movieId: String,
        ): Movie
    }
}