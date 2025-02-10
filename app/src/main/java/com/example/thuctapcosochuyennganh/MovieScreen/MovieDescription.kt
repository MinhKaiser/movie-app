package com.example.thuctapcosochuyennganh.MovieScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.thuctapcosochuyennganh.DataClass.Movie
import com.example.thuctapcosochuyennganh.R
import com.example.thuctapcosochuyennganh.ViewModel.EpisodeViewModel
import com.example.thuctapcosochuyennganh.ViewModel.FavoriteMovieViewModel
import com.example.thuctapcosochuyennganh.ViewModel.MovieViewModel

@Composable
fun MovieDetailForm(
    movie: Movie,
    episodeViewModel: EpisodeViewModel,
    movieViewModel: MovieViewModel,
    navController: NavHostController,
    favoriteMovieViewModel: FavoriteMovieViewModel,
) {
    val episodeState = episodeViewModel.episodeState.value
    val isFavorite = favoriteMovieViewModel.isFavorite(movie.movieId)

    // Khi movieId thay đổi, gọi API để lấy các tập phim
    LaunchedEffect(movie.movieId) {
        episodeViewModel.fetchAllEpisodesByMovieId(movie.movieId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 10.dp)
            .background(Color(0xFFF2F2F2))
    ) {
        // Row 1: Ảnh phim và thông tin cơ bản
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Image(
//                painter = painterResource(id = R.drawable.img), // Thay ảnh thích hợp
                painter = rememberImagePainter(data = movie.image), // Load ảnh từ URL chính xác
                contentDescription = "Movie Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(120.dp)
                    .height(180.dp)
                    .clip(RoundedCornerShape(5.dp))
            )

            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = movie.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Đạo diễn: " + movie.director,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Năm phát hành: ${movie.releaseYear}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Thể loại: ${movie.genres.joinToString(", ")}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Lượt xem: ${movie.view}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Button(
                    onClick = {
                        if (isFavorite) {
                            favoriteMovieViewModel.deleteFavoriteMovie(movie.movieId) // Xoá khỏi yêu thích
                        } else {
                            favoriteMovieViewModel.addFavoriteMovie(movie.movieId) // Thêm vào yêu thích
                        }
                    },
                    modifier = Modifier.padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                             if (isFavorite)
                                 Color(0xFFF2B8B5)
                            else
                                Color(0xFFC6A2FA)
                    ),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text =
                            if (isFavorite)
                                "Delete"
                            else
                                "Favorite",
                        color = Color.Black
                    )
                }
            }
        }

        // Row 2: Mô tả nội dung phim
        Text(
            text = "Nội dung:",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Text(
            text = movie.description,
            fontSize = 16.sp,
            textAlign = TextAlign.Justify,
            color = Color.DarkGray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Row 3: Danh sách các button tập phim
        Text(
            text = "Danh sách các tập:",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            when {
                episodeState.loading -> {
                    item {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }
                }
                episodeState.error != null -> {
                    item {
                        Text(
                            text = "Lỗi: ${episodeState.error}",
                            color = Color.Red,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                else -> {
                    val episodes = episodeState.list
                    items(episodes) { episode ->
                        // Tạo biến đếm để hiển thị số thứ tự của mỗi tập
                        val episodeIndex = episodes.indexOf(episode) + 1 // Thêm 1 để bắt đầu từ 1 thay vì 0
                        Button(
                            onClick = {
                                // Điều hướng đến màn hình WatchMovieScreen với movieId và episodeId
                                navController.navigate("ExoView/${movie.movieId}/${episode.episodeId}")
                            },
                            modifier = Modifier
                                .width(90.dp)
                                .height(40.dp),
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFC6A2FA)
                            )
                        ) {
                            Text(
                                text = ("Tập " + episodeIndex.toString()),  // Hiển thị số thứ tự của tập
                                fontSize = 14.sp,
                                color = Color.Black
                            )
                        }
                    }
                }

            }
        }

        Spacer(modifier = Modifier.height(16.dp)) // Thêm khoảng cách giữa phần tập phim và phần đánh giá
        MovieRating(movieViewModel = movieViewModel)
    }
}


//@Composable
//fun MovieDetailForm(
//    movie: Movie,
//    episodeViewModel: EpisodeViewModel,
//    navController: NavHostController
//) {
//    val episodeState = episodeViewModel.episodeState.value
//    var selectedEpisodeUrl by remember { mutableStateOf("") } // Biến để lưu URL của tập phim được chọn
//
//    // Khi movieId thay đổi, gọi API để lấy các tập phim
//    LaunchedEffect(movie.movieId) {
//        episodeViewModel.fetchAllEpisodesByMovieId(movie.movieId)
//    }
//
//    // Khi trạng thái của tập phim thay đổi, cập nhật URL video của tập phim
//    LaunchedEffect(episodeState.selectedEpisode) {
//        selectedEpisodeUrl = episodeState.selectedEpisode?.episodeUrl ?: ""
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//            .padding(top = 10.dp)
//            .background(Color(0xFFF2F2F2))
//    ) {
//        // Row 1: Ảnh phim và thông tin cơ bản
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 8.dp)
//        ) {
//            Image(
//                painter = painterResource(id = R.drawable.img), // Thay ảnh thích hợp
//                contentDescription = "Movie Image",
//                contentScale = ContentScale.Crop,
//                modifier = Modifier
//                    .width(120.dp)
//                    .height(180.dp)
//                    .clip(RoundedCornerShape(5.dp))
//            )
//
//            Column(
//                modifier = Modifier
//                    .padding(start = 16.dp)
//                    .align(Alignment.CenterVertically)
//            ) {
//                Text(
//                    text = movie.title,
//                    fontSize = 20.sp,
//                    fontWeight = FontWeight.Bold
//                )
//                // Các thông tin phim khác (giống như cũ)
//            }
//        }
//
//        // Row 2: Mô tả nội dung phim
//        Text(
//            text = "Nội dung:",
//            fontSize = 18.sp,
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier.padding(vertical = 8.dp)
//        )
//        Text(
//            text = movie.description,
//            fontSize = 16.sp,
//            textAlign = TextAlign.Justify,
//            color = Color.DarkGray,
//            modifier = Modifier.padding(bottom = 16.dp)
//        )
//
//        // Row 3: Danh sách các button tập phim
//        Text(
//            text = "Danh sách các tập:",
//            fontSize = 18.sp,
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier.padding(bottom = 8.dp)
//        )
//
//        LazyRow(
//            modifier = Modifier
//                .fillMaxWidth()
//                .wrapContentHeight()
//                .padding(4.dp),
//            horizontalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            when {
//                episodeState.loading -> {
//                    item {
//                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
//                    }
//                }
//                episodeState.error != null -> {
//                    item {
//                        Text(
//                            text = "Lỗi: ${episodeState.error}",
//                            color = Color.Red,
//                            modifier = Modifier.padding(16.dp)
//                        )
//                    }
//                }
//                else -> {
//                    // Lấy danh sách các tập phim từ state
//                    val episodes = episodeState.list
//                    items(episodes) { episode ->
//                        Button(
//                            onClick = {
//                                // Khi chọn tập, gọi hàm fetchEpisodeDetails và cập nhật URL video
//                                episodeViewModel.fetchEpisodeDetails(movie.movieId, episode.episodeId)
//                            },
//                            modifier = Modifier
//                                .width(90.dp)
//                                .height(40.dp),
//                            shape = RoundedCornerShape(4.dp),
//                            colors = ButtonDefaults.buttonColors(
//                                containerColor = Color(0xFF6200EE)
//                            )
//                        ) {
//                            Text(text = "Tập ${episode.episodeTitle}", fontSize = 14.sp)
//                        }
//                    }
//                }
//            }
//        }
//
//        // Row 4: Nếu có URL video, hiển thị ExoPlayer để xem tập phim
//        if (selectedEpisodeUrl.isNotEmpty()) {
//            Text(
//                text = "Đang phát: ${episodeState.selectedEpisode?.episodeTitle}",
//                fontSize = 18.sp,
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier.padding(vertical = 8.dp)
//            )
//            // Tạo ExoPlayer để phát video
//            ExoView(videoURL = selectedEpisodeUrl)
//        }
//    }
//}

