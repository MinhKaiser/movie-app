package com.example.thuctapcosochuyennganh.AppBar

import android.media.MediaPlayer
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.thuctapcosochuyennganh.LoginAndRegister.LoginForm
import com.example.thuctapcosochuyennganh.R
import com.example.thuctapcosochuyennganh.ViewModel.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    var alpha by remember { mutableStateOf(0f) }
    val context = LocalContext.current

    // Khởi tạo MediaPlayer để phát âm thanh
    val mediaPlayer = remember {
        MediaPlayer.create(context, R.raw.audio)
    }

    // Tạo Animatable để điều khiển chuyển động của chữ
    val movieOffset = remember { Animatable(-200f) } // MOVIE di chuyển từ trên xuống
    val hubOffset = remember { Animatable(200f) } // HUB di chuyển từ dưới lên

    // Tạo Animatable để điều khiển kích thước của chữ
    val movieScale = remember { Animatable(0.5f) } // MOVIE bắt đầu từ kích thước nhỏ
    val hubScale = remember { Animatable(0.5f) } // HUB bắt đầu từ kích thước nhỏ

    // Tạo Animatable cho opacity (độ mờ) của chữ HUB
    val hubAlpha = remember { Animatable(1f) } // HUB bắt đầu có độ mờ 100%

    // Phát âm thanh khi màn hình splash xuất hiện
    LaunchedEffect(key1 = true) {
        mediaPlayer?.start()
        alpha = 1f

        // Chuyển động đồng thời chữ MOVIE và HUB
        val animationDuration = 1500 // Thời gian dài hơn để phóng to

        launch {
            movieOffset.animateTo(
                targetValue = 0f, // Di chuyển đến vị trí 0
                animationSpec = tween(
                    durationMillis = animationDuration,
                    easing = FastOutSlowInEasing
                )
            )
            movieScale.animateTo(
                targetValue = 4f, // Phóng to chữ MOVIE gấp 4 lần (đủ màn hình)
                animationSpec = tween(
                    durationMillis = animationDuration,
                    easing = FastOutSlowInEasing
                )
            )
        }
        launch {
            hubOffset.animateTo(
                targetValue = 0f, // Di chuyển đến vị trí 0
                animationSpec = tween(
                    durationMillis = animationDuration,
                    easing = FastOutSlowInEasing
                )
            )
            hubScale.animateTo(
                targetValue = 3f, // Phóng to chữ HUB
                animationSpec = tween(
                    durationMillis = animationDuration,
                    easing = FastOutSlowInEasing
                )
            )
            // Giảm độ mờ của chữ HUB về 0 để biến mất dần
            hubAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = animationDuration,
                    easing = FastOutSlowInEasing
                )
            )
        }

        delay(2000) // Thời gian cho màn hình splash
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // Nền đen sau ảnh
    ) {
        // Thêm ảnh nền
        Image(
            painter = painterResource(id = R.drawable.img_1), // Thêm ảnh nền
            contentDescription = "Splash Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // Cân chỉnh ảnh cho phù hợp với màn hình
        )

        // Căn giữa chữ và áp dụng hiệu ứng lướt
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center) // Căn giữa nội dung trong Box
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo phần "MOVIE" - Lướt từ trên xuống và phóng to
                Text(
                    text = "M",
                    fontSize = 300.sp * movieScale.value, // Thay đổi kích thước chữ MOVIE
                    fontWeight = FontWeight.Bold,
                    color = Color.Red,
                    modifier = Modifier
                        .alpha(alpha) // Áp dụng hiệu ứng mờ dần
                        .offset(y = movieOffset.value.dp) // Di chuyển chữ "MOVIE"
                        .padding(end = 4.dp),
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color.Black,
                            offset = Offset(0f, 0f),
                            blurRadius = 100f
                        )
                    )
                )
                // Logo phần "HUB" - Lướt từ dưới lên, phóng to và dần biến mất
                Text(
                    text = "H",
                    fontSize = 300.sp * hubScale.value, // Thay đổi kích thước chữ HUB
                    fontWeight = FontWeight.Bold,
                    color = Color.Red,
                    modifier = Modifier
                        .alpha(hubAlpha.value) // Điều chỉnh độ mờ của chữ HUB
                        .offset(y = hubOffset.value.dp) // Di chuyển chữ "HUB"
                        .padding(start = 4.dp),
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color.Black,
                            offset = Offset(0f, 0f),
                            blurRadius = 100f
                        )
                    )
                )
            }
        }
    }

    // Giải phóng tài nguyên sau khi không còn cần thiết
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release() // Giải phóng tài nguyên MediaPlayer
        }
    }
}







