package com.example.thuctapcosochuyennganh.ViewModel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thuctapcosochuyennganh.API.EpisodeAPIService
import com.example.thuctapcosochuyennganh.API.FavoriteMovieAPIService
import com.example.thuctapcosochuyennganh.DataClass.Movie
import com.example.thuctapcosochuyennganh.ViewModel.MovieViewModel.MovieState
import kotlinx.coroutines.launch

class FavoriteMovieViewModel(authViewModel: AuthViewModel):ViewModel() {
    private val favoriteMovieService = FavoriteMovieAPIService(authViewModel).favoriteMovieService

    data class FavoriteMovieState(
        val loading: Boolean = true,
        val list: List<Movie> = emptyList(),
        val error: String? = null //cho biết chuỗi có thể rỗng
    )

    private val _favoriteMovieState = mutableStateOf(FavoriteMovieState())
    val favoriteMovieState: State<FavoriteMovieState> = _favoriteMovieState

    init {
        fetchAllFavoriteMovie()
    }

    //Hiển thị danh sách phim yêu thích
    fun fetchAllFavoriteMovie(){
        _favoriteMovieState.value = FavoriteMovieState(loading = true)
        viewModelScope.launch {
            try {
                _favoriteMovieState.value = _favoriteMovieState.value.copy(
                    list = favoriteMovieService.getAllFavoriteMovie(),
                    loading = false,
                    error = null
                )
            }catch (e: Exception){
                _favoriteMovieState.value = _favoriteMovieState.value.copy(
                    loading = false,
                    error = "Lỗi ${e.message}"
                )
            }
        }
    }

    //Thêm phim vào danh sách yêu thích

    fun addFavoriteMovie(movieId: String) {
        viewModelScope.launch {
            Log.d("Favorite", "Thêm vào: $favoriteMovieState")
            _favoriteMovieState.value = _favoriteMovieState.value.copy(loading = true, error = null)
            try {
                val addMovie = favoriteMovieService.addFavoriteMovieById(movieId)
                _favoriteMovieState.value = _favoriteMovieState.value.copy(
                    loading = false,
                    list = _favoriteMovieState.value.list + addMovie // Thêm phim mới vào danh sách hiện tại
                )
            } catch (e: Exception) {
                _favoriteMovieState.value = _favoriteMovieState.value.copy(
                    loading = false,
                    error = "Lỗi ${e.message}"
                )
            }
        }
    }

    //Xoá phim khỏi danh sách yêu thích

    fun deleteFavoriteMovie(movieId: String) {
        _favoriteMovieState.value = _favoriteMovieState.value.copy(loading = true)
        viewModelScope.launch {
            try {
                favoriteMovieService.deleteFavoriteMovieById(movieId)
                _favoriteMovieState.value = _favoriteMovieState.value.copy(
                    list = _favoriteMovieState.value.list.filter { it.movieId != movieId },
                    loading = false,
                    error = null
                )
            } catch (e: Exception) {
                _favoriteMovieState.value = _favoriteMovieState.value.copy(
                    loading = false,
                    error = "Lỗi ${e.message}"
                )
            }
        }
    }

    // Kiểm tra phim có trong danh sách yêu thích không
    fun isFavorite(movieId: String): Boolean {
        return _favoriteMovieState.value.list.any { it.movieId == movieId }
    }

}