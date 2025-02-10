package com.example.thuctapcosochuyennganh.HomeMoviePage

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.thuctapcosochuyennganh.API.MovieAPIService
import com.example.thuctapcosochuyennganh.DataClass.Movie
import com.example.thuctapcosochuyennganh.R
import com.example.thuctapcosochuyennganh.ViewModel.MovieViewModel
import kotlinx.coroutines.launch


@Composable
fun LibraryForm(navController: NavController, movieViewModel: MovieViewModel) {
    // Gọi fetchAllMovie khi màn hình được hiển thị và mỗi khi state thay đổi
    LaunchedEffect(movieViewModel.movieState.value.moviePage?.pageNumber) {
        val currentPage = movieViewModel.movieState.value.moviePage?.pageNumber ?: 0
        movieViewModel.fetchAllMovie(currentPage) // Tải phim cho trang hiện tại
    }

    val movieState by movieViewModel.movieState

    // Kiểm tra trạng thái loading, error, hoặc dữ liệu
    when {
        movieState.loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        movieState.error != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Error: ${movieState.error}", color = Color.Red)
            }
        }
        else -> {
            movieState.moviePage?.let { moviePage ->
                Column(modifier = Modifier.fillMaxSize()) {
                    // Kiểm tra và hiển thị phân trang
                    if (moviePage.totalPages > 1) {
                        PaginationControls(
                            currentPage = moviePage.pageNumber,
                            totalPages = moviePage.totalPages,
                            onPageSelected = { movieViewModel.onPageSelected(it) }, // Chọn trang mới
                            onNextPage = { movieViewModel.nextPage() },
                            onPreviousPage = { movieViewModel.previousPage() }
                        )
                    }
                    // Hiển thị danh sách phim
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2), // Chia làm 2 cột
                        modifier = Modifier.padding(8.dp) // Thêm khoảng cách ngoài
                    ) {
                        items(moviePage.movieList) { movie ->
                            BrowserItemLibrary(
                                movie = movie,
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun PaginationControls(
    currentPage: Int,
    totalPages: Int,
    onPageSelected: (Int) -> Unit,
    onNextPage: () -> Unit,
    onPreviousPage: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        // Nút lùi
        if (currentPage > 0) { // currentPage là 0-based, nên nút lùi chỉ hiển thị nếu currentPage > 0
            IconButton(onClick = onPreviousPage) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Previous Page",
                    tint = Color.Black
                )
            }
        }

        // Dãy số trang với tối đa 6 nút
        val pagesToShow = mutableListOf<Int>()

        // Luôn luôn hiển thị trang đầu tiên
        pagesToShow.add(0) // Trang đầu tiên là 0 (logic)

        // Trang ở giữa
        if (currentPage <= 2) {
            // Hiển thị từ trang đầu đến tối đa 5 trang
            pagesToShow.addAll(1 until minOf(6, totalPages))
            if (totalPages > 6) pagesToShow.add(-1) // Thêm dấu "..." nếu còn nhiều trang
        } else if (currentPage in 3 until (totalPages - 3)) {
            pagesToShow.add(currentPage - 1)
            pagesToShow.add(currentPage)
            pagesToShow.add(currentPage + 1)
            pagesToShow.add(-1) // Thêm dấu "..."
            pagesToShow.add(totalPages - 1) // Trang cuối cùng
        } else {
            // Hiển thị các trang cuối
            pagesToShow.addAll(maxOf(0, totalPages - 5) until totalPages)
        }

        // Hiển thị các trang
        pagesToShow.forEach { page ->
            if (page == -1) {
                Text(text = "...", modifier = Modifier.padding(horizontal = 4.dp))
            } else {
                val isCurrentPage = page == currentPage
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(RoundedCornerShape(8.dp)) // Bo góc của ô vuông
                        .background(if (isCurrentPage) Color(0xFFF2B8B5) else Color(0xFFC6A2FA)) // Màu đỏ khi chọn, màu tím khi chưa chọn
                        .clickable { if (!isCurrentPage) onPageSelected(page) }
                        .size(32.dp), // Giảm kích thước của ô vuông
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (page + 1).toString(), // Hiển thị số trang tăng thêm 1
                        color = Color.Black,
                        style = MaterialTheme.typography.h6// Giảm kích thước chữ
                    )
                }
            }
        }

        // Nút tiến
        if (currentPage < totalPages - 1) { // currentPage là 0-based, nên nút tiến chỉ hiển thị nếu còn trang tiếp theo
            IconButton(onClick = onNextPage) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Next Page",
                    tint = Color.Black
                )
            }
        }
    }
}



