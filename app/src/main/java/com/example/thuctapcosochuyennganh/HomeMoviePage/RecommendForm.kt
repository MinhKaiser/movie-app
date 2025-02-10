package com.example.thuctapcosochuyennganh.HomeMoviePage

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WbIncandescent
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.thuctapcosochuyennganh.DataClass.Movie
import com.example.thuctapcosochuyennganh.R
import com.example.thuctapcosochuyennganh.ViewModel.MovieViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecommendForm(navController: NavHostController, movieViewModel: MovieViewModel) {
    LaunchedEffect(Unit) {
        movieViewModel.fetchAllRecommendMovie()
    }

    val recommendState by movieViewModel.recommendState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Dòng text phía trên
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(3.dp)
                .border(
                    width = 2.dp,
                    color = Color.LightGray,
                    shape = RectangleShape
                )
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center).padding(10.dp),
                text = "CÓ THỂ BẠN SẼ THÍCH",
                fontSize = 19.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }

        // Kiểm tra trạng thái loading, error, hoặc dữ liệu
        when {
            recommendState.loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            recommendState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.WbIncandescent,
                            contentDescription = "No Movies",
                            tint = Color.Gray,
                            modifier = Modifier.size(50.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Bạn chưa xem phim gì,\nvui lòng xem một vài phim để chúng tôi biết sở thích của bạn",
                            color = Color.Gray,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(recommendState.list) { movie ->
                        RecommmendMovieItem(
                            movie = movie,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun RecommmendMovieItem(movie: Movie, navController: NavController){
    Row(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
            .border(BorderStroke(width = 2.dp, Color.DarkGray))
            .clickable {
                navController.navigate("MovieDetailForm/${movie.movieId}") // truyền movie id
            }
    ) {
        Column(Modifier.border(BorderStroke(width = 2.dp, Color.DarkGray))) {
            Image(
//                painter = painterResource(id = R.drawable.img),
                painter = rememberImagePainter(data = movie.image), // Load ảnh từ URL chính xác
                contentDescription = movie.title,
                modifier = Modifier
                    .width(100.dp) // Lấy toàn bộ chiều rộng của Card
                    .height(150.dp), // Chiều cao ảnh tùy chỉnh, có thể thay đổi
                contentScale = ContentScale.Crop // Cắt ảnh để vừa khung
            )
        }
        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .align(Alignment.CenterVertically)
        ) {
            androidx.compose.material3.Text(
                modifier = Modifier.padding(top = 3.dp, end = 3.dp),
                text = movie.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
