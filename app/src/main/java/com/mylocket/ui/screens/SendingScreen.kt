package com.mylocket.ui.screens

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mylocket.service.SupabaseAuthService
import com.mylocket.service.SupabaseStorageService
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.mylocket.R
import com.mylocket.viewmodel.FriendViewModel
import com.mylocket.viewmodel.FriendViewModelFactory
import com.mylocket.viewmodel.PostViewModel
import com.mylocket.viewmodel.PostViewModelFactory
import com.mylocket.data.Friend
import com.mylocket.data.FriendStatus
import com.mylocket.ui.theme.BlueOcean
import com.mylocket.ui.theme.BlueLight
import java.io.File

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendingScreen(
    navController: NavController,
    imagePath: String?,
    authService: SupabaseAuthService
) {
    val scope = rememberCoroutineScope()
    val storageService = SupabaseStorageService()
    //Lấy chiều rộng màn hình
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp

    //Content để đăng post
    var message by remember {
        mutableStateOf("")
    }
    var textFieldWidth by remember { mutableStateOf(IntSize.Zero) }
    val placeholderText = "Thêm một tin nhắn"
    val placeholderWidth = placeholderText.length * 11



    var status by remember {
        mutableStateOf("all")
    }

    val context = LocalContext.current

    val currentUser = authService.getCurrentUser()

    // Handle null user case - redirect to welcome if no user
    if (currentUser == null) {
        LaunchedEffect(Unit) {
            navController.navigate("welcome") {
                popUpTo("sending") { inclusive = true }
            }
        }
        return
    }

    val friendViewModel: FriendViewModel = viewModel(
        factory = FriendViewModelFactory(currentUser.id)
    )
    val friendList by friendViewModel.friends.collectAsState(emptyList())

    val postViewModel: PostViewModel = viewModel(
        factory = PostViewModelFactory(currentUser.id)
    )

    var chooseSending by remember {
        mutableStateOf(listOf<String>())
    }

    var isUploading by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(friendList) {
        if (friendList.isNotEmpty()) {
            chooseSending = friendList.map { item -> item.friendId } + currentUser.id
        }
    }

    val bitmap = BitmapFactory.decodeFile(imagePath)
    // Lấy độ xoay
    val rotationDegrees = imagePath?.let { getRotationDegrees(it) }
    // Xoay bitmap nếu cần
    val rotatedBitmap = rotationDegrees?.let { rotateBitmap(bitmap, it) }
    Scaffold(
        topBar = {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(300)) + slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(500)
                )
            ) {
                TopAppBar(
                    title = {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Gửi đến...",
                                modifier = Modifier.align(Alignment.Center),
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.SansSerif,
                                color = Color.White
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.back),
                                contentDescription = "Quay lại"
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                val isSaved = saveImageToDownloads(context, imagePath!!)
                                if (isSaved) {
                                    Toast.makeText(context, "Lưu ảnh thành công", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Lưu ảnh thất bại", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_download),
                                contentDescription = "Tải xuống"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White
                    ),
                    modifier = Modifier.padding(top = 20.dp)
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(top = 150.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(screenWidth.dp)
                    .clip(RoundedCornerShape(60.dp))
            ) {
                Image(
                    bitmap = rotatedBitmap!!.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                TextField(
                    value = message,
                    onValueChange = {
                        input ->
                            message = input },
                    colors = TextFieldDefaults.colors(
                        cursorColor = Color.White,
                        unfocusedContainerColor = Color(0x80000000),
                        focusedContainerColor = Color(0x80000000),
                        focusedTextColor = Color.White,
                        unfocusedPlaceholderColor = Color.White
                    ),
                    placeholder = {
                        Text(text = "Thêm một tin nhắn")
                    },
                    modifier = Modifier
                        .onGloballyPositioned { coordinates ->
                            textFieldWidth = coordinates.size
                        }
                        .width(if (message == "") placeholderWidth.dp else with(LocalDensity.current) { textFieldWidth.width.toDp() })
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 10.dp)
                        .clip(RoundedCornerShape(20.dp)),
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp, bottom = 50.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = "",
                        tint = Color.White,
                        modifier = Modifier.size(35.dp)
                        )
                }

                IconButton(
                    onClick = {
                        if (!isUploading) {
                            val file = File(imagePath)  // Tạo đối tượng File từ đường dẫn
                            if (file.exists()) {
                                isUploading = true
                                // Sử dụng postViewModel để upload và add post
                                postViewModel.uploadAndAddPost(
                                    file = file,
                                    content = message,
                                    toWho = chooseSending,
                                    onSuccess = {
                                        isUploading = false
                                        Log.d("Supabase", "Post uploaded successfully")
                                        navController.popBackStack()
                                    },
                                    onError = { error ->
                                        isUploading = false
                                        Log.w("Supabase", "Error uploading post: $error")
                                        // TODO: Show error message to user
                                    }
                                )
                            } else {
                                Log.w("Supabase", "File does not exist at the specified path")
                            }
                        }
                    },
                    enabled = !isUploading,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = Color.White,
                        containerColor = if (isUploading) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else BlueOcean
                    ),
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.CenterVertically)
                    ) {
                    if (isUploading) {
                        // Show loading indicator
                        androidx.compose.material3.CircularProgressIndicator(
                            modifier = Modifier.size(50.dp),
                            color = Color.White,
                            strokeWidth = 4.dp
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_send),
                            contentDescription = "Gửi",
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_bling),
                        contentDescription = "",
                        tint = Color.White,
                        modifier = Modifier.size(35.dp)
                    )
                }
            }

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(50.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                item {
                    Column (
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        IconButton(
                            onClick = {
                                chooseSending = friendList.map { item -> item.id } + currentUser.id
                                status = "all"
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier
                                .border(
                                    width = 2.dp,
                                    color = if (status == "all") BlueOcean else MaterialTheme.colorScheme.outline,
                                    shape = CircleShape
                                )
                        ) {
                            Icon(painter = painterResource(id = R.drawable.friend), contentDescription = "")
                        }
                        Text(
                            text = "Tất cả",
                            modifier = Modifier.padding(top = 5.dp),
                            style = TextStyle(
                                fontWeight = FontWeight.SemiBold,
                                color = if (status == "all") BlueOcean else MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }


                }
                items(friendList) {
                    item: Friend ->
                    if (item.status == FriendStatus.FRIENDS.toString()){
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(start = 10.dp, end = 10.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    status = item.friendId
                                    chooseSending = listOf(currentUser.id, item.friendId)
                                }) {
                                Image(
                                    painter = painterResource(id = R.drawable.img),
                                    contentDescription = "",
                                    modifier = Modifier
                                        .border(
                                            width = 2.dp,
                                            color = if (status == item.friendId) BlueOcean else MaterialTheme.colorScheme.outline,
                                            shape = CircleShape
                                        )
                                )
                            }

                            Text(
                                text = item.name,
                                modifier = Modifier.padding(top = 5.dp),
                                style = TextStyle(
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (status == item.id) BlueOcean else MaterialTheme.colorScheme.onSurface
                                ),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                            )
                        }
                    }
                }

            }
        }
    }
}
fun getRotationDegrees(imagePath: String): Int {
    val exif = ExifInterface(imagePath)
    val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90
        ExifInterface.ORIENTATION_ROTATE_180 -> 180
        ExifInterface.ORIENTATION_ROTATE_270 -> 270
        else -> 0
    }
}

fun rotateBitmap(bitmap: Bitmap, degrees: Int): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(degrees.toFloat())
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}
fun saveImageToDownloads(context: Context, cacheImagePath: String): Boolean {
    val sourceFile = File(cacheImagePath)
    val filename = sourceFile.name

    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Sử dụng MediaStore cho Android 10 trở lên
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                resolver.openOutputStream(it)?.use { outputStream ->
                    sourceFile.inputStream().use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                true
            } ?: false
        } else {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val destinationFile = File(downloadsDir, filename)
            sourceFile.copyTo(destinationFile, overwrite = true)
            true
        }
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

// Preview cho SendingScreen
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SendingScreenPreview() {
    com.mylocket.ui.theme.MyLocketTheme {
        val navController = androidx.navigation.compose.rememberNavController()
        val authService = com.mylocket.service.SupabaseAuthService()
        SendingScreen(
            navController = navController,
            imagePath = null,
            authService = authService
        )
    }
}
