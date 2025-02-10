package com.example.thuctapcosochuyennganh.ViewModel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thuctapcosochuyennganh.API.MovieAPIService
import com.example.thuctapcosochuyennganh.API.PersonalAPIService
import com.example.thuctapcosochuyennganh.DataClass.ChangePassword
import com.example.thuctapcosochuyennganh.DataClass.UserDetailRespone
import com.example.thuctapcosochuyennganh.ViewModel.AuthViewModel.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PersonalViewModel(authViewModel: AuthViewModel) : ViewModel() {
    private val personalService = PersonalAPIService(authViewModel).personalService

    // Trạng thái quản lý thông tin người dùng
    data class UserDetailState(
        val loading: Boolean = true,
        val userDetail: UserDetailRespone? = null,
        val error: String? = null
    )

    // Trạng thái quản lý thay đổi mật khẩu
    data class ChangePasswordState(
        val loading: Boolean = false,
        val successMessage: String? = null,
        val error: String? = null
    )

    private val _userDetailState = mutableStateOf(UserDetailState())
    val userDetailState: State<UserDetailState> = _userDetailState

    private val _changePasswordState = mutableStateOf(ChangePasswordState())
    val changePasswordState: State<ChangePasswordState> = _changePasswordState

    // Lấy thông tin chi tiết người dùng
    fun fetchUserDetail() {
        _userDetailState.value = UserDetailState(loading = true)

        viewModelScope.launch {
            try {
                val userDetail = personalService.getUserDetail()
                Log.d("UserDetail", "Lấy thông tin người dùng thành công: $userDetail") // Log thông tin người dùng
                _userDetailState.value = UserDetailState(
                    loading = false,
                    userDetail = userDetail
                )
            } catch (e: Exception) {
                Log.e("UserDetail", "Lỗi khi lấy thông tin người dùng: ${e.message}") // Log lỗi
                _userDetailState.value = UserDetailState(
                    loading = false,
                    error = "Không thể tải thông tin người dùng: ${e.message}"
                )
            }
        }
    }

    // Đổi mật khẩu
    fun changePassword(oldPassword: String, newPassword: String) {
        _changePasswordState.value = ChangePasswordState(loading = true)
        Log.d("ChangePassword", "Bắt đầu đổi mật khẩu...") // Log bắt đầu quá trình

        viewModelScope.launch {
            try {
                Log.d("ChangePassword", "Gửi yêu cầu đổi mật khẩu với dữ liệu: oldPassword=$oldPassword, newPassword=$newPassword")
                val response = personalService.changePassword(ChangePassword(oldPassword, newPassword))
                Log.d("ChangePassword", "Đổi mật khẩu thành công: $response") // Log phản hồi thành công

                _changePasswordState.value = ChangePasswordState(
                    loading = false,
                    successMessage = "Thay đổi mật khẩu thành công, vui lòng đăng nhập lại"
                )
            } catch (e: Exception) {
                Log.e("ChangePassword", "Lỗi khi đổi mật khẩu: ${e.message}", e) // Log lỗi chi tiết

                _changePasswordState.value = ChangePasswordState(
                    loading = false,
                    error = "Không thể đổi mật khẩu: ${e.message}"
                )
            }
        }
    }

    // Reset trạng thái của thay đổi mật khẩu
    fun resetChangePasswordState() {
        _changePasswordState.value = ChangePasswordState()
    }
}