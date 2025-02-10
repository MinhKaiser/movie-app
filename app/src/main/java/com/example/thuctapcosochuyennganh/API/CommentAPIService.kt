package com.example.thuctapcosochuyennganh.API

import com.example.thuctapcosochuyennganh.DataClass.Comment
import com.example.thuctapcosochuyennganh.DataClass.CommentPage
import com.example.thuctapcosochuyennganh.DataClass.Episode
import com.example.thuctapcosochuyennganh.DataClass.Movie
import com.example.thuctapcosochuyennganh.DataClass.MoviePage
import com.example.thuctapcosochuyennganh.ViewModel.AuthViewModel
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

class CommentAPIService(private val authViewModel: AuthViewModel) {
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

                // Thực thi yêu cầu
                chain.proceed(requestBuilder.build())
            }
            .build()

        // Retrofit với cả Scalars và Gson Converter
        retrofit = Retrofit.Builder()
            .baseUrl("https://movie.nxqdev.io.vn/api/")
            .addConverterFactory(ScalarsConverterFactory.create())  // Sử dụng ScalarsConverterFactory cho chuỗi
            .addConverterFactory(GsonConverterFactory.create())    // Sử dụng GsonConverterFactory cho JSON
            .client(okHttpClient)
            .build()
    }

    // Tạo API interface từ Retrofit
    val commentService: CommentAPIServiceInterface = retrofit.create(CommentAPIServiceInterface::class.java)

    interface CommentAPIServiceInterface {
        @GET("public/movies/episodes/{episodeId}/comments")
        suspend fun getAllCommentByEpisode(
            @Path("episodeId") episodeId: String,
            @Query("pageNumber") pageNumber: Int? = null
        ): CommentPage

        @POST("public/movies/episodes/{episodeId}/comments")
        suspend fun addComment(
            @Path("episodeId") episodeId: String,
            @Body content: String
        ): Comment

        @PUT("public/movies/episodes/comments/{commentId}")
        suspend fun updateComment(
            @Path("commentId") commentId: String,
            @Body content: String
        ): Comment

        // Xoá comment và trả về String thay vì JSON
        @DELETE("public/movies/episodes/comments/{commentId}")
        suspend fun deleteComment(
            @Path("commentId") commentId: String
        ): String // Trả về String thay vì JSON
    }
}