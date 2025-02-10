package com.example.thuctapcosochuyennganh.HomeMoviePage

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.thuctapcosochuyennganh.DataClass.Genre
import com.example.thuctapcosochuyennganh.DataClass.Movie
import com.example.thuctapcosochuyennganh.R
import com.example.thuctapcosochuyennganh.ViewModel.GenreViewModel

@Composable
fun FilterForm(
    genreViewModel: GenreViewModel,
    navController: NavHostController
) {
    val genreState = genreViewModel.genreState.value
    val moviesByGenreState = genreViewModel.moviesByGenreState.value

    LaunchedEffect(Unit) {
        genreViewModel.fetchAllGenres()
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Text(
            text = "Chọn Thể Loại",
            style = MaterialTheme.typography.h5,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (genreState.loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (genreState.error != null) {
            Text(
                text = "Lỗi: ${genreState.error}",
                color = Color.Red,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp) // Chiều cao cố định với cơ chế kéo dọc
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val chunkedGenres = genreState.list.chunked(7) // Chia danh sách thành nhóm 7 phần tử

                items(chunkedGenres) { genreRow ->
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(genreRow) { genre ->
                            GenreButton(
                                genre = genre,
                                isSelected = genre.genreName in genreState.selectedGenres,
                                onClick = { genreViewModel.toggleGenreSelection(genre.genreName) }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Danh Sách Phim",
            style = MaterialTheme.typography.h5,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (moviesByGenreState.loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (moviesByGenreState.error != null) {
            Text(
                text = "Lỗi: ${moviesByGenreState.error}",
                color = Color.Red,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else if (moviesByGenreState.movies.isEmpty()) {
            Text(
                text = "Không có phim nào phù hợp.",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(moviesByGenreState.movies) { movie ->
                    MovieGenreItem(movie = movie, navController = navController)
                }
            }
        }
    }
}

@Composable
fun GenreButton(
    genre: Genre,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = if (isSelected) {
            ButtonDefaults.buttonColors(Color(0xFFF2B8B5))
        } else {
            ButtonDefaults.buttonColors(Color(0xFFC6A2FA))
        },
        modifier = Modifier
                .width(125.dp)
                .height(40.dp),
        shape = RoundedCornerShape(4.dp),
    ) {
        Text(
            text = genre.genreName,
            color = Color.Black
            )
    }
}


@Composable
fun MovieGenreItem(movie: Movie, navController: NavController){
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


