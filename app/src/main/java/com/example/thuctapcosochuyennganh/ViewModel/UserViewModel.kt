package com.example.thuctapcosochuyennganh.ViewModel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thuctapcosochuyennganh.API.LoginAPIService
import com.example.thuctapcosochuyennganh.DataClass.ChangePassword
import com.example.thuctapcosochuyennganh.DataClass.SignInRequest
import com.example.thuctapcosochuyennganh.DataClass.SignUpRequest
import com.example.thuctapcosochuyennganh.DataClass.User
import com.example.thuctapcosochuyennganh.DataClass.UserDetailRespone
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException
import java.io.IOException


class AuthViewModel(private val authService: LoginAPIService.AuthAPIService) : ViewModel() {

    // Trạng thái để quản lý kết quả của các API
    data class AuthState(
        val loading: Boolean = false,
        val error: String? = null,
        val successMessage: String? = null,
        val jwtToken: String? = null,
        val user: User? = null,
        val userDetails: UserDetailRespone? = null
    )

    // Trạng thái Mutable State
    private val _authState = mutableStateOf(AuthState())
    val authState: State<AuthState> = _authState

    // Hàm cập nhật trạng thái
    fun updateAuthState(newState: AuthState) {
        _authState.value = newState
    }

    private suspend fun getFCMToken(): String {
        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            throw IOException("Không thể lấy FCM Token: ${e.message}")
        }
    }


    // Hàm đăng ký người dùng mới
    fun registerUser(userName: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                updateAuthState(authState.value.copy(loading = true, error = null))

                // Tạo đối tượng SignUpRequest từ dữ liệu form
                val register = SignUpRequest(
                    username = userName,
                    email = email,
                    password = password,
                )

                // Gửi request đăng ký
                val result = authService.registerUser(register)

                // Cập nhật trạng thái sau khi đăng ký thành công
                updateAuthState(authState.value.copy(
                    successMessage = "Đăng ký thành công!",
                    loading = false,
                    error = null // Xóa thông báo lỗi nếu có
                ))
            } catch (e: HttpException) {
                // Xử lý lỗi HTTP
                updateAuthState(authState.value.copy(
                    error = "Đăng ký thất bại do trùng tài khoản mật hoặc email",
                    loading = false,
                    successMessage = null // Reset thông báo thành công
                ))
            } catch (e: IOException) {
                // Xử lý lỗi mạng
                updateAuthState(authState.value.copy(
                    error = "Đăng ký thất bại, vui lòng kiểm tra đường truyền",
                    loading = false,
                    successMessage = null // Reset thông báo thành công
                ))
            }
        }
    }
    // Hàm đăng nhập
    fun loginUser(username: String, password: String) {
        viewModelScope.launch {
            try {
                // Bắt đầu trạng thái loading
                updateAuthState(authState.value.copy(loading = true, error = null))

                //Lấy FcmToken
                val fcmtoken = getFCMToken()


                // Tạo đối tượng SignInRequest chỉ cần userName và password để đăng nhập
                val login = SignInRequest(
                    username = username,
                    password = password,
                    fcmtoken = fcmtoken
                )

                // Gửi request login và nhận token từ server
                val token = authService.loginUser(login)

                // Kiểm tra token nếu nhận được
                if (token.token.isNotEmpty()) {
                    // Cập nhật trạng thái với token
                    updateAuthState(authState.value.copy(jwtToken = token.token, loading = false))
                } else {
                    updateAuthState(authState.value.copy(error = "Đăng nhập thất bại: Không nhận được token", loading = false))
                }
            } catch (e: HttpException) {
                // Xử lý lỗi HTTP
                updateAuthState(authState.value.copy(error = "Đăng nhập thất bại, sai thông tin tài khoản hoặc mật khẩu", loading = false))
            } catch (e: IOException) {
                // Xử lý lỗi mạng
                updateAuthState(authState.value.copy(error = "Đăng nhập thất bại, vui lòng kiểm tra đường truyền", loading = false))
            }
        }
    }

// Hàm đăng xuất và xóa FCM token, JWT token
    fun signOut() {
        viewModelScope.launch {
            try {
                // Bắt đầu trạng thái loading
                updateAuthState(authState.value.copy(loading = true, error = null))

                // Gửi yêu cầu đăng xuất
//                val message = authService.signOutUser(SignOutRequest(fcmtoken = getFCMToken()))

//                 Xóa FCM token khỏi Firebase
//                FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        println("FCM Token đã bị xóa thành công.")
//                    } else {
//                        println("Lỗi khi xóa FCM Token: ${task.exception?.message}")
//                    }
//                }

                // Cập nhật trạng thái sau khi đăng xuất
                updateAuthState(
                    authState.value.copy(
//                        successMessage = message,
                        loading = false,
                        jwtToken = null, // Xóa JWT token khỏi trạng thái
                        user = null // Xóa thông tin người dùng
                    )
                )

                // Thông báo thành công hoặc xử lý thất bại
                println("Đăng xuất thành công.")
            } catch (e: HttpException) {
                // Xử lý lỗi HTTP
                updateAuthState(
                    authState.value.copy(
                        error = "Đăng xuất thất bại: ${e.message}",
                        loading = false
                    )
                )
            } catch (e: IOException) {
                // Xử lý lỗi mạng
                updateAuthState(
                    authState.value.copy(
                        error = "Lỗi mạng. Vui lòng thử lại! ${e.message}",
                        loading = false
                    )
                )
            }
        }
    }

    fun getToken(): String? {
        return authState.value.jwtToken
    }

    //in ra FCMToken
    fun logFCMToken() {
        viewModelScope.launch {
            try {
                val fcmToken = getFCMToken()
                println("FCM Token: $fcmToken")
            } catch (e: Exception) {
                println("FCM Token Error: Lỗi khi lấy token: ${e.message}")
            }
        }
    }

}

