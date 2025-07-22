package com.mylocket.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mylocket.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.mylocket.data.Post
import com.mylocket.data.User
import com.mylocket.service.SupabaseDatabaseService
import com.mylocket.viewmodel.PostViewModel
import com.mylocket.viewmodel.PostViewModelFactory
import com.mylocket.viewmodel.CommentViewModel
import com.mylocket.viewmodel.CommentViewModelFactory
import com.mylocket.ui.theme.MyLocketTheme
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import com.mylocket.data.Comment
import com.mylocket.service.SupabaseAuthService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostsComponent(
    userId: String,
    modifier: Modifier = Modifier
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
                    currentUserId = userId
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
    currentUserId: String
) {
    val scope = rememberCoroutineScope()
    val databaseService = SupabaseDatabaseService()

    // State for sender name
    var senderName by remember { mutableStateOf<String?>(null) }

    // State for comment bottom sheet
    var showCommentSheet by remember { mutableStateOf(false) }
    val commentSheetState = rememberModalBottomSheetState()

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
                onClick = { showCommentSheet = true },
                modifier = Modifier
                    .background(
                        Color.Black.copy(alpha = 0.6f),
                        shape = CircleShape
                    )
                    .size(48.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_send),
                    contentDescription = "Bình luận",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }

    // Comment Bottom Sheet
    if (showCommentSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCommentSheet = false },
            sheetState = commentSheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            CommentBottomSheet(
                postId = post.id,
                currentUserId = currentUserId,
                onDismiss = { showCommentSheet = false }
            )
        }
    }
}

@Composable
private fun CommentBottomSheet(
    postId: String,
    currentUserId: String,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val databaseService = SupabaseDatabaseService()
    val authService = SupabaseAuthService()

    // Comment ViewModel
    val commentViewModel: CommentViewModel = viewModel(
        factory = CommentViewModelFactory(postId)
    )

    val comments by commentViewModel.comments.collectAsState()
    val isLoading by commentViewModel.isLoading.collectAsState()

    // Comment input state
    var commentText by remember { mutableStateOf(TextFieldValue("")) }

    // Get current user info
    val currentUser = authService.getCurrentUser()
    var currentUserName by remember { mutableStateOf("") }

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            scope.launch {
                val result = databaseService.getUserById(currentUser.id)
                if (result.isSuccess) {
                    val user = result.getOrNull()
                    currentUserName = user?.name?.takeIf { it.isNotBlank() } ?: "Bạn"
                } else {
                    currentUserName = "Bạn"
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Bình luận",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = onDismiss) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = "Đóng"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Comments list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else if (comments.isEmpty()) {
                item {
                    Text(
                        text = "Chưa có bình luận nào",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                items(comments) { comment ->
                    CommentItem(comment = comment, currentUserId = currentUserId)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Comment input
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            OutlinedTextField(
                value = commentText,
                onValueChange = { commentText = it },
                placeholder = { Text("Viết bình luận...") },
                modifier = Modifier.weight(1f),
                maxLines = 3,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (commentText.text.isNotBlank()) {
                        commentViewModel.addComment(
                            content = commentText.text,
                            userId = currentUserId,
                            userName = currentUserName
                        )
                        commentText = TextFieldValue("")
                    }
                },
                enabled = commentText.text.isNotBlank()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_send),
                    contentDescription = "Gửi bình luận",
                    tint = if (commentText.text.isNotBlank())
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun CommentItem(
    comment: Comment,
    currentUserId: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // User avatar placeholder
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = comment.userName.take(1).uppercase(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = comment.userName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = formatTimeAgo(comment.time),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
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
