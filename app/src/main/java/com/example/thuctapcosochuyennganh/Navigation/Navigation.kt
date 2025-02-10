package com.example.thuctapcosochuyennganh.Navigation


import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.thuctapcosochuyennganh.AppBar.AppBar
import com.example.thuctapcosochuyennganh.AppBar.DialogSearch
import com.example.thuctapcosochuyennganh.AppBar.SplashScreen
import com.example.thuctapcosochuyennganh.ChatScreen.ChatApp
import com.example.thuctapcosochuyennganh.DataClass.Movie
import com.example.thuctapcosochuyennganh.DataClass.User
import com.example.thuctapcosochuyennganh.DrawerScreen.ChangedPassWordForm
import com.example.thuctapcosochuyennganh.DrawerScreen.MyListForm
import com.example.thuctapcosochuyennganh.DrawerScreen.ProfileForm
import com.example.thuctapcosochuyennganh.HomeMoviePage.FilterForm
import com.example.thuctapcosochuyennganh.HomeMoviePage.LibraryForm
import com.example.thuctapcosochuyennganh.HomeMoviePage.RecommendForm
import com.example.thuctapcosochuyennganh.LoginAndRegister.LoginForm
import com.example.thuctapcosochuyennganh.LoginAndRegister.RegisterForm
import com.example.thuctapcosochuyennganh.MovieScreen.ExoView
import com.example.thuctapcosochuyennganh.MovieScreen.MovieDetailForm
import com.example.thuctapcosochuyennganh.ViewModel.AuthViewModel
import com.example.thuctapcosochuyennganh.ViewModel.CommentViewModel
import com.example.thuctapcosochuyennganh.ViewModel.EpisodeViewModel
import com.example.thuctapcosochuyennganh.ViewModel.FavoriteMovieViewModel
import com.example.thuctapcosochuyennganh.ViewModel.GenreViewModel
import com.example.thuctapcosochuyennganh.ViewModel.MovieViewModel
import com.example.thuctapcosochuyennganh.ViewModel.PersonalViewModel
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun Navigation(
    navController: NavHostController,
    uriState: MutableStateFlow<String>,
    imagePicker: ActivityResultLauncher<PickVisualMediaRequest>
) {
    val movieViewModel: MovieViewModel = viewModel()
    val episodeViewModel: EpisodeViewModel = viewModel()
    val genreViewModel: GenreViewModel = viewModel()
    val favoriteMovieViewModel: FavoriteMovieViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val commentViewModel: CommentViewModel = viewModel()
    val personalViewModel: PersonalViewModel = viewModel()
    // Kiểm tra trạng thái của MovieViewModel và EpisodeViewModel để cập nhật giao diện
    val movieState = movieViewModel.movieState.value
    val episodeState = episodeViewModel.episodeState.value

    NavHost(navController = navController, startDestination = "SplashScreen") {
        composable("SplashScreen") {
            SplashScreen {
                navController.navigate("LoginForm") {
                    popUpTo("SplashScreen") { inclusive = true }
                }
            }
        }
        composable("LoginForm") {
            LoginForm(navController,authViewModel)
        }
        composable("RegisterForm") {
            RegisterForm(navController,authViewModel)
        }
        composable("AppBar") {
            AppBar(
                navController = navController,
                movieViewModel = movieViewModel,
                genreViewModel = genreViewModel,
                authViewModel = authViewModel,
                personalViewModel = personalViewModel,
                favoriteMovieViewModel = favoriteMovieViewModel,
                uriState = uriState,
                imagePicker = imagePicker
            )
        }
        composable("RecommendScreen") {
            RecommendForm(navController = navController, movieViewModel = movieViewModel)
        }
        composable("ProfileScreen") {
            ProfileForm(personalViewModel = personalViewModel)
        }
        composable("FilterScreen") {
            FilterForm(genreViewModel = genreViewModel, navController = navController)
        }
        composable("LibraryScreen") {
            LibraryForm(navController = navController, movieViewModel = movieViewModel)
        }
        composable(
            "MovieDetailForm/{movieId}",
            arguments = listOf(navArgument("movieId") { type = NavType.StringType })
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId") ?: ""

            val detailState = movieViewModel.detailState.value

            LaunchedEffect(movieId) {
                movieViewModel.fetchMovieById(movieId)
            }

            when {
                detailState.loading -> {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }
                detailState.movie != null -> {
                    MovieDetailForm(
                        movie = detailState.movie,
                        episodeViewModel = episodeViewModel,
                        movieViewModel = movieViewModel,
                        navController = navController,
                        favoriteMovieViewModel = favoriteMovieViewModel
                    )
                }
                else -> {
                    // Nếu không tìm thấy phim hoặc có lỗi, hiển thị thông báo lỗi
                    Text(
                        text = detailState.error ?: "Không tìm thấy phim.",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
        composable(
            "ExoView/{movieId}/{episodeId}",
            arguments = listOf(
                navArgument("movieId") { type = NavType.StringType },
                navArgument("episodeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId") ?: ""
            val episodeId = backStackEntry.arguments?.getString("episodeId") ?: ""

            // Gọi ExoView
            ExoView(
                movieId = movieId,
                episodeId = episodeId,
                episodeViewModel = episodeViewModel,
                commentViewModel = commentViewModel
            )
        }

        composable("ChangePassWordForm") {
            ChangedPassWordForm(navController = navController, personalViewModel = personalViewModel, authViewModel = authViewModel)
        }
        composable("MyListForm") {
            MyListForm(navController = navController, favoriteMovieViewModel = favoriteMovieViewModel, authViewModel = authViewModel, personalViewModel = personalViewModel)
        }
        composable("AIChatBox"){
            ChatApp(uriState, imagePicker)
        }
    }
}


