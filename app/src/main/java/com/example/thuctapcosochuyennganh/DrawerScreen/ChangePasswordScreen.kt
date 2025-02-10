package com.example.thuctapcosochuyennganh.DrawerScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.thuctapcosochuyennganh.ViewModel.AuthViewModel
import com.example.thuctapcosochuyennganh.ViewModel.PersonalViewModel
import kotlinx.coroutines.delay


@Composable
fun ChangedPassWordForm(
    navController: NavHostController,
    personalViewModel: PersonalViewModel,
    authViewModel: AuthViewModel
) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val changePasswordState by personalViewModel.changePasswordState

    // Reset lỗi mỗi khi quay lại màn hình này
    LaunchedEffect(navController.currentBackStackEntry?.destination?.route) {
        personalViewModel.resetChangePasswordState()
        errorMessage = null
    }

    // Kiểm tra mật khẩu mới có đúng yêu cầu không
    fun isNewPasswordValid(password: String): Boolean {
        return password.firstOrNull()?.isUpperCase() == true &&
                password.any { it in "@!#$%^&*()" } // Kiểm tra ký tự đặc biệt
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Đổi Mật Khẩu",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Nhập mật khẩu cũ
        OutlinedTextField(
            value = oldPassword,
            onValueChange = { oldPassword = it },
            label = { Text("Nhập mật khẩu hiện tại") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Nhập mật khẩu mới
        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("Mật khẩu mới") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Xác nhận mật khẩu mới
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Xác nhận mật khẩu mới") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Nút đổi mật khẩu
        Button(
            onClick = {
                errorMessage = null // Reset lỗi trước khi xử lý
                if (newPassword != confirmPassword) {
                    errorMessage = "Mật khẩu mới và xác nhận không khớp"
                } else if (!isNewPasswordValid(newPassword)) {
                    errorMessage = "Mật khẩu mới phải có chữ cái đầu tiên viết hoa và chứa ít nhất một ký tự đặc biệt (@, !, #, v.v.)"
                } else {
                    personalViewModel.changePassword(oldPassword, newPassword)
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFC6A2FA),
                contentColor = Color.Black
            ),
            modifier = Modifier
                .width(150.dp)
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text("Đổi mật khẩu")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hiển thị trạng thái
        when {
            changePasswordState.loading -> {
                CircularProgressIndicator()
            }
            changePasswordState.successMessage != null -> {
                Text(
                    text = changePasswordState.successMessage!!,
                    color = Color(0xFF01A803),
                    modifier = Modifier.padding(top = 16.dp)
                )
                LaunchedEffect(Unit) {
                    authViewModel.signOut()
                    // Đặt hiệu ứng để điều hướng khi thành công
                    personalViewModel.resetChangePasswordState() // Reset trạng thái
                    navController.navigate("LoginForm")
                }
            }
            changePasswordState.error != null -> {
                Text(
                    text = changePasswordState.error!!,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
            errorMessage != null -> {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}



