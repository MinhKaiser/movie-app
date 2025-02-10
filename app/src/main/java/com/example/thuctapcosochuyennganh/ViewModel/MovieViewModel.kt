package com.example.thuctapcosochuyennganh.ViewModel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thuctapcosochuyennganh.API.GenreAPIService
import com.example.thuctapcosochuyennganh.API.MovieAPIService
import com.example.thuctapcosochuyennganh.DataClass.Episode
import com.example.thuctapcosochuyennganh.DataClass.Genre
import com.example.thuctapcosochuyennganh.DataClass.Message
import com.example.thuctapcosochuyennganh.DataClass.Movie
import com.example.thuctapcosochuyennganh.DataClass.MoviePage
import com.example.thuctapcosochuyennganh.ViewModel.EpisodeViewModel.EpisodeState
import kotlinx.coroutines.launch


class MovieViewModel(authViewModel: AuthViewModel) : ViewModel() {
    private val movieService = MovieAPIService(authViewModel).movieService

    data class RateState(
        val loading: Boolean = true,
        val successMessage: String? = null,
        val error: String? = null
    )

    data class RecommendState(
        val loading: Boolean = true,
        val list: List<Movie> = emptyList(),
        val error: String? = null,
    )

    data class MovieState(
        val loading: Boolean = true,
        val list: List<Movie> = emptyList(),
        val moviePage: MoviePage? = null,
        val error: String? = null
    )

    // Thêm trạng thái riêng cho danh sách tìm kiếm
    data class SearchState(
        val loading: Boolean = false,
        val searchResults: List<Movie> = emptyList(),
        val error: String? = null
    )

    data class DetailState(
        val loading: Boolean = false,
        val movie: Movie? = null,
        val error: String? = null
    )

    private val _moviesState = mutableStateOf(MovieState())
    val movieState: State<MovieState> = _moviesState

    private val _searchState = mutableStateOf(SearchState())
    val searchState: State<SearchState> = _searchState

    private val _detailState = mutableStateOf(DetailState())
    val detailState: State<DetailState> = _detailState

    private val _recommendState = mutableStateOf(RecommendState())
    val recommendState: State<RecommendState> = _recommendState

    private val _rateState = mutableStateOf(RateState())
    val rateState: State<RateState> = _rateState

    fun rateMovie(movieId: String, score: Int) {
        _rateState.value = RateState(loading = true)

        viewModelScope.launch {
            try {
                // Gọi API đánh giá phim
                val response = movieService.rateMovie(movieId, score)
                Log.d("MovieViewModel", "Đánh giá thành công: $response")

                _rateState.value = RateState(
                    loading = false,
                    successMessage = "Đánh giá thành công: $score sao!"
                )
            } catch (e: Exception) {
                // Xử lý lỗi khi đánh giá
                Log.e("MovieViewModel", "Lỗi khi đánh giá: ${e.message}")
                _rateState.value = RateState(
                    loading = false,
                    error = "Lỗi khi đánh giá phim: ${e.message}"
                )
            }
        }
    }

    fun fetchAllRecommendMovie(){
        _recommendState.value = RecommendState(loading = true)
        viewModelScope.launch {
            try {
                val recommendList = movieService.getRecommendMovie()
                _recommendState.value = _recommendState.value.copy(
                    list = recommendList,
                    loading = false,
                    error = null
                )
            } catch (e: Exception) {
                _recommendState.value = _recommendState.value.copy(
                    loading = false,
                    error = "Lỗi kết nối, vui lòng kiểm tra lại kết nối mạng ${e.message}"
                )
            }
        }
    }

    fun fetchAllMovie(pageNumber: Int) {
        viewModelScope.launch {
            // Log giá trị của pageNumber
            Log.d("MovieViewModel", "Fetching movies for page: $pageNumber")

            _moviesState.value = _moviesState.value.copy(loading = true)
            try {
                val moviePage = movieService.getAllMovie(pageNumber)

                // Log kết quả trả về từ API
                Log.d("MovieViewModel", "Fetched movie page: ${moviePage.movieList.size} movies")

                _moviesState.value = _moviesState.value.copy(
                    loading = false,
                    moviePage = moviePage,
                    list = moviePage.movieList // Cập nhật danh sách phim của trang
                )
            } catch (e: Exception) {
                // Log lỗi nếu có
                Log.e("MovieViewModel", "Error fetching movies: ${e.message}")
                _moviesState.value = _moviesState.value.copy(loading = false, error = e.message)
            }
        }
    }



