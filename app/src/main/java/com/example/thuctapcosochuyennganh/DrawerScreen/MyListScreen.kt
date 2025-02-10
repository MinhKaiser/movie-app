package com.example.thuctapcosochuyennganh.DrawerScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.thuctapcosochuyennganh.DataClass.Movie
import com.example.thuctapcosochuyennganh.DataClass.User
import com.example.thuctapcosochuyennganh.HomeMoviePage.BrowserItemLibrary
import com.example.thuctapcosochuyennganh.R
import com.example.thuctapcosochuyennganh.ViewModel.AuthViewModel
import com.example.thuctapcosochuyennganh.ViewModel.FavoriteMovieViewModel
import com.example.thuctapcosochuyennganh.ViewModel.PersonalViewModel

@Composable
fun MyListForm(navController: NavHostController, favoriteMovieViewModel: FavoriteMovieViewModel, authViewModel: AuthViewModel, personalViewModel: PersonalViewModel) {

    val detailState = personalViewModel.userDetailState.value

    // Gọi fetchUserDetail khi ProfileForm được khởi tạo
    LaunchedEffect(Unit) {
        personalViewModel.fetchUserDetail()
    }

    val authState by authViewModel.authState
    LaunchedEffect(Unit) {
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start // Canh chỉnh bên trái
        ){
            // Icon và tên người dùng
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Account",
                modifier = Modifier.padding(end = 8.dp) // Khoảng cách giữa icon và tên người dùng
            )
            Column {
                Text(
                    text = detailState.userDetail?.userName ?: "Chưa có tên người dùng",
                    style = MaterialTheme.typography.body1 // Style cho tên người dùng
                )
            }
        }

        Row(modifier = Modifier.padding(top = 16.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_local_movies_24),
                contentDescription = "My Movie",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(text = "My Movie")
        }
        Divider()

        // Gọi fetchAllFavoriteMovie
        LaunchedEffect(Unit) {
            favoriteMovieViewModel.fetchAllFavoriteMovie() // Tải danh sách phim yêu thích khi bắt đầu
        }

        val favoriteMovieState by favoriteMovieViewModel.favoriteMovieState

        // Kiểm tra trạng thái loading, error, hoặc dữ liệu
        when {
            favoriteMovieState.loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            favoriteMovieState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.Text(text = "Error: ${favoriteMovieState.error}", color = Color.Red)
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1), // Chia làm 1 cột
                    modifier = Modifier.padding(8.dp) // Thêm khoảng cách ngoài
                ) {
                    items(favoriteMovieState.list) { movie ->
                        FavoriteMovieItem(
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
fun FavoriteMovieItem(movie: Movie, navController: NavController){
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

//@Preview
//@Composable
//fun FavoriteItemPreview(){
//    Row(
//        modifier = Modifier
//            .padding(5.dp)
//            .fillMaxWidth()
//            .border(BorderStroke(width = 2.dp, Color.DarkGray))
//    ) {
//        Column(Modifier.border(BorderStroke(width = 2.dp, Color.DarkGray))) {
//            Image(
//                painter = painterResource(id = R.drawable.img),
//                contentDescription = "null",
//                modifier = Modifier
//                    .width(100.dp) // Lấy toàn bộ chiều rộng của Card
//                    .height(150.dp), // Chiều cao ảnh tùy chỉnh, có thể thay đổi
//                contentScale = ContentScale.Crop,
//            )
//        }
//        Column(
//            modifier = Modifier
//                .padding(start = 16.dp)
//                .align(Alignment.CenterVertically)
//                .height(150.dp)
//        ) {
//            androidx.compose.material3.Text(
//                modifier = Modifier.padding(top = 3.dp, end = 3.dp),
//                text = "Em Sinh Viên Lol Ngon Nước Siêu Ngọt Xem Siêu Cấp Nứng",
//                fontSize = 20.sp,
//                fontWeight = FontWeight.Bold,
//                maxLines = 2,
//                overflow = TextOverflow.Ellipsis
//            )
//            Spacer(modifier = Modifier.padding(2.dp))
//            androidx.compose.material3.Text(
//                modifier = Modifier.padding(top = 1.dp, end = 6.dp),
//                text = "Đụ nhau tá lả Đụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lả,Đụ nhau tá lả Đụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lả Đụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lả Đụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lả Đụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lả Đụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lả Đụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lả Đụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lảĐụ nhau tá lả",
//                fontSize = 15.sp,
//                maxLines = 5,
//                overflow = TextOverflow.Ellipsis
//            )
//        }
//    }
//}

