package com.example.thuctapcosochuyennganh.AppBar

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.thuctapcosochuyennganh.ChatScreen.ChatApp
import com.example.thuctapcosochuyennganh.DrawerScreen.ChangedPassWordForm
import com.example.thuctapcosochuyennganh.DrawerScreen.MyListForm
import com.example.thuctapcosochuyennganh.DrawerScreen.ProfileForm
import com.example.thuctapcosochuyennganh.HomeMoviePage.FilterForm
import com.example.thuctapcosochuyennganh.HomeMoviePage.LibraryForm
import com.example.thuctapcosochuyennganh.HomeMoviePage.RecommendForm
import com.example.thuctapcosochuyennganh.LoginAndRegister.LoginForm
import com.example.thuctapcosochuyennganh.LoginAndRegister.RegisterForm
import com.example.thuctapcosochuyennganh.R
import com.example.thuctapcosochuyennganh.ViewModel.AuthViewModel
import com.example.thuctapcosochuyennganh.ViewModel.FavoriteMovieViewModel
import com.example.thuctapcosochuyennganh.ViewModel.GenreViewModel
import com.example.thuctapcosochuyennganh.ViewModel.MovieViewModel
import com.example.thuctapcosochuyennganh.ViewModel.PersonalViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


@Composable
fun AppBar(
    navController: NavHostController,
    movieViewModel: MovieViewModel,
    genreViewModel: GenreViewModel,
    authViewModel: AuthViewModel,
    personalViewModel: PersonalViewModel,
    favoriteMovieViewModel: FavoriteMovieViewModel,
    uriState: MutableStateFlow<String>,
    imagePicker: ActivityResultLauncher<PickVisualMediaRequest>
) {
    var selectedBottomTabIndex by remember { mutableStateOf(0) }
    var selectedDrawerTabIndex by remember { mutableStateOf(-1) } // Tab cho Drawer
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                authViewModel = authViewModel,
                onItemClick = { item ->
                    selectedDrawerTabIndex = item // Cập nhật chỉ số tab khi chọn mục từ Drawer
                    selectedBottomTabIndex = -1
                    scope.launch { drawerState.close() } // Đóng drawer
            },
                onLogoutClick = {
                    // Đưa người dùng về màn hình đăng nhập
                    navController.navigate("LoginForm") // Thay đổi "login" thành route đăng nhập của bạn
                    scope.launch { drawerState.close() } // Đóng drawer
                })
        }
    ) {
        androidx.compose.material3.Scaffold(
            topBar = {
                TopBar(
                    navController = navController,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    movieViewModel = movieViewModel
                )
            },

            bottomBar = {
                BottomBar(onTabSelected = {
                    selectedBottomTabIndex = it
                    selectedDrawerTabIndex = -1
                })
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Nội dung dựa trên tab đã chọn
                when (selectedBottomTabIndex) {
                    0 -> LibraryForm(navController = navController, movieViewModel = movieViewModel)
                    1 -> RecommendForm(navController = navController, movieViewModel = movieViewModel)
                    2 -> FilterForm(genreViewModel = genreViewModel, navController = navController)
                }
                Spacer(modifier = Modifier.weight(1f))
                when (selectedDrawerTabIndex) {
                    0 -> MyListForm(navController = navController, favoriteMovieViewModel = favoriteMovieViewModel, authViewModel = authViewModel, personalViewModel = personalViewModel)// Nội dung tương ứng cho Drawer
                    1 -> ProfileForm(personalViewModel = personalViewModel)
                    2 -> ChangedPassWordForm(navController = navController, personalViewModel = personalViewModel, authViewModel = authViewModel)
                    3 -> ChatApp(uriState, imagePicker)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavHostController, onMenuClick: () -> Unit, movieViewModel: MovieViewModel) {

    val dialogOpen = remember { mutableStateOf(false) }

    androidx.compose.material3.TopAppBar(
        title = { androidx.compose.material3.Text("Nhóm 15") },
        modifier = Modifier.background(Color(0xFFC6A2FA)),
        navigationIcon = {
            IconButton(onClick = { onMenuClick() }) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
        },
        actions = {
            androidx.compose.material3.IconButton(onClick = {
                dialogOpen.value = true
            }) {
                androidx.compose.material3.Icon(
                    Icons.Default.Search,
                    contentDescription = "Tìm kiếm",
                )
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFFC6A2FA))
    )
    DialogSearch(
        dialogOpen = dialogOpen,
        navController = navController,
        movieViewModel = movieViewModel)
}

@Composable
fun DialogSearch(
    dialogOpen: MutableState<Boolean>,
    navController: NavController,
    movieViewModel: MovieViewModel
) {
    var query by remember { mutableStateOf("") }
    val searchState = movieViewModel.searchState.value
    val debounceTime = 500L // Thời gian debounce (500ms)

    // Trì hoãn việc gọi API cho đến khi người dùng dừng gõ
    LaunchedEffect(query) {
        if (query.isNotEmpty()) {
            delay(debounceTime) // Trì hoãn việc gọi API
            movieViewModel.searchMoviesByName(query) // Gọi API tìm kiếm phim theo query
        }
    }

    if (dialogOpen.value) {
        // Hiển thị AlertDialog
        AlertDialog(
            onDismissRequest = {
                // Đóng dialog khi nhấn ngoài hoặc nhấn nút quay lại
                dialogOpen.value = false
            },
            title = {
                Row {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_search_24),
                        contentDescription = "Tìm kiếm",
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tìm kiếm phim")
                }
            },
            text = {
                Column {
                    // Input field để người dùng nhập từ khóa tìm kiếm
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        label = { Text("Nhập tên phim") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Hiển thị gợi ý khi có từ khóa
                    when {
                        searchState.loading -> {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                        }
                        searchState.error != null -> {
                            Text(
                                text = searchState.error,
                                color = Color.Red,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        query.isNotEmpty() && searchState.searchResults.isNotEmpty() -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(searchState.searchResults) { movie ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                // Khi chọn một bộ phim từ gợi ý, chuyển hướng đến trang chi tiết phim
                                                navController.navigate("MovieDetailForm/${movie.movieId}")
                                            }
                                            .padding(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Hiển thị hình ảnh phim
                                        Image(
//                                            painter = painterResource(id = R.drawable.img),
                                             painter = rememberImagePainter(data = movie.image), // Load ảnh từ URL chính xác
                                            contentDescription = movie.title,
                                            modifier = Modifier
                                                .width(80.dp) // Lấy toàn bộ chiều rộng của Card
                                                .height(120.dp), // Chiều cao ảnh tùy chỉnh, có thể thay đổi
                                            contentScale = ContentScale.Crop // Cắt ảnh để vừa khung
                                        )

                                        // Hiển thị tiêu đề phim
                                        Column(
                                            modifier = Modifier
                                                .padding(start = 16.dp)
                                                .align(Alignment.CenterVertically)
                                        ) {
                                            Text(
                                                modifier = Modifier.padding(top = 3.dp, end = 3.dp),
                                                text = movie.title,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                    // Đường kẻ chia cách giữa các mục
                                    Divider(color = Color.Gray, thickness = 1.5.dp, modifier = Modifier.padding(vertical = 0.dp))
                                }
                            }
                        }
                        query.isNotEmpty() && searchState.searchResults.isEmpty() -> {
                            Text(
                                text = "Không tìm thấy phim phù hợp.",
                                modifier = Modifier.padding(8.dp),
                            )
                        }
                        query.isEmpty() -> {
                            Text(
                                text = "Không có gợi ý.",
                                modifier = Modifier.padding(8.dp),
                            )
                        }
                    }
                }
            },
            properties = DialogProperties(
                dismissOnBackPress = true, // Đóng dialog khi nhấn nút quay lại
                dismissOnClickOutside = true // Đóng dialog khi nhấn ngoài
            ),
            buttons = {
                TextButton(onClick = { dialogOpen.value = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}



@Composable
fun BottomBar(onTabSelected: (Int) -> Unit) {
    BottomNavigation(
        modifier = Modifier.padding(bottom = 40.dp),
        backgroundColor = Color(0xFFC6A2FA)
    ) {
        BottomNavigationItem(
            icon = {
                androidx.compose.material3.Icon(
                    Icons.Default.Home,
                    contentDescription = "Trang chủ"
                )
            },
            label = { androidx.compose.material3.Text("Trang chủ") },
            selected = false,
            onClick = { onTabSelected(0) }
        )
        BottomNavigationItem(
            icon = {
                androidx.compose.material3.Icon(
                    painter = painterResource(id = R.drawable.baseline_local_movies_24),
                    contentDescription = "Đề xuất"
                )
            },
            label = { androidx.compose.material3.Text("Đề xuất") },
            selected = false,
            onClick = { onTabSelected(1) }
        )
        BottomNavigationItem(
            icon = {
                androidx.compose.material3.Icon(
                    painter = painterResource(id = R.drawable.baseline_filter_list_alt_24),
                    contentDescription = "Bộ Lọc"
                )
            },
            label = { androidx.compose.material3.Text("Bộ Lọc") },
            selected = false,
            onClick = { onTabSelected(2) }
        )
    }
}

@Composable
fun DrawerContent(onItemClick: (Int) -> Unit,onLogoutClick: () -> Unit, authViewModel: AuthViewModel) {
    val authState by authViewModel.authState // Theo dõi trạng thái AuthState
    Column {
        Spacer(modifier = Modifier.padding(20.dp))
        TextButton(onClick = { onItemClick(0) }) {
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp),
                ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favorite",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
                Text(text = "   Danh sách phim yêu thích",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    style = MaterialTheme.typography.body1.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black)
                )
            }
        }
        TextButton(onClick = { onItemClick(1) }) {
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
                Text(text = "   Profile",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                         style = MaterialTheme.typography.body1.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black)
                )
            }
        }
        TextButton(onClick = { onItemClick(2) }) {
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Đổi mật khẩu",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
                Text(text = "   Đổi mật khẩu",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    style = MaterialTheme.typography.body1.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black)
                )
            }
        }
        TextButton(onClick = { onItemClick(3) }) {
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_comment_24), contentDescription = "AI",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
                Text(text = "   Hỏi đáp về phim - AI",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    style = MaterialTheme.typography.body1.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black)
                )
            }
        }
        TextButton(onClick = {
            authViewModel.signOut()
            onLogoutClick()
        }) {
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Đăng xuất",
                    tint = Color.Red,
                    modifier = Modifier.size(24.dp)
                )
                Text(text = "   Đăng xuất",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    style = MaterialTheme.typography.body1.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Red)
                )
            }
        }
    }
}







