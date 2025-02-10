package com.example.thuctapcosochuyennganh.MovieScreen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.thuctapcosochuyennganh.ViewModel.MovieViewModel

@Composable
fun MovieRating(movieViewModel: MovieViewModel) {
    val rateState = movieViewModel.rateState.value // Truy cập trực tiếp từ ViewModel

    var averageRating by remember { mutableStateOf(0f) }
    var ratingCount by remember { mutableStateOf(0) }
    var showDialog by remember { mutableStateOf(false) }

    // Hiển thị số lượt đánh giá
    val ratingCountText = if (ratingCount > 0) "$ratingCount lượt đánh giá" else "Chưa có đánh giá"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Hiển thị số lượt đánh giá
        Text(
            text = ratingCountText,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            fontSize = 25.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Hiển thị điểm trung bình với vòng tròn bao quanh
        AverageRatingCircle(averageRating)

        Spacer(modifier = Modifier.height(16.dp))

        RatingBar(averageRating)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier
                .width(120.dp)
                .height(40.dp),
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFC6A2FA), // Màu nền của Button
                contentColor = Color.Black          // Màu chữ bên trong Button
            ),
            onClick = { showDialog = true }
        ) {
            Text("Đánh giá")
        }
    }


    if (showDialog) {
        RatingDialog(
            onDismiss = { showDialog = false },
            onRate = { rating ->
                averageRating = (averageRating * ratingCount + rating) / (ratingCount + 1)
                ratingCount++
                movieViewModel.rateMovie("movieId", rating.toInt())  // Giả sử movieId có sẵn
            }
        )
    }

    // Hiển thị trạng thái đánh giá
    if (rateState.loading) {
        CircularProgressIndicator()
    } else {
        rateState.successMessage?.let { message ->
            Text(message, color = Color.Green)
        }
        rateState.error?.let { errorMessage ->
            Text(errorMessage, color = Color.Red)
        }
    }
}

@Composable
fun AverageRatingCircle(averageRating: Float) {
    val circleColor = when {
        averageRating >= 3.5f -> Color.Green
        averageRating >= 2f -> Color.Yellow
        else -> Color.Red
    }

    // Tính toán tỷ lệ phần trăm của vòng tròn
    val percentage = (averageRating / 5f) * 100f

    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 10f
            val radius = size.minDimension / 2 - strokeWidth / 2
            val startAngle = -90f // Bắt đầu từ trên cùng

            // Vẽ vòng tròn nền
            drawArc(
                color = Color.Gray,
                startAngle = startAngle,
                sweepAngle = 360f,
                useCenter = false,
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth)
            )

            // Vẽ vòng tròn phần trăm
            drawArc(
                color = circleColor,
                startAngle = startAngle,
                sweepAngle = 360f * (percentage / 100f),
                useCenter = false,
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth)
            )
        }

        // Hiển thị điểm trung bình trong vòng tròn
        Text(
            text = "%.1f".format(averageRating),
            style = MaterialTheme.typography.headlineLarge.copy(color = Color.White)
        )
    }
}


@Composable
fun RatingBar(rating: Float, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        for (i in 1..5) {
            val starFill = when {
                rating >= i -> 1f
                rating > i - 1 -> rating - (i - 1)
                else -> 0f
            }
            StarIcon(fillPercent = starFill)
        }
    }
}

@Composable
fun StarIcon(fillPercent: Float) {
    Canvas(modifier = Modifier.size(40.dp)) {
        val path = Path().apply {
            val midX = size.width / 2
            val midY = size.height / 2
            val radius = size.width / 2
            val innerRadius = radius / 2.5f
            val angle = (2 * Math.PI / 5).toFloat()
            moveTo(midX, midY - radius)
            for (i in 1..5) {
                lineTo(
                    midX + (innerRadius * Math.sin((i - 0.5) * angle).toFloat()),
                    midY - (innerRadius * Math.cos((i - 0.5) * angle).toFloat())
                )
                lineTo(
                    midX + (radius * Math.sin((i * angle).toDouble()).toFloat()),
                    midY - (radius * Math.cos((i * angle).toDouble()).toFloat())
                )
            }
            close()
        }

        drawPath(path, color = Color.Gray, style = Stroke(width = 4f))

        if (fillPercent > 0) {
            clipRect(right = size.width * fillPercent) {
                drawPath(path, color = Color.Yellow, style = Fill)
            }
        }
    }
}

@Composable
fun RatingDialog(onDismiss: () -> Unit, onRate: (Float) -> Unit) {
    var selectedRating by remember { mutableStateOf(0f) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Đánh giá bộ phim theo cảm nhận của bạn", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    for (i in 1..5) {
                        IconButton(onClick = { selectedRating = i.toFloat() }) {
                            StarIcon(fillPercent = if (i <= selectedRating) 1f else 0f)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    modifier = Modifier
                        .width(120.dp)
                        .height(40.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFC6A2FA),
                        contentColor = Color.Black ),
                    onClick = {
                    if (selectedRating > 0) {
                        onRate(selectedRating)
                        onDismiss()
                    }
                }) {
                    Text("Đánh giá")
                }
            }
        }
    }
}

