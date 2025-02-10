package com.example.thuctapcosochuyennganh.LoginAndRegister

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.thuctapcosochuyennganh.ViewModel.AuthViewModel



@Composable
fun LoginForm(
    navController: NavHostController,
    authViewModel: AuthViewModel // Thêm ViewModel
) {
    val authState by authViewModel.authState // Lấy trạng thái từ ViewModel

    // Các trường nhập liệu
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) } // Biến để kiểm tra lỗi nhập liệu

    // Lắng nghe sự thay đổi của jwtToken để điều hướng nếu đăng nhập thành công
    LaunchedEffect(authState.jwtToken) {
        authState.jwtToken?.let {
            navController.navigate("AppBar")
        }
    }

    // Lắng nghe khi chuyển giữa các tab, reset lỗi khi chuyển qua trang đăng ký
    LaunchedEffect(navController.currentBackStackEntry?.destination?.route) {
        showError = false // Reset lỗi khi chuyển sang màn hình đăng ký
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Đăng Nhập",
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Trường nhập Username
        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                showError = false // Ẩn lỗi khi người dùng nhập lại
            },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            isError = showError && username.isBlank()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Trường nhập Password
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                showError = false // Ẩn lỗi khi người dùng nhập lại
            },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            isError = showError && password.isBlank()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Nút đăng nhập
        Button(
            modifier = Modifier
                .width(100.dp)
                .height(40.dp),
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFC6A2FA), // Màu nền của Button
                contentColor = Color.Black
            ),
            onClick = {
                if (username.isNotBlank() && password.isNotBlank()) {
                    // Gọi hàm login nếu các trường không trống
                    authViewModel.loginUser(username, password)
                } else {
                    // Hiển thị lỗi nếu có trường bị trống
                    showError = true
                }
            },
            enabled = !authState.loading // Vô hiệu hóa khi đang tải
        ) {
            if (authState.loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Login")
            }
        }

        // Hiển thị lỗi nếu có trường trống
        if (showError && (username.isBlank() || password.isBlank())) {
            Text(
                text = "Vui lòng điền đầy đủ Username và Password",
                color = Color.Red,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        // Hiển thị lỗi từ AuthState
        authState.error?.let { errorMessage ->
            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier.padding(top = 16.dp),
                style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Chuyển đến màn hình đăng ký
        TextButton(onClick = {
            authViewModel.updateAuthState(authState.copy(error = null)) // Reset lỗi khi chuyển tab
            navController.navigate("RegisterForm")
        }) {
            Text("Don't have an account? Click here to create")
        }
    }
}
