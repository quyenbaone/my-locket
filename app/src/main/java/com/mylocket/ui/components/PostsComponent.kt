package com.mylocket.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mylocket.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.mylocket.data.Post
import com.mylocket.data.User
import com.mylocket.service.SupabaseDatabaseService
import com.mylocket.viewmodel.PostViewModel
import com.mylocket.ui.theme.BlueOcean
import com.mylocket.viewmodel.PostViewModelFactory
import com.mylocket.ui.theme.MyLocketTheme
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import android.util.Log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostsComponent(
    userId: String,
    modifier: Modifier = Modifier,
    navController: androidx.navigation.NavController? = null
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp

    // Get PostViewModel for this user
    val postViewModel: PostViewModel = viewModel(
        factory = PostViewModelFactory(userId)
    )

    // Collect posts from ViewModel
    val posts by postViewModel.posts.collectAsState()

    // Loading state
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(posts) {
        isLoading = false
    }

    when {
        isLoading -> {
            // Show loading indicator
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        posts.isEmpty() -> {
            // Show empty state
            EmptyPostsState(screenWidth)
        }
        else -> {
            // Show all posts with vertical pager
            val pagerState = rememberPagerState(
                initialPage = 0,
                pageCount = { posts.size }
            )

            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                PostItem(
                    post = posts[page],
                    screenWidth = screenWidth,
                    currentIndex = page,
                    totalPosts = posts.size,
                    currentUserId = userId,
                    navController = navController
                )
            }
        }
    }
}

@Composable
private fun EmptyPostsState(screenWidth: Int) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(150.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(screenWidth.dp)
                .clip(shape = RoundedCornerShape(60.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "📸",
                    style = MaterialTheme.typography.displayLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Chưa có ảnh nào",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Hãy chụp ảnh đầu tiên!",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Vuốt sang trái để mở camera",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(150.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PostItem(
    post: Post,
    screenWidth: Int,
    currentIndex: Int = 0,
    totalPosts: Int = 1,
    currentUserId: String,
    navController: androidx.navigation.NavController? = null
) {
    val scope = rememberCoroutineScope()
    val databaseService = SupabaseDatabaseService()

    // State for sender name
    var senderName by remember { mutableStateOf<String?>(null) }

    // Remove inline comments state - we'll navigate to full screen instead

    // Load sender name
    LaunchedEffect(post.userId) {
        if (post.userId == currentUserId) {
            senderName = "Bạn"
        } else {
            scope.launch {
                val result = databaseService.getUserById(post.userId)
                if (result.isSuccess) {
                    val user = result.getOrNull()
                    senderName = user?.name?.takeIf { it.isNotBlank() } ?: "Bạn bè"
                } else {
                    senderName = "Bạn bè"
                }
            }
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(150.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(screenWidth.dp)
                .clip(shape = RoundedCornerShape(60.dp)),
        ) {
            // Load image from URL using Coil
            AsyncImage(
                model = post.photo,
                contentDescription = "Post image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Show post indicator (current/total)
            if (totalPosts > 1) {
                Text(
                    text = "${currentIndex + 1}/$totalPosts",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(
                            Color.Black.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            // Show content if available
            if (post.content.isNotEmpty()) {
                Text(
                    text = post.content,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 20.dp)
                        .background(
                            Color.Black.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                )
            }
        }

        // Show time ago and post info
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                Text(
                    text = "${senderName ?: "..."} ",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = formatTimeAgo(post.time),
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            // Show swipe hint for multiple posts
            if (totalPosts > 1) {
                Text(
                    text = "Vuốt lên/xuống để xem ảnh khác",
                    color = Color.Gray.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Comment button
            Spacer(modifier = Modifier.height(16.dp))

            IconButton(
                onClick = {
                    Log.d("PostItem", "Comment button clicked for post: ${post.id}")
                    navController?.navigate("comments/${post.id}/${currentUserId}")
                },
                modifier = Modifier
                    .background(
                        Color.Black.copy(alpha = 0.6f),
                        shape = CircleShape
                    )
                    .size(64.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_send),
                    contentDescription = "Xem bình luận",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}


private fun formatTimeAgo(time: Instant?): String {
    if (time == null) return "vừa xong"

    val now = kotlinx.datetime.Clock.System.now()
    val duration = now - time

    return when {
        duration < 1.minutes -> "vừa xong"
        duration < 1.hours -> "${duration.inWholeMinutes}p"
        duration < 1.days -> "${duration.inWholeHours}h"
        else -> "${duration.inWholeDays}d"
    }
}

// Preview cho PostsComponent
@Preview(showBackground = true)
@Composable
fun PostsComponentPreview() {
    MyLocketTheme {
        // Mock UI cho preview (không có data thật)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Gray.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "📸 Posts",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Loading posts...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PostsComponentLoadingPreview() {
    MyLocketTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}
