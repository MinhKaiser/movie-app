package com.example.thuctapcosochuyennganh.DrawerScreen

import android.provider.ContactsContract.Profile
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thuctapcosochuyennganh.R
import com.example.thuctapcosochuyennganh.ViewModel.AuthViewModel
import com.example.thuctapcosochuyennganh.ViewModel.PersonalViewModel


@Composable
fun ProfileForm(personalViewModel: PersonalViewModel) {

    // Quan sát trạng thái AuthState
    val detailState = personalViewModel.userDetailState.value

    // Gọi fetchUserDetail khi ProfileForm được khởi tạo
    LaunchedEffect(Unit) {
        personalViewModel.fetchUserDetail()
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.TopStart
    ) {
        // Kiểm tra trạng thái loading, lỗi, hoặc hiển thị thông tin user
        when {
            detailState.loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            detailState.error != null -> {
                Text(
                    text = detailState.error ?: "Lỗi không xác định",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            detailState.userDetail != null -> {
                Profile(
                    username = detailState.userDetail?.userName ?: "Chưa có tên người dùng",
                    userId = detailState.userDetail?.userId ?: "Chưa có id"
                )
            }
            else -> {
                Text(
                    text = "Không có thông tin người dùng",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}


@Composable
fun Profile(username: String, userId: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(24.dp)
    ){
        Text(
            text = "Profile",
            style = MaterialTheme.typography.h5.copy(
                fontWeight = FontWeight.Bold,
                color = Color.Black
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        ProfileInfoItem(label = "Username", value = username, icon = Icons.Default.AccountBox)
        ProfileInfoItem(label = "Id", value = userId, icon = Icons.Default.Email)
    }
}

@Composable
fun ProfileInfoItem(
    label: String,
    value: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier
                .size(24.dp)
                .padding(end = 8.dp),
            tint = Color(0xFF6200EE)
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.caption.copy(color = Color.Gray),
                fontSize = 20.sp
            )
            Text(
                text = value,
                style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Medium),
                fontSize = 20.sp
            )
        }
    }
}
