package com.example.thuctapcosochuyennganh.API

import com.example.thuctapcosochuyennganh.DataClass.Genre
import com.example.thuctapcosochuyennganh.DataClass.Movie
import com.example.thuctapcosochuyennganh.DataClass.SignInRequest
import com.example.thuctapcosochuyennganh.ViewModel.AuthViewModel
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

class GenreAPIService(private val authViewModel: AuthViewModel) {
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
    val genreService = retrofit.create(genreAPIService::class.java)

    interface genreAPIService{
        //Hiển thị danh sách các thể loại
        @GET("public/genres")
        suspend fun getAllGenre(): List<Genre>

        //Truyền vào list String này các thể loại từ danh sách thể loại sau đó trả về để hiển thị phim tương ứng
        @POST("public/genres/advance")
        suspend fun getGenreToList(@Body genre: List<String>): List<Movie>

    }
}