//@Composable
//fun DialogSearch(
//    dialogOpen: MutableState<Boolean>,
//    onSearch: (String) -> Unit
//){
//    if(dialogOpen.value){
//        androidx.compose.material3.AlertDialog(
//            onDismissRequest = {
//                dialogOpen.value = false
//            },
//            confirmButton = {
//                androidx.compose.material3.TextButton(onClick = {
//                    dialogOpen.value = false
//
//                }) {
//                    androidx.compose.material3.Text(text = "Tìm kiếm")
//                }
//            },
//            title = {
//                Row {
//                    androidx.compose.material3.Icon(
//                        painter = painterResource(id = R.drawable.baseline_search_24),
//                        contentDescription = "Tìm kiếm"
//                    )
//                    androidx.compose.material3.Text("Tìm kiếm phim")
//                }
//            },
//            text = {
//                Column {
//                    androidx.compose.material3.OutlinedTextField(
//                        value = "",
//                        onValueChange = {
//
//                        },
//                        label = { androidx.compose.material3.Text("Nhập tên phim") },
//                        modifier = Modifier.fillMaxWidth(),
//                    )
//                }
//            },
//            properties = DialogProperties(
//                dismissOnBackPress = true,
//                dismissOnClickOutside = true
//            )
//        )
//    }
//}

