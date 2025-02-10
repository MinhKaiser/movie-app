package com.example.thuctapcosochuyennganh.ViewModel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thuctapcosochuyennganh.API.CommentAPIService
import com.example.thuctapcosochuyennganh.DataClass.Comment
import com.example.thuctapcosochuyennganh.DataClass.CommentPage
import kotlinx.coroutines.launch

class CommentViewModel(authViewModel: AuthViewModel) : ViewModel() {
    private val commentService = CommentAPIService(authViewModel).commentService

    // Trạng thái của danh sách comment
    data class CommentState(
        val loading: Boolean = true,
        val list: List<Comment> = emptyList(),
        val commentPage: CommentPage? = null,
        val error: String? = null
    )

    private val _commentsState = mutableStateOf(CommentState())
    val commentsState: State<CommentState> = _commentsState

    // Hàm lấy tất cả comment theo `episodeId` (phân trang)
    fun fetchAllComments(episodeId: String, pageNumber: Int) {
        viewModelScope.launch {

            _commentsState.value = _commentsState.value.copy(loading = true)
            try {
                val commentPage = commentService.getAllCommentByEpisode(pageNumber = pageNumber, episodeId = episodeId)

                Log.d(
                    "CommentViewModel",
                    "Fetched comments for page: ${commentPage.commentResponseDtoList.size} comments"
                )

                _commentsState.value = _commentsState.value.copy(
                    loading = false,
                    commentPage = commentPage,
                    list = commentPage.commentResponseDtoList
                )
            } catch (e: Exception) {
                // Log lỗi nếu có
                Log.e("CommentViewModel", "Error fetching comments: ${e.message}")
                _commentsState.value = _commentsState.value.copy(loading = false, error = e.message)
            }
        }
    }

    // Hàm chọn trang khi người dùng nhấn vào một trang cụ thể
    fun onPageSelected(page: Int, episodeId: String) {
        fetchAllComments(episodeId, page) // Gọi API để tải comment cho trang đó
    }

    // Chuyển sang trang tiếp theo
    fun nextPage(episodeId: String) {
        _commentsState.value.commentPage?.let { commentPage ->
            if (commentPage.pageNumber < commentPage.totalPages - 1) {
                fetchAllComments(episodeId, commentPage.pageNumber + 1)
            }
        }
    }

    // Chuyển sang trang trước
    fun previousPage(episodeId: String) {
        _commentsState.value.commentPage?.let { commentPage ->
            if (commentPage.pageNumber > 0) {
                fetchAllComments(episodeId, commentPage.pageNumber - 1)
            }
        }
    }

    // Thêm comment mới
    fun addComment(episodeId: String, content: String) {
        viewModelScope.launch {
            try {
                val newComment = commentService.addComment(episodeId, content)
                fetchAllComments(episodeId, pageNumber = 0)
            } catch (e: Exception) {
                _commentsState.value = _commentsState.value.copy(error = e.message)
            }
        }
    }

    fun updateComment(episodeId: String, commentId: String, content: String) {
        viewModelScope.launch {
            try {
                Log.d("CommentViewModel", "updateComment đã được gọi với episodeId: $episodeId, commentId: $commentId, content: $content")

                val updatedComment = commentService.updateComment(commentId, content)

                if (updatedComment != null) {
                    Log.d("CommentViewModel", "Cập nhật bình luận thành công: $updatedComment")
                    val currentPage = _commentsState.value.commentPage?.pageNumber ?: 0
                    fetchAllComments(episodeId, pageNumber = currentPage)
                } else {
                    Log.e("CommentViewModel", "Lỗi khi cập nhật bình luận.")
                    _commentsState.value = _commentsState.value.copy(error = "Bình luận không được cập nhật.")
                }
            } catch (e: Exception) {
                Log.e("CommentViewModel", "Lỗi khi cập nhật bình luận: ${e.message}")
                _commentsState.value = _commentsState.value.copy(error = "Lỗi khi cập nhật bình luận: ${e.message}")
            }
        }
    }

    // Hàm xoá bình luận
    fun deleteComment(episodeId: String, commentId: String) {
        viewModelScope.launch {
            _commentsState.value = _commentsState.value.copy(loading = true) // Đặt trạng thái loading
            try {
                Log.d("CommentViewModel", "Xoá bình luận - episodeId: $episodeId, commentId: $commentId")
                // Gọi API xoá comment
                commentService.deleteComment(commentId)

                // Kiểm tra xem cần tải lại trang nào
                val currentPage = _commentsState.value.commentPage?.pageNumber ?: 0
                val currentList = _commentsState.value.list

                Log.d("CommentViewModel", "Trạng thái hiện tại - currentPage: $currentPage, currentList size: ${currentList.size}")

                if (currentList.isNotEmpty() && currentList.first().commentid == commentId && currentPage > 0) {
                    // Nếu xoá comment đầu tiên của trang và không phải trang đầu, tải trang trước đó
                    fetchAllComments(episodeId, pageNumber = currentPage - 1)
                } else {
                    // Ngược lại, tải lại trang hiện tại
                    fetchAllComments(episodeId, pageNumber = currentPage)
                }

                Log.d("CommentViewModel", "Xoá bình luận thành công: $commentId")
            } catch (e: Exception) {
                // Log lỗi và cập nhật trạng thái error
                Log.e("CommentViewModel", "Lỗi khi xoá bình luận: ${e.message}")
                _commentsState.value = _commentsState.value.copy(
                    loading = false,
                    error = "Lỗi khi xoá bình luận: ${e.message}"
                )
            }
        }
    }

}