    // Hàm chọn trang khi người dùng nhấn vào một trang cụ thể
    fun onPageSelected(page: Int) {
        fetchAllMovie(page) // Gọi lại API để tải phim cho trang đó
    }

    // Chuyển sang trang tiếp theo
    fun nextPage() {
        _moviesState.value.moviePage?.let { moviePage ->
            if (moviePage.pageNumber < moviePage.totalPages) {
                fetchAllMovie(moviePage.pageNumber + 1)
            }
        }
    }

    // Chuyển sang trang trước
    fun previousPage() {
        _moviesState.value.moviePage?.let { moviePage ->
            if (moviePage.pageNumber > 0) {
                fetchAllMovie(moviePage.pageNumber - 1)
            }
        }
    }

    // Tìm kiếm phim theo tên và cập nhật SearchState
    fun searchMoviesByName(query: String) {
        _searchState.value = SearchState(loading = true)

        viewModelScope.launch {
            try {
                // Gọi API để tìm kiếm phim theo tên
                val result = movieService.getMoviesByName(query)
                Log.d("MovieViewModel", "Tìm thấy phim từ elastic: ${result} movies")
                _searchState.value = SearchState(searchResults = result, loading = false)

            } catch (e: Exception) {
                _searchState.value = SearchState(error = "Không thể tìm kiếm phim: ${e.message}", loading = false)
            }
        }
    }


    fun fetchMovieById(movieId: String) {
        _detailState.value = DetailState(loading = true)
        viewModelScope.launch {
            try {
                val movie = movieService.getMovieById(movieId)
                _detailState.value = DetailState(movie = movie, loading = false)
            } catch (e: Exception) {
                _detailState.value = DetailState(
                    error = "Không thể tải thông tin phim: ${e.message}",
                    loading = false
                )
            }
        }
    }
}


//    fun fetchMovieById(movieId: String): Movie? {
//        _detailState.value = DetailState(loading = true)
//        var fetchedMovie: Movie? = null
//        viewModelScope.launch {
//            try {
//                val movie = movieService.getMovieById(movieId)
//                Log.d("MovieViewModel", "Tìm phim: ${movie} movies")
//                _detailState.value = DetailState(movie = movie, loading = false)
//                fetchedMovie = movie // Gán giá trị cho biến trả về
//            } catch (e: Exception) {
//                _detailState.value = DetailState(
//                    error = "Không thể tải thông tin phim: ${e.message}",
//                    loading = false
//                )
//            }
//        }
//        return fetchedMovie // Trả về movie (sẽ là null do coroutine chạy bất đồng bộ)
//    }
//class MovieViewModel:ViewModel() {
//    data class MovieState(
//        val loading: Boolean = true,
//        val list: List<Movie> = emptyList(),
//        val error: String? = null //cho biết chuỗi có thể rỗng
//    )
//
//    private val _moviesState = mutableStateOf(MovieState())
//
//    val movieState:State<MovieState> = _moviesState
//
//    init{
//        fetchAllMovie()
//    }
//
//    fun fetchAllMovie(){
//        _moviesState.value = MovieState(loading = true)
//        viewModelScope.launch {
//            try {
//                _moviesState.value = _moviesState.value.copy(
//                    list = movieService.getAllMovie(),
//                    loading = false,
//                    error = null
//                )
//            }catch (e: Exception){
//                _moviesState.value = _moviesState.value.copy(
//                    loading = false,
//                    error = "Lỗi kết nối, vui lòng kiểm tra lại kết nối mạng"
//                )
//            }
//        }
//    }
//
//    fun fetchMovieById(movieId:String){
//        _moviesState.value = MovieState(loading = true)
//        viewModelScope.launch {
//            try {
//                _moviesState.value  = movieState.value.copy(
//                    list = _moviesState.value.list + movieService.getMovieById(movieId),
//                    loading = false,
//                    error = null
//                )
//            }catch (e: Exception){
//                _moviesState.value = _moviesState.value.copy(
//                    loading = false,
//                    error = "Lỗi kết nối, vui lòng kiểm tra lại kết nối mạng"
//                )
//            }
//        }
//    }
//}
//