@Composable
fun BrowserItemLibrary(movie: Movie, navController: NavController) {
    Card(
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                navController.navigate("MovieDetailForm/${movie.movieId}") // truyền movie id
            },
        border = BorderStroke(3.dp, color = Color.DarkGray)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
//                painter = painterResource(id = R.drawable.img),
                painter = rememberImagePainter(data = movie.image), // Load ảnh từ URL chính xác
                contentDescription = movie.title,
                modifier = Modifier
                    .fillMaxWidth() // Lấy toàn bộ chiều rộng của Card
                    .height(300.dp), // Chiều cao ảnh tùy chỉnh, có thể thay đổi
                contentScale = ContentScale.Crop // Cắt ảnh để vừa khung
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = movie.title, // Sử dụng `movie.name` cho đúng
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body1,
                maxLines = 2, // Hiển thị tối đa 1 dòng
            )
        }
    }
}


//@Composable
//fun LibraryForm(navController: NavController, movieViewModel: MovieViewModel) {
//    // Gọi fetchAllMovie khi màn hình được hiển thị
//    LaunchedEffect(Unit) {
//        movieViewModel.fetchAllMovie() // Tải danh sách phim khi bắt đầu
//    }
//
//    val movieState by movieViewModel.movieState
//
//    // Kiểm tra trạng thái loading, error, hoặc dữ liệu
//    when {
//        movieState.loading -> {
//            Box(
//                modifier = Modifier.fillMaxSize(),
//                contentAlignment = Alignment.Center
//            ) {
//                CircularProgressIndicator()
//            }
//        }
//        movieState.error != null -> {
//            Box(
//                modifier = Modifier.fillMaxSize(),
//                contentAlignment = Alignment.Center
//            ) {
//                Text(text = "Error: ${movieState.error}", color = Color.Red)
//            }
//        }
//        else -> {
//            LazyVerticalGrid(
//                columns = GridCells.Fixed(2), // Chia làm 2 cột
//                modifier = Modifier.padding(8.dp) // Thêm khoảng cách ngoài
//            ) {
//                items(movieState.list) { movie ->
//                    BrowserItemLibrary(
//                        movie = movie,
//                        navController = navController
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun BrowserItemLibrary(movie: Movie, navController: NavController) {
//    Card(
//        modifier = Modifier
//            .padding(8.dp)
//            .fillMaxWidth()
//            .clickable {
//                navController.navigate("MovieDetailForm/${movie.movieId}")
//            },
//        border = BorderStroke(3.dp, color = Color.DarkGray)
//    ) {
//        Column(
//            modifier = Modifier.fillMaxSize(),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            Image(
//                painter = rememberImagePainter(data = movie.title), // Load ảnh từ URL
//                contentDescription = movie.title,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(150.dp)
//                    .clip(RoundedCornerShape(8.dp)),
//                contentScale = ContentScale.Crop // Cắt ảnh để vừa khung
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(
//                text = movie.title,
//                modifier = Modifier.fillMaxWidth(),
//                textAlign = TextAlign.Center,
//                style = MaterialTheme.typography.body1,
//                maxLines = 1, // Hiển thị tối đa 1 dòng
//            )
//        }
//    }
//}




//fun LibraryForm(navController: NavController, movieService: MovieAPIService){
//    var coroutineScope = rememberCoroutineScope()
//    var isLoading by remember {
//        mutableStateOf(true)
//    }
//    var movies by remember { mutableStateOf<List<Movie>>(emptyList()) }
//    LaunchedEffect(Unit) {
//        coroutineScope.launch {
//            isLoading = true
//            movies = try {
//                movieService.getAllMovie().movies
//            } catch (e: Exception) {
//                emptyList()
//            }
//            isLoading = false
//        }
//    }
//    if (isLoading) {
//        Box(
//            modifier = Modifier.fillMaxSize(),
//            contentAlignment = Alignment.Center // Căn giữa nội dung bên trong Box
//        ) {
//            CircularProgressIndicator()
//        }
//    } else {
//        LazyVerticalGrid(
//            columns = GridCells.Fixed(2),
//            contentPadding = PaddingValues(8.dp)
//        ) {
//            items(movies) { movie ->
//                BrowserItemLibrary(movie = movie, navController = navController)
//            }
//        }
//    }



//@Composable
//fun BrowserItemLibrary(movie: Movie, navController: NavController) {
//    Card(
//        modifier = Modifier
//            .padding(8.dp)
//            .border(BorderStroke(3.dp, color = Color.DarkGray))
//            .clickable {
//                navController.navigate("MovieDetailForm")
//            }
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(8.dp),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            Image(
//                painter = rememberImagePainter(data = movie.movieImgUrl),
//                contentDescription = "ảnh phim",
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .fillMaxHeight()
//                    .clip(RoundedCornerShape(0.dp))
//                    .clickable {
//                        navController.navigate("MovieDetailForm")
//                    }
//            )
//            Spacer(modifier = Modifier.height(4.dp))
//            Text(
//                text = movie.name,
//                modifier = Modifier.fillMaxWidth(),
//                textAlign = TextAlign.Center,
//                style = MaterialTheme.typography.body1
//            )
//        }
//    }
//}


