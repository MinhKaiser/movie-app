package com.example.thuctapcosochuyennganh.ViewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thuctapcosochuyennganh.API.EpisodeAPIService
import com.example.thuctapcosochuyennganh.API.MovieAPIService
import com.example.thuctapcosochuyennganh.DataClass.Episode
import kotlinx.coroutines.launch

class EpisodeViewModel(authViewModel: AuthViewModel): ViewModel() {

    private val episodeService = EpisodeAPIService(authViewModel).episodeService

    data class EpisodeState(
        val loading: Boolean = true,
        val list: List<Episode> = emptyList(), //khi bắt đầu sẽ là list rỗng
        val error: String? = null, //cho biết chuỗi có thể rỗng
        val selectedEpisode: Episode? = null, // Tập phim được chọn
    )

    private val _episodeState = mutableStateOf(EpisodeState())
    val episodeState: State<EpisodeState> = _episodeState

    // Hàm để lấy tất cả các tập của một bộ phim theo movieId
    fun fetchAllEpisodesByMovieId(movieId: String) {
        _episodeState.value = EpisodeState(loading = true)
        viewModelScope.launch {
            try {
                val episodeList = episodeService.getAllEpisodesByIdMovie(movieId)
                _episodeState.value = _episodeState.value.copy(
                    list = episodeList,
                    loading = false,
                    error = null
                )
            } catch (e: Exception) {
                _episodeState.value = _episodeState.value.copy(
                    loading = false,
                    error = "Lỗi kết nối, vui lòng kiểm tra lại kết nối mạng ${e.message}"
                )
            }
        }
    }

    // Hàm để lấy thông tin chi tiết của một tập phim khi người dùng chọn tập
    fun fetchEpisodeDetails(movieId: String, episodeId: String) {
        _episodeState.value = EpisodeState(loading = true)
        viewModelScope.launch {
            try {
                val episode = episodeService.getEpisodeByIdMovie(movieId, episodeId)
                _episodeState.value = _episodeState.value.copy(
                    selectedEpisode = episode,
                    loading = false,
                    error = null
                )
            } catch (e: Exception) {
                _episodeState.value = _episodeState.value.copy(
                    loading = false,
                    error = "Lỗi kết nối, vui lòng kiểm tra lại kết nối mạng ${e.message}"
                )
            }
        }
    }
}