package com.example.thuctapcosochuyennganh.API

import com.example.thuctapcosochuyennganh.API.MovieAPIService.movieAPIService
import com.example.thuctapcosochuyennganh.DataClass.Episode
import com.example.thuctapcosochuyennganh.DataClass.Movie
import com.example.thuctapcosochuyennganh.ViewModel.AuthViewModel
import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

class EpisodeAPIService(private val authViewModel: AuthViewModel) {
    private val retrofit: Retrofit

    init {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = authViewModel.getToken()
                val requestBuilder = chain.request().newBuilder()

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
    val episodeService: episodeAPIService = retrofit.create(episodeAPIService::class.java)

    interface episodeAPIService {
        //Lấy ra list episode của movie (public) - id của movie
        @GET("public/movies/{movieId}/episodes")
        suspend fun getAllEpisodesByIdMovie(@Path("movieId") movieId: String): List<Episode>

        //Lấy ra episode của movie (xem từng tập khi click vào button)
        @GET("public/movies/{movieId}/episodes/{episodeId}")
        suspend fun getEpisodeByIdMovie(
            @Path("movieId") movieId: String,
            @Path("episodeId") episodeId: String
        ): Episode

        //Thêm episode vào movie theo id của movie
        @POST("admin/movies/{movieId}/episodes")
        suspend fun addEpisodeToMovieByIdMovie(@Path("movieId") movieId: String): Episode
    }
}