//@Composable
//fun MovieDetailForm(
//    movie: Movie,
//    episodeViewModel: EpisodeViewModel,
//    navController: NavHostController
//) {
//    val episodeState = episodeViewModel.episodeState.value
//    var selectedEpisodeUrl by remember { mutableStateOf("") } // Biến để lưu URL của tập phim được chọn
//
//    // Khi movieId thay đổi, gọi API để lấy các tập phim
//    LaunchedEffect(movie.movieId) {
//        episodeViewModel.fetchAllEpisodesByMovieId(movie.movieId)
//    }
//
//    // Khi trạng thái của tập phim thay đổi, cập nhật URL video của tập phim
//    LaunchedEffect(episodeState.selectedEpisode) {
//        selectedEpisodeUrl = episodeState.selectedEpisode?.episodeUrl ?: ""
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//            .padding(top = 10.dp)
//            .background(Color(0xFFF2F2F2))
//    ) {
//        // Row 1: Ảnh phim và thông tin cơ bản
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 8.dp)
//        ) {
//            Image(
//                painter = painterResource(id = R.drawable.img), // Thay ảnh thích hợp
//                contentDescription = "Movie Image",
//                contentScale = ContentScale.Crop,
//                modifier = Modifier
//                    .width(120.dp)
//                    .height(180.dp)
//                    .clip(RoundedCornerShape(5.dp))
//            )
//
//            Column(
//                modifier = Modifier
//                    .padding(start = 16.dp)
//                    .align(Alignment.CenterVertically)
//            ) {
//                Text(
//                    text = movie.title,
//                    fontSize = 20.sp,
//                    fontWeight = FontWeight.Bold
//                )
//                // Các thông tin phim khác (giống như cũ)
//            }
//        }
//
//        // Row 2: Mô tả nội dung phim
//        Text(
//            text = "Nội dung:",
//            fontSize = 18.sp,
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier.padding(vertical = 8.dp)
//        )
//        Text(
//            text = movie.description,
//            fontSize = 16.sp,
//            textAlign = TextAlign.Justify,
//            color = Color.DarkGray,
//            modifier = Modifier.padding(bottom = 16.dp)
//        )
//
//        // Row 3: Danh sách các button tập phim
//        Text(
//            text = "Danh sách các tập:",
//            fontSize = 18.sp,
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier.padding(bottom = 8.dp)
//        )
//
//        LazyRow(
//            modifier = Modifier
//                .fillMaxWidth()
//                .wrapContentHeight()
//                .padding(4.dp),
//            horizontalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            when {
//                episodeState.loading -> {
//                    item {
//                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
//                    }
//                }
//                episodeState.error != null -> {
//                    item {
//                        Text(
//                            text = "Lỗi: ${episodeState.error}",
//                            color = Color.Red,
//                            modifier = Modifier.padding(16.dp)
//                        )
//                    }
//                }
//                else -> {
//                    // Lấy danh sách các tập phim từ state
//                    val episodes = episodeState.list
//                    items(episodes) { episode ->
//                        Button(
//                            onClick = {
//                                // Điều hướng đến màn hình xem phim khi chọn tập
//                                navController.navigate("WatchMovieScreen/${episode.episodeId}")
//                            },
//                            modifier = Modifier
//                                .width(90.dp)
//                                .height(40.dp),
//                            shape = RoundedCornerShape(4.dp),
//                            colors = ButtonDefaults.buttonColors(
//                                containerColor = Color(0xFF6200EE)
//                            )
//                        ) {
//                            Text(text = "Tập ${episode.episodeTitle}", fontSize = 14.sp)
//                        }
//                    }
//                }
//            }
//        }
//    }
//}

