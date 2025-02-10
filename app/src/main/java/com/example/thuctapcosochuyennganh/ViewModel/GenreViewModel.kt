package com.example.thuctapcosochuyennganh.ViewModel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thuctapcosochuyennganh.API.GenreAPIService
import com.example.thuctapcosochuyennganh.DataClass.Genre
import com.example.thuctapcosochuyennganh.DataClass.Movie
import kotlinx.coroutines.launch

class GenreViewModel(authViewModel: AuthViewModel) : ViewModel() {
    private val genreService = GenreAPIService(authViewModel).genreService

    // Trạng thái của Genre
    data class GenreState(
        val loading: Boolean = true,
        val list: List<Genre> = emptyList(),
        val selectedGenres: List<String> = emptyList(),
        val error: String? = null
    )

    // Trạng thái phim theo thể loại
    data class MoviesByGenreState(
        val loading: Boolean = false,
        val movies: List<Movie> = emptyList(),
        val error: String? = null
    )

    private val _genreState = mutableStateOf(GenreState())
    val genreState: State<GenreState> = _genreState

    private val _moviesByGenreState = mutableStateOf(MoviesByGenreState())
    val moviesByGenreState: State<MoviesByGenreState> = _moviesByGenreState

    // Lấy danh sách tất cả các thể loại
    fun fetchAllGenres() {
        _genreState.value = _genreState.value.copy(loading = true)

        viewModelScope.launch {
            try {
                val genres = genreService.getAllGenre()

                // Log danh sách thể loại
                Log.d("GenreViewModel", "Fetched genres: ${genres.size} items")

                _genreState.value = _genreState.value.copy(
                    loading = false,
                    list = genres,
                    error = null
                )
            } catch (e: Exception) {
                Log.e("GenreViewModel", "Error fetching genres: ${e.message}")
                _genreState.value = _genreState.value.copy(
                    loading = false,
                    error = "Không thể tải danh sách thể loại: ${e.message}"
                )
            }
        }
    }

    // Lấy danh sách phim theo thể loại đã chọn
    fun fetchMoviesBySelectedGenres() {
        val selectedGenres = _genreState.value.selectedGenres

        if (selectedGenres.isEmpty()) {
            _moviesByGenreState.value = _moviesByGenreState.value.copy(movies = emptyList())
            return
        }

        _moviesByGenreState.value = _moviesByGenreState.value.copy(loading = true)

        viewModelScope.launch {
            try {
                val movies = genreService.getGenreToList(selectedGenres)

                // Log danh sách phim trả về
                Log.d("GenreViewModel", "Fetched movies: ${movies.size} items")

                _moviesByGenreState.value = _moviesByGenreState.value.copy(
                    loading = false,
                    movies = movies,
                    error = null
                )
            } catch (e: Exception) {
                _moviesByGenreState.value = _moviesByGenreState.value.copy(
                    loading = false,
                    error = "Không thể tải danh sách phim: ${e.message}"
                )
            }
        }
    }

    // Chọn hoặc bỏ chọn thể loại
    fun toggleGenreSelection(genre: String) {
        val currentSelection = _genreState.value.selectedGenres
        val updatedSelection = if (currentSelection.contains(genre)) {
            currentSelection - genre
        } else {
            currentSelection + genre
        }

        _genreState.value = _genreState.value.copy(selectedGenres = updatedSelection)

        // Tự động tải phim sau khi cập nhật danh sách thể loại đã chọn
        fetchMoviesBySelectedGenres()
    }
}