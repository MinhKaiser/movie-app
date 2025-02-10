package com.example.thuctapcosochuyennganh

import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.thuctapcosochuyennganh.API.LoginAPIService
import com.example.thuctapcosochuyennganh.ChatScreen.ChatApp
import com.example.thuctapcosochuyennganh.LoginAndRegister.LoginForm
import com.example.thuctapcosochuyennganh.Navigation.Navigation
import com.example.thuctapcosochuyennganh.ViewModel.AuthViewModel
import com.example.thuctapcosochuyennganh.ViewModel.AuthViewModelFactory
import com.example.thuctapcosochuyennganh.ViewModel.CommentViewModel
import com.example.thuctapcosochuyennganh.ViewModel.CommentViewModelFactory
import com.example.thuctapcosochuyennganh.ViewModel.EpisodeViewModel
import com.example.thuctapcosochuyennganh.ViewModel.EpisodeViewModelFactory
import com.example.thuctapcosochuyennganh.ViewModel.FavoriteMovieViewModel
import com.example.thuctapcosochuyennganh.ViewModel.FavoriteMovieViewModelFactory
import com.example.thuctapcosochuyennganh.ViewModel.GenreViewModel
import com.example.thuctapcosochuyennganh.ViewModel.GenreViewModelFactory
import com.example.thuctapcosochuyennganh.ViewModel.MovieViewModel
import com.example.thuctapcosochuyennganh.ViewModel.MovieViewModelFactory
import com.example.thuctapcosochuyennganh.ViewModel.PersonalViewModel
import com.example.thuctapcosochuyennganh.ViewModel.PersonalViewModelFactory
import com.example.thuctapcosochuyennganh.ui.theme.ThucTapCoSoChuyenNganhTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    private lateinit var authViewModel: AuthViewModel
    private lateinit var movieViewModel: MovieViewModel
    private lateinit var episodeViewModel: EpisodeViewModel
    private lateinit var favoriteMovieViewModel: FavoriteMovieViewModel
    private lateinit var genreViewModel: GenreViewModel
    private lateinit var commentViewModel: CommentViewModel
    private lateinit var personalViewModel: PersonalViewModel

    private val uriState = MutableStateFlow("")

    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            uri?.let {
                uriState.update { uri.toString() } // Xử lý khi Uri không null
            } ?: run {
                uriState.update { "" } // Xử lý trường hợp Uri là null
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Đảm bảo gọi super.onCreate trước

        // Khởi tạo Retrofit và AuthAPIService
        val retrofit = Retrofit.Builder()
            .baseUrl("https://movie.nxqdev.io.vn/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val authService = retrofit.create(LoginAPIService.AuthAPIService::class.java)

        // Tạo AuthViewModel thông qua factory
        val authViewModelFactory = AuthViewModelFactory(authService)
        authViewModel = ViewModelProvider(this, authViewModelFactory).get(AuthViewModel::class.java)

        // Tạo MovieViewModel thông qua MovieViewModelFactory
        val movieViewModelFactory = MovieViewModelFactory(authViewModel)
        movieViewModel = ViewModelProvider(this, movieViewModelFactory).get(MovieViewModel::class.java)

        // Tạo EpisodeViewModel thông qua EpisodeViewModelFactory
        val episodeViewModelFactory = EpisodeViewModelFactory(authViewModel)
        episodeViewModel = ViewModelProvider(this, episodeViewModelFactory).get(EpisodeViewModel::class.java)

        // Tạo FavoriteMovieViewModel thông qua FavoriteMovieViewModelFactory
        val favoriteMovieViewModelFactory = FavoriteMovieViewModelFactory(authViewModel)
        favoriteMovieViewModel = ViewModelProvider(this, favoriteMovieViewModelFactory).get(FavoriteMovieViewModel::class.java)

        // Tạo GenreViewModel thông qua GenreViewModelFactory
        val genreViewModelFactory = GenreViewModelFactory(authViewModel)
        genreViewModel = ViewModelProvider(this, genreViewModelFactory).get(GenreViewModel::class.java)

        // Tạo CommentViewModel thông qua CommentViewModelFactory
        val commentViewModelFactory = CommentViewModelFactory(authViewModel)
        commentViewModel = ViewModelProvider(this,commentViewModelFactory).get(CommentViewModel::class.java)

        // Tạo CommentViewModel thông qua CommentViewModelFactory
        val personalViewModelFactory = PersonalViewModelFactory(authViewModel)
        personalViewModel = ViewModelProvider(this,personalViewModelFactory).get(PersonalViewModel::class.java)

        // Tiếp tục thiết lập UI
        enableEdgeToEdge()
        setContent {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            val navController = rememberNavController()
            ThucTapCoSoChuyenNganhTheme {
                Navigation(navController = navController, uriState = uriState, imagePicker = imagePicker)
                authViewModel.logFCMToken()
            }
        }
    }
}
