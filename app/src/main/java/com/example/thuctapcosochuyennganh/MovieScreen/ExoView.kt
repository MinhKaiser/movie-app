package com.example.thuctapcosochuyennganh.MovieScreen

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.VideoView
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.thuctapcosochuyennganh.DataClass.Comment
import com.example.thuctapcosochuyennganh.R
import com.example.thuctapcosochuyennganh.ViewModel.CommentViewModel
import com.example.thuctapcosochuyennganh.ViewModel.EpisodeViewModel
import kotlinx.coroutines.delay
import java.time.format.DateTimeFormatter


@Composable
fun ExoView(
    movieId: String,
    episodeId: String,
    episodeViewModel: EpisodeViewModel,
    commentViewModel: CommentViewModel
) {
    val context = LocalContext.current

    // Tạo ExoPlayer và giữ trạng thái qua remember
    val player = remember(context) {
        ExoPlayer.Builder(context).build()
    }

    // Giữ PlayerView qua remember
    val playerView = remember {
        PlayerView(context)
    }

    // Lấy thông tin tập phim từ ViewModel
    val episodeState = episodeViewModel.episodeState.value

    // Tải thông tin tập phim nếu cần
    LaunchedEffect(movieId, episodeId) {
        episodeViewModel.fetchEpisodeDetails(movieId, episodeId)
    }

    // Lấy SharedPreferences để lưu tiến độ phim
    val sharedPreferences = context.getSharedPreferences("movie_progress", Context.MODE_PRIVATE)
    val savedProgressKey = "progress_${movieId}_$episodeId"

    // Lấy tiến độ đã lưu
    var savedProgress by remember {
        mutableStateOf(sharedPreferences.getLong(savedProgressKey, 0L))
    }

    // Trạng thái hiển thị Dialog
    var showResumeDialog by remember { mutableStateOf(savedProgress > 0) }

    // Thiết lập MediaItem cho ExoPlayer
    LaunchedEffect(episodeState.selectedEpisode?.url) {
        episodeState.selectedEpisode?.url?.let { url ->
            val mediaItem = MediaItem.fromUri(url)
            player.setMediaItem(mediaItem)
            player.prepare()
            // Nếu có tiến độ đã lưu, tua đến vị trí đó
            if (savedProgress > 0) {
                player.seekTo(savedProgress)
            }
        }
    }

    // Trạng thái fullscreen
    var isFullScreen by rememberSaveable { mutableStateOf(false) }

    // Âm lượng
    var volumeLevel by remember { mutableStateOf(1.0f) } // Giá trị mặc định là âm lượng tối đa
    var showVolumeSlider by remember { mutableStateOf(false) } // Trạng thái hiển thị thanh trượt âm lượng

    var showMenu by remember { mutableStateOf(false) } // Trạng thái hiển thị menu



    // Chỉnh âm lượng
    val updateVolume: (Float) -> Unit = { volume ->
        volumeLevel = volume
        player.volume = volume
    }

    // Modifier thay đổi kích thước video khi fullscreen
    val videoModifier = if (isFullScreen) {
        Modifier
            .fillMaxSize()
            .background(Color.Black)
    } else {
        Modifier
            .fillMaxWidth()
            .aspectRatio(16 / 9f)
    }

    // Phóng to/thu nhỏ màn hình
    val toggleFullScreen: () -> Unit = {
        isFullScreen = true
        // Quay lại chế độ portrait
        val activity = context as? Activity
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    // Chuyển đổi normalscreen
    val toggleNormalSize: () -> Unit = {
        isFullScreen = false
        // Quay lại chế độ portrait
        val activity = context as? Activity
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    // Ẩn thanh trượt sau 3 giây nếu không có sự tương tác
    val volumeSliderState = rememberUpdatedState(showVolumeSlider)
    LaunchedEffect(volumeSliderState.value) {
        if (volumeSliderState.value) {
            delay(8000) // Đợi 8 giây
            if (volumeSliderState.value) {
                showVolumeSlider = false
            }
        }
    }

    // Lắng nghe sự kiện nút quay lại (back press) với BackHandler
    BackHandler(enabled = isFullScreen) {
        // Nếu đang ở chế độ full-screen, quay về chế độ màn hình nhỏ
        if (isFullScreen) {
            toggleNormalSize()
        }
    }

    // Hiển thị Dialog hỏi xem tiếp hay xem lại
    if (showResumeDialog) {
        AlertDialog(
            onDismissRequest = { showResumeDialog = false },
            title = {
                Text("Tiếp tục xem?", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Bạn có muốn tiếp tục xem từ vị trí đã dừng hay xem lại từ đầu?")
            },
            confirmButton = {
                Button(
                    onClick = {
                    showResumeDialog = false
                    player.seekTo(savedProgress) // Tua đến vị trí đã lưu
                    player.play() },
                    shape = RoundedCornerShape(4.dp),
                    ) {
                    Text("Xem tiếp")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                    showResumeDialog = false
                    savedProgress = 0 // Xóa tiến độ đã lưu
                    player.seekTo(0L) // Tua lại từ đầu
                    player.play()},
                    shape = RoundedCornerShape(4.dp),
                ){
                    Text("Xem lại từ đầu")
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Hiển thị video
        Box(modifier = Modifier.fillMaxWidth()) {
            // Đảm bảo PlayerView chỉ được tạo 1 lần và không bị tái tạo
            AndroidView(
                modifier = videoModifier.align(Alignment.TopCenter),
                factory = {
                    playerView.apply {
                        this.player = player // Gán player tại đây
                    }
                }
            )

            // Nút ba chấm để hiển thị menu
            IconButton(
                onClick = { showMenu = !showMenu },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 20.dp, start = 10.dp)
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = Color.DarkGray,
                    modifier = Modifier.size(30.dp)
                )
            }

            // Dropdown menu chứa các tùy chọn
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                modifier = Modifier
                    .align(Alignment.TopStart)
            ) {
                // Nút phóng to
                DropdownMenuItem(onClick = {
                    toggleFullScreen()
                    showMenu = false
                }) {
                    Row {
                        Column {
                            androidx.compose.material3.Icon(
                                painter = painterResource(id = R.drawable.baseline_zoom_out_map_24),
                                contentDescription = "Phóng to"
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Full Screen"
                            )
                        }
                    }
                }

                // Nút thu nhỏ
                DropdownMenuItem(onClick = {
                    toggleNormalSize()
                    showMenu = false
                }) {
                    Row {
                        Column {
                            androidx.compose.material3.Icon(
                                painter = painterResource(id = R.drawable.baseline_zoom_in_map_24),
                                contentDescription = "Thu Nhỏ"
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Normal Screen"
                            )
                        }
                    }
                }

                // Nút chỉnh âm lượng
                DropdownMenuItem(
                    onClick = {
                        showVolumeSlider = !showVolumeSlider
                        showMenu = false
                    }) {
                    Row {
                        Column {
                            androidx.compose.material3.Icon(
                                painter = painterResource(id = R.drawable.baseline_volume_up_24),
                                contentDescription = "Volume"
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Volume"
                            )
                        }
                    }
                }
            }

            // Thanh trượt âm lượng
            if (showVolumeSlider) {
                Slider(
                    value = volumeLevel,
                    onValueChange = updateVolume,
                    valueRange = 0f..1f,
                    modifier = Modifier
                        .width(100.dp) // Chiều cao thanh chỉnh âm lượng (chiều dọc)
                        .align(Alignment.TopStart)
                        .padding(top = 40.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(onPress = {
                                // Khi có sự chạm vào thanh trượt, reset thời gian ẩn thanh trượt
                                showVolumeSlider = true
                            })
                        }
                )
            }


        }

        // Hiển thị phần bình luận khi video thu nhỏ
        if (!isFullScreen) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)) {
                // Hiển thị tiêu đề phim
                episodeState.selectedEpisode?.let { episode ->
                    Text(
                        text = episode.episodeTitle, // Sử dụng tiêu đề của tập phim
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Text(
                    text = "Bình luận",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                CommentSection(episodeId,commentViewModel)
            }
        }
    }

    // Lưu tiến độ khi người dùng thoát tab
    DisposableEffect(Unit) {
        onDispose {
            val progress = player.currentPosition
            sharedPreferences.edit().putLong(savedProgressKey, progress).apply()
            player.release()
        }
    }
}

@Composable
fun CommentSection(
    episodeId: String,
    commentViewModel: CommentViewModel
) {
    // Gọi fetchAllComments khi màn hình được hiển thị và mỗi khi state thay đổi
    LaunchedEffect(commentViewModel.commentsState.value.commentPage?.pageNumber) {
        val currentPage = commentViewModel.commentsState.value.commentPage?.pageNumber ?: 0
        commentViewModel.fetchAllComments(episodeId, currentPage) // Tải bình luận cho trang hiện tại
    }

    val commentState by commentViewModel.commentsState
    var commentText by remember { mutableStateOf("") }
    var editingCommentId by remember { mutableStateOf<String?>(null) } // Lưu ID của bình luận đang sửa
    var showError by remember { mutableStateOf(false) } // Trạng thái hiển thị lỗi

    // Kiểm tra trạng thái loading, error, hoặc dữ liệu
    when {
        commentState.loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        commentState.error != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Error: ${commentState.error}", color = Color.Red)
            }
        }
        else -> {
            commentState.commentPage?.let { commentPage ->
                Column(modifier = Modifier.fillMaxSize()) {
                    // Hiển thị phân trang nếu có nhiều hơn 1 trang
                    if (commentPage.totalPages > 1) {
                        PaginationControls(
                            currentPage = commentPage.pageNumber,
                            totalPages = commentPage.totalPages,
                            onPageSelected = { page -> commentViewModel.onPageSelected(page, episodeId) }, // Chọn trang mới
                            onNextPage = { commentViewModel.nextPage(episodeId) },
                            onPreviousPage = { commentViewModel.previousPage(episodeId) }
                        )
                    }

                    // Hiển thị danh sách bình luận
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)  // Đảm bảo LazyColumn chiếm phần không gian còn lại
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(commentPage.commentResponseDtoList.orEmpty()) { comment ->
                            CommentItem(
                                comment = comment,
                                onEdit = { commentId ->
                                    editingCommentId = commentId // Set ID bình luận đang sửa
                                    commentText = comment.content // Đặt nội dung bình luận vào TextField
                                },
                                onDelete = { commentId ->
                                    commentViewModel.deleteComment(episodeId, commentId)
                                }
                            )
                        }
                    }

                    // Form thêm bình luận hoặc sửa bình luận
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        // TextField và các nút (Cancel, Update/Add) trong cùng một dòng
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 40.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // TextField nhập bình luận
                            TextField(
                                value = commentText,
                                onValueChange = {
                                    commentText = it
                                    showError = commentText.isBlank() // Hiển thị lỗi nếu trống
                                },
                                label = { Text(if (editingCommentId == null) "Add a comment" else "Edit your comment") },
                                placeholder = { Text("Write something...") },
                                isError = showError, // Đánh dấu lỗi cho TextField
                                modifier = Modifier
                                    .weight(1f) // Chiếm hết không gian còn lại
                                    .padding(end = 8.dp) // Khoảng cách giữa TextField và nút
                            )

                            // Nút Cancel (hiển thị khi đang sửa bình luận)
                            if (editingCommentId != null) {
                                Button(
                                    modifier = Modifier
                                        .width(90.dp)
                                        .height(40.dp)
                                        .padding(end = 8.dp), // Khoảng cách giữa Cancel và Update
                                    shape = RoundedCornerShape(4.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFF2B8B5), // Màu nền nút Cancel
                                        contentColor = Color.Black          // Màu chữ bên trong nút
                                    ),
                                    onClick = {
                                        // Reset về trạng thái ban đầu khi hủy sửa
                                        editingCommentId = null
                                        commentText = ""
                                        showError = false
                                    }
                                ) {
                                    Text("Huỷ")
                                }
                            }

                            // Nút gửi bình luận (Add hoặc Update)
                            Button(
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(40.dp),
                                shape = RoundedCornerShape(4.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFC6A2FA), // Màu nền của Button
                                    contentColor = Color.Black          // Màu chữ bên trong Button
                                ),
                                onClick = {
                                    if (commentText.isNotBlank()) {
                                        if (editingCommentId != null) {
                                            // Nếu đang sửa bình luận
                                            commentViewModel.updateComment(episodeId, editingCommentId!!, commentText.trim())
                                            editingCommentId = null // Reset ID sau khi sửa xong
                                        } else {
                                            // Nếu là bình luận mới
                                            commentViewModel.addComment(episodeId, commentText.trim())
                                        }
                                        commentText = "" // Clear input sau khi gửi
                                        showError = false
                                    } else {
                                        showError = true
                                    }
                                },
                                enabled = commentText.isNotBlank() // Chỉ cho phép nhấn nếu không rỗng
                            ) {
                                Text(if (editingCommentId == null) "Gửi" else "Sửa")
                            }
                        }

                        // Hiển thị lỗi nếu có
                        if (showError) {
                            Text(
                                text = "Comment cannot be empty",
                                color = Color.Red,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 4.dp)
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
        if (currentPage > 0) {
            IconButton(onClick = onPreviousPage) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                    contentDescription = "Previous Page")
            }
        }

        // Dãy số trang với tối đa 6 nút
        val pagesToShow = mutableListOf<Int>()

        // Luôn luôn hiển thị trang đầu tiên
        pagesToShow.add(0)

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
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isCurrentPage) Color(0xFFF2B8B5) else Color(0xFFC6A2FA))
                        .clickable { if (!isCurrentPage) onPageSelected(page) }
                        .size(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (page + 1).toString(),
                        color = Color.Black,
                    )
                }
            }
        }

        // Nút tiến
        if (currentPage < totalPages - 1) {
            IconButton(onClick = onNextPage) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_arrow_forward_24),
                    contentDescription = "Next Page")
            }
        }
    }
}