//@Composable
//fun MovieDetailForm(navController: NavHostController,episodeCount: Int) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//            .padding(top = 10.dp)
//            .background(Color(0xFFF2F2F2))
//    ) {
//        // Row 1: Ảnh phim và thông tin cơ bản
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 8.dp)
//        ) {
//            // Cột 1: Ảnh phim
//            Image(
//                painter = painterResource(id = R.drawable.img),
//                contentDescription = "Movie Image",
//                contentScale = ContentScale.Crop,
//                modifier = Modifier
//                    .width(120.dp)
//                    .height(180.dp)
//                    .clip(RoundedCornerShape(5.dp))
//            )
//            // Cột 2: Thông tin phim
//            Column(
//                modifier = Modifier
//                    .padding(start = 16.dp)
//                    .align(Alignment.CenterVertically)
//            ) {
//                Text(
//                    text = "The Dark Knight",
//                    fontSize = 20.sp,
//                    fontWeight = FontWeight.Bold
//                )
//                Text(
//                    text = "Tên tiếng việt: Kỵ sĩ bóng đêm",
//                    fontSize = 16.sp,
//                    color = Color.Gray
//                )
//                Text(
//                    text = "Đạo diễn: Christopher Nolan",
//                    fontSize = 16.sp,
//                    color = Color.Gray
//                )
//                Text(
//                    text = "imdb: 9.0",
//                    fontSize = 16.sp,
//                    color = Color.Gray
//                )
//                Text(
//                    text = "Thể loại: Hành động, viễn tưởng, hàn lâm, siêu anh hùng",
//                    fontSize = 16.sp,
//                    color = Color.Gray
//                )
//                Text(
//                    text = "Năm sản xuất: 2008",
//                    fontSize = 16.sp,
//                    color = Color.Gray
//                )
//            }
//        }
//
//        // Row 2: Mô tả nội dung phim
//        Text(
//            text = "Nội dung:",
//            fontSize = 18.sp,
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier.padding(vertical = 8.dp)
//        )
//        Text(
//            text = "Khi mối đe dọa được biết đến với cái tên Joker gây ra sự tàn phá và hỗn loạn đối với người dân Gotham, Batman, James Gordon và Harvey Dent phải hợp tác cùng nhau để chấm dứt sự điên loạn",
//            fontSize = 16.sp,
//            textAlign = TextAlign.Justify,
//            color = Color.DarkGray,
//            modifier = Modifier.padding(bottom = 16.dp)
//        )
//
//        // Row 3: Danh sách các button tập phim
//        Text(
//            text = "Danh sách các tập:",
//            fontSize = 18.sp,
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier.padding(bottom = 8.dp)
//        )
//        LazyRow(
//            modifier = Modifier
//                .fillMaxWidth()
//                .wrapContentHeight()
//                .padding(4.dp),
//            horizontalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            // Sử dụng vòng lặp for với LazyRow để thêm các nút tập phim
//            for (episode in 1..episodeCount) {
//                item {
//                    Button(
//                        onClick = {
//                            navController.navigate("WatchMovieScreen")
//                        },
//                        modifier = Modifier
//                            .width(90.dp) // Chiều rộng cố định cho nút
//                            .height(40.dp),
//                        shape = RoundedCornerShape(4.dp),
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = Color(0xFF6200EE) // Màu nền tím
//                        )
//                    ) {
//                        Text(text = "Tập $episode", fontSize = 14.sp)
//                    }
//                }
//            }
//        }
//    }
//}
