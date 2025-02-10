package com.example.thuctapcosochuyennganh.API

import com.example.thuctapcosochuyennganh.DataClass.Episode
import com.example.thuctapcosochuyennganh.DataClass.Movie
import com.example.thuctapcosochuyennganh.DataClass.MoviePage
import com.example.thuctapcosochuyennganh.ViewModel.AuthViewModel
import com.google.gson.Gson
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


class MovieAPIService(private val authViewModel: AuthViewModel) {
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

    // Tạo API interface từ Retrofit
    val movieService: movieAPIService = retrofit.create(movieAPIService::class.java)

    interface movieAPIService {
        @GET("public/movies/{movieId}")
        suspend fun getMovieById(@Path("movieId") movieId: String): Movie

        // Lấy danh sách tất cả phim với phân trang, sắp xếp
        @GET("public/movies")
        suspend fun getAllMovie(
            @Query("pageNumber") pageNumber: Int? = null,
        ): MoviePage

        @DELETE("admin/movies")
        suspend fun deleteMovieById(@Path("movieId") movieId:String): Movie

        @POST("admin/movies")
        suspend fun addMovie()

        @PUT("admin/movies/{movieId}")
        suspend fun updateMovieById(@Path("movieId") movieId: String): Movie

        @GET("public/movies/search/{name}")
        suspend fun getMoviesByName(@Path("name") name: String): List<Movie>

        @GET("public/movies/recommend")
        suspend fun getRecommendMovie(): List<Movie>

        @POST("public/movies/rate/{movieId}")
        suspend fun rateMovie(
            @Path("movieId") movieId: String,
            @Body score:Int
        ): String
    }
}