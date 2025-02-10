package com.example.thuctapcosochuyennganh.LoginAndRegister

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.thuctapcosochuyennganh.ViewModel.AuthViewModel

@Composable
fun RegisterForm(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val authState by authViewModel.authState
    val context = LocalContext.current

    // Các trường nhập liệu
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordCheck by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var errorMessages by remember { mutableStateOf<List<String>>(emptyList()) }

    // Lắng nghe khi chuyển giữa các tab, reset lỗi khi chuyển qua trang đăng ký
    LaunchedEffect(navController.currentBackStackEntry?.destination?.route) {
        errorMessages = emptyList() // Reset lỗi khi chuyển sang màn hình đăng ký
    }

    // Hàm kiểm tra thông tin đăng ký
    fun validateForm(): Boolean {
        val errors = mutableListOf<String>()

        if (username.isBlank()) {
            errors.add("Tài khoản không được để trống")
        } else if (username.length < 8) {
            errors.add("Tài khoản phải có ít nhất 8 ký tự")
        }

        if (email.isBlank()) {
            errors.add("Email không được để trống")
        } else if (!email.endsWith("@gmail.com")) {
            errors.add("Email phải kết thúc bằng @gmail.com")
        }

        if (password.isBlank()) {
            errors.add("Mật khẩu không được để trống")
        } else if (!isPasswordValid(password)) {
            errors.add("Mật khẩu phải có chữ cái viết hoa đầu tiên và ít nhất một ký tự đặc biệt")
        }

        if (passwordCheck.isBlank()) {
            errors.add("Vui lòng xác nhận mật khẩu")
        } else if (password != passwordCheck) {
            errors.add("Mật khẩu xác nhận không khớp")
        }

        if (errors.isNotEmpty()) {
            errorMessages = errors // Cập nhật danh sách lỗi
            return false
        }

        return true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Đăng Ký",
            modifier = Modifier.padding(bottom = 24.dp),
            style = MaterialTheme.typography.h5
        )

        // Trường nhập Username
        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                errorMessages = emptyList() // Reset lỗi khi thay đổi giá trị
            },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            isError = errorMessages.any { it.contains("Tài khoản") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Trường nhập Email
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                errorMessages = emptyList() // Reset lỗi khi thay đổi giá trị
            },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            isError = errorMessages.any { it.contains("Email") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Trường nhập Password
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                errorMessages = emptyList() // Reset lỗi khi thay đổi giá trị
            },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            isError = errorMessages.any { it.contains("Mật khẩu") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Trường nhập PasswordCheck
        OutlinedTextField(
            value = passwordCheck,
            onValueChange = {
                passwordCheck = it
                errorMessages = emptyList() // Reset lỗi khi thay đổi giá trị
            },
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            isError = errorMessages.any { it.contains("Mật khẩu xác nhận") }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Nút Đăng ký
        Button(
            modifier = Modifier
                .width(100.dp)
                .height(40.dp),
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFC6A2FA),
                contentColor = Color.Black
            ),
            onClick = {
                if (validateForm()) {
                    authViewModel.registerUser(
                        userName = username,
                        email = email,
                        password = password
                    )
                }
            },
            enabled = !authState.loading
        ) {
            if (authState.loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Đăng Ký")
            }
        }

        // Hiển thị thông báo lỗi từ ViewModel
        authState.error?.let { errorMessage ->
            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier.padding(top = 16.dp),
                style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold)
            )
        }


        // Hiển thị AlertDialog với phong cách đẹp hơn
        if (errorMessages.isNotEmpty()) {
            if (errorMessages.isNotEmpty()) {
                AlertDialog(
                    onDismissRequest = {},
                    title = {
                        Text(
                            text = "Thông báo lỗi",
                            style = MaterialTheme.typography.h6.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp // Tăng kích thước chữ tiêu đề
                            ),
                            color = Color(0xFFBB2D3B) // Màu đỏ cho tiêu đề
                        )
                    },
                    text = {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            errorMessages.forEach {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.body1.copy(
                                        fontWeight = FontWeight.Bold, // Đậm chữ
                                        fontSize = 16.sp // Tăng kích thước chữ
                                    ),
                                    color = Color(0xFFBB2D3B), // Màu đỏ cho thông báo lỗi
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier
                                .width(100.dp)
                                .height(40.dp),
                            onClick = { errorMessages = emptyList() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFBB2D3B),
                                contentColor = Color.White
                            ),
                        ) {
                            Text(
                                text = "Đóng",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp // Chữ đậm và cỡ chữ vừa phải
                            )
                        }
                    },
                    shape = RoundedCornerShape(16.dp), // Bo góc cho AlertDialog
                    modifier = Modifier.padding(16.dp)
                )
            }

        }
        // Hiển thị thông báo thành công nếu có
        authState.successMessage?.let { successMessage ->
            Text(
                text = "Đăng ký thành công",
                color = Color(0xFF01A803),
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Chuyển đến màn hình đăng ký
        TextButton(onClick = {
            authViewModel.updateAuthState(authState.copy(error = null))
            authViewModel.updateAuthState(authState.copy(successMessage = null))
            navController.navigate("LoginForm")
        }) {
            Text("Bạn đã có tài khoản? Quay lại đăng nhập")
        }
    }
}

// Kiểm tra điều kiện mật khẩu hợp lệ
fun isPasswordValid(password: String): Boolean {
    // Mật khẩu phải có chữ cái viết hoa đầu tiên và ít nhất một ký tự đặc biệt
    val regex = "^(?=.*[A-Z])(?=.*[@!]).{6,}$".toRegex()
    return regex.matches(password)
}