@Composable
fun CommentItem(comment: Comment, onEdit: (String) -> Unit, onDelete: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Ảnh đại diện người dùng
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = comment.username.first().uppercase(),
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Spacer(modifier = Modifier.width(8.dp))

        // Nội dung bình luận
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = comment.username,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = comment.createAt,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Nếu bình luận là của người dùng, hiển thị nút chỉnh sửa và xóa
        if (comment.canCancel) {
            IconButton(onClick = { onEdit(comment.commentid) }) {
                Icon(painter = painterResource(id = R.drawable.baseline_colorize_24), contentDescription = "Edit")
            }
            IconButton(onClick = { onDelete(comment.commentid) }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Comment")
            }
        }
    }
}


//@Composable
//fun CommentSection(
//    episodeId: String,
//    commentViewModel: CommentViewModel
//) {
//    // Gọi fetchAllComments khi màn hình được hiển thị và mỗi khi state thay đổi
//    LaunchedEffect(commentViewModel.commentsState.value.commentPage?.pageNumber) {
//        val currentPage = commentViewModel.commentsState.value.commentPage?.pageNumber ?: 0
//        commentViewModel.fetchAllComments(episodeId, currentPage) // Tải bình luận cho trang hiện tại
//    }
//
//    val commentState by commentViewModel.commentsState
//    var commentText by remember { mutableStateOf("") }
//    var editingCommentId by remember { mutableStateOf<String?>(null) } // Lưu ID của bình luận đang sửa
//
//    // Kiểm tra trạng thái loading, error, hoặc dữ liệu
//    when {
//        commentState.loading -> {
//            Box(
//                modifier = Modifier.fillMaxSize(),
//                contentAlignment = Alignment.Center
//            ) {
//                CircularProgressIndicator()
//            }
//        }
//        commentState.error != null -> {
//            Box(
//                modifier = Modifier.fillMaxSize(),
//                contentAlignment = Alignment.Center
//            ) {
//                Text(text = "Error: ${commentState.error}", color = Color.Red)
//            }
//        }
//        else -> {
//            commentState.commentPage?.let { commentPage ->
//                Column(modifier = Modifier.fillMaxSize()) {
//                    // Hiển thị phân trang nếu có nhiều hơn 1 trang
//                    if (commentPage.totalPages > 1) {
//                        PaginationControls(
//                            currentPage = commentPage.pageNumber,
//                            totalPages = commentPage.totalPages,
//                            onPageSelected = { page -> commentViewModel.onPageSelected(page, episodeId) }, // Chọn trang mới
//                            onNextPage = { commentViewModel.nextPage(episodeId) },
//                            onPreviousPage = { commentViewModel.previousPage(episodeId) }
//                        )
//                    }
//
//                    // Hiển thị danh sách bình luận
//                    LazyColumn(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .weight(1f)  // Đảm bảo LazyColumn chiếm phần không gian còn lại
//                            .padding(8.dp),
//                        verticalArrangement = Arrangement.spacedBy(8.dp)
//                    ) {
//                        items(commentPage.commentResponseDtoList.orEmpty()) { comment ->
//                            CommentItem(
//                                comment = comment,
//                                onEdit = { commentId ->
//                                    editingCommentId = commentId // Set ID bình luận đang sửa
//                                    commentText = comment.content // Đặt nội dung bình luận vào TextField
//                                },
//                                onDelete = { commentId ->
//                                    commentViewModel.deleteComment(episodeId,commentId)
//                                }
//                            )
//                        }
//                    }
//
//                    // Form thêm bình luận hoặc sửa bình luận
//                    Column(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(8.dp)
//                    ) {
//                        // TextField nhập bình luận
//                        TextField(
//                            value = commentText,
//                            onValueChange = { commentText = it },
//                            label = { Text(if (editingCommentId == null) "Add a comment" else "Edit your comment") },
//                            modifier = Modifier.fillMaxWidth(),
//                            placeholder = { Text("Write something...") }
//                        )
//
//                        Spacer(modifier = Modifier.height(8.dp)) // Khoảng cách giữa TextField và Button
//
//                        Row(
//                            modifier = Modifier.fillMaxWidth(),
//                            horizontalArrangement = Arrangement.SpaceBetween
//                        ) {
//                            // Nút hủy nếu đang sửa bình luận
//                            if (editingCommentId != null) {
//                                Button(
//                                    onClick = {
//                                        // Reset về trạng thái ban đầu khi hủy sửa
//                                        editingCommentId = null
//                                        commentText = ""
//                                    },
//                                    modifier = Modifier.weight(1f)
//                                ) {
//                                    Text("Cancel")
//                                }
//                            }
//
//                            // Nút gửi bình luận
//                            Button(
//                                onClick = {
//                                    if (commentText.isNotEmpty()) {
//                                        if (editingCommentId != null) {
//                                            // Nếu đang sửa bình luận
//                                            commentViewModel.updateComment(editingCommentId!!, commentText, episodeId)
//                                            editingCommentId = null // Reset ID sau khi sửa xong
//                                        } else {
//                                            // Nếu là bình luận mới
//                                            commentViewModel.addComment(episodeId, commentText)
//                                        }
//                                        commentText = "" // Clear input sau khi gửi
//                                    }
//                                },
//                                modifier = Modifier.weight(1f)
//                            ) {
//                                Text(if (editingCommentId == null) "Add Comment" else "Update Comment")
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}










