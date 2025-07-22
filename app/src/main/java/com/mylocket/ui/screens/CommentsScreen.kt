package com.mylocket.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mylocket.R
import com.mylocket.data.Comment
import com.mylocket.service.SupabaseAuthService
import com.mylocket.service.SupabaseDatabaseService
import com.mylocket.ui.theme.BlueOcean
import com.mylocket.ui.theme.CommentBackground
import com.mylocket.ui.theme.CommentTextActive
import com.mylocket.ui.theme.CommentTextInactive
import com.mylocket.viewmodel.CommentViewModel
import com.mylocket.viewmodel.CommentViewModelFactory
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsScreen(
    navController: NavController,
    postId: String,
    currentUserId: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val databaseService = SupabaseDatabaseService()
    val authService = SupabaseAuthService()

    // Comment ViewModel
    val commentViewModel: CommentViewModel = viewModel(
        factory = CommentViewModelFactory(postId, context)
    )

    val comments by commentViewModel.comments.collectAsState()
    val isLoading by commentViewModel.isLoading.collectAsState()

    // Comment input state
    var commentText by remember { mutableStateOf(TextFieldValue("")) }
    var isSubmitting by remember { mutableStateOf(false) }

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Bình luận",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close),
                            contentDescription = "Quay lại",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                )
            )
        },
        bottomBar = {
            // Comment input section
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.Black,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    TextField(
                        value = commentText.text,
                        onValueChange = { newText ->
                            commentText = TextFieldValue(newText)
                        },
                        placeholder = {
                            Text(
                                text = "Viết bình luận...",
                                color = CommentTextInactive
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 56.dp),
                        maxLines = 4,
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = CommentTextActive,
                            unfocusedTextColor = CommentTextInactive,
                            focusedContainerColor = CommentBackground,
                            unfocusedContainerColor = Color.Gray.copy(alpha = 0.2f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = CommentTextActive
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    IconButton(
                        onClick = {
                            if (commentText.text.isNotBlank() && !isSubmitting) {
                                scope.launch {
                                    try {
                                        isSubmitting = true
                                        commentViewModel.addComment(
                                            content = commentText.text,
                                            userId = currentUserId,
                                            userName = currentUserName
                                        )
                                        commentText = TextFieldValue("")
                                    } catch (e: Exception) {
                                        // Handle error if needed
                                    } finally {
                                        isSubmitting = false
                                    }
                                }
                            }
                        },
                        enabled = commentText.text.isNotBlank() && !isSubmitting,
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                if (commentText.text.isNotBlank() && !isSubmitting) BlueOcean else Color.Gray.copy(alpha = 0.3f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_send),
                            contentDescription = "Gửi bình luận",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        },
        containerColor = Color.Black
    ) { paddingValues ->
        // Comments list
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = BlueOcean
                        )
                    }
                }
            } else if (comments.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "💬",
                                style = MaterialTheme.typography.displayMedium
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Chưa có bình luận nào",
                                color = Color.White.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Hãy là người đầu tiên bình luận!",
                                color = Color.White.copy(alpha = 0.5f),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(
                    items = comments,
                    key = { comment -> comment.id }
                ) { comment ->
                    CommentItem(
                        comment = comment,
                        currentUserId = currentUserId
                    )
                }
            }
        }
    }
}

@Composable
private fun CommentItem(
    comment: Comment,
    currentUserId: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // User avatar
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    BlueOcean.copy(alpha = 0.2f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = comment.userName.take(1).uppercase(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = BlueOcean
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
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = formatTimeAgo(comment.time),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

private fun formatTimeAgo(time: Instant?): String {
    if (time == null) return "vừa xong"

    val now = Clock.System.now()
    val duration = now - time

    return when {
        duration < 1.minutes -> "vừa xong"
        duration < 1.hours -> "${duration.inWholeMinutes}p"
        duration < 1.days -> "${duration.inWholeHours}h"
        else -> "${duration.inWholeDays}d"
    }
}
