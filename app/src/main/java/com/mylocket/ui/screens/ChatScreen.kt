package com.mylocket.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mylocket.R
import com.mylocket.data.ChatConversation
import com.mylocket.data.Friend
import com.mylocket.service.ChatService
import com.mylocket.ui.theme.BlueOcean
import com.mylocket.viewmodel.AuthViewModel
import com.mylocket.viewmodel.FriendViewModel
import com.mylocket.viewmodel.FriendViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    chatService: ChatService,
    authViewModel: AuthViewModel = viewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val currentUserId = currentUser?.id ?: ""

    // Handle null user case - redirect to welcome if no user
    if (currentUser == null) {
        LaunchedEffect(Unit) {
            navController.navigate("welcome") {
                popUpTo("chat") { inclusive = true }
            }
        }
        return
    }

    // Create FriendViewModel with proper factory
    val friendViewModel: FriendViewModel = viewModel(
        factory = FriendViewModelFactory(currentUserId)
    )

    // Get friends list
    val friends by friendViewModel.friends.collectAsState()

    // Get conversations
    val conversations by chatService.getConversationsForUser(currentUserId).collectAsState(initial = emptyList())

    // Load mock data for testing
    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            chatService.loadMockData(currentUserId)
        }
    }


    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {
//                    Box(modifier = Modifier.fillMaxWidth()) {
//                        Text(
//                            text = "Tin nhắn",
//                            modifier = Modifier
//                                .align(Alignment.Center)
//                                .padding(end = 40.dp),
//                            fontWeight = FontWeight.Bold,
//                            fontFamily = FontFamily.SansSerif
//                        )
//                    }
//
//
//                },
//                navigationIcon = {
//                    IconButton(
//                        onClick = { navController.popBackStack() }
//                    ) {
//                        Icon(painter = painterResource(id = R.drawable.back) , contentDescription = null)
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = Background,
//                    titleContentColor = Color.Gray,
//                    navigationIconContentColor = Color.Black
//                )
//            )
//        }
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Tin nhắn",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(end = 40.dp),
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(painter = painterResource(id = R.drawable.back) , contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(paddingValues)
        ) {
            // Filter friends who have FRIENDS status
            val confirmedFriends = friends.filter { it.status == "FRIENDS" }

            when {
                conversations.isEmpty() && confirmedFriends.isEmpty() -> {
                    // Show empty state when no confirmed friends
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.empty_friend),
                            contentDescription = null,
                            modifier = Modifier.size(125.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Thêm bạn bè trên Locket",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Bạn cần bạn bè trên Locket để bắt đầu nhắn tin",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(horizontal = 30.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                else -> {
                    // Show conversations and friends list for chatting
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        // Header
                        Text(
                            text = if (conversations.isNotEmpty()) "Cuộc trò chuyện" else "Bạn bè",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Conversations and Friends list
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            // Show existing conversations first
                            items(conversations) { conversation ->
                                ConversationItem(
                                    conversation = conversation,
                                    currentUserId = currentUserId,
                                    friends = confirmedFriends,
                                    onClick = { friendId, friendName ->
                                        navController.navigate("chat_detail/$friendId/$friendName")
                                    }
                                )
                            }

                            // Show friends without conversations
                            items(confirmedFriends.filter { friend ->
                                conversations.none { conversation ->
                                    conversation.participants.contains(friend.friendId)
                                }
                            }) { friend ->
                                FriendChatItem(
                                    friend = friend,
                                    onClick = {
                                        navController.navigate("chat_detail/${friend.friendId}/${friend.name}")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConversationItem(
    conversation: ChatConversation,
    currentUserId: String,
    friends: List<Friend>,
    onClick: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Get the other participant (friend)
    val friendId = conversation.participants.find { it != currentUserId } ?: return
    val friend = friends.find { it.friendId == friendId }
    val friendName = friend?.name ?: when (friendId) {
        "friend_123" -> "Anna"
        "friend_456" -> "Minh"
        "friend_789" -> "Linh"
        else -> "Unknown"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick(friendId, friendName) },
        colors = CardDefaults.cardColors(
            containerColor = Color.Gray.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar placeholder
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(BlueOcean),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = friendName.take(1).uppercase(),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = friendName,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                conversation.lastMessage?.let { lastMessage ->
                    Text(
                        text = lastMessage.content,
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                conversation.lastMessage?.let { lastMessage ->
                    Text(
                        text = formatTimestamp(lastMessage.timestamp),
                        color = Color.White.copy(alpha = 0.5f),
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 12.sp
                    )
                }

                if (conversation.unreadCount > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(BlueOcean),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = conversation.unreadCount.toString(),
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FriendChatItem(
    friend: Friend,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.Gray.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar placeholder
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(BlueOcean),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = friend.name.take(1).uppercase(),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = friend.name,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Tap to start chatting",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Icon(
                painter = painterResource(id = R.drawable.ic_send),
                contentDescription = "Start chat",
                tint = BlueOcean,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60000 -> "now"
        diff < 3600000 -> "${diff / 60000}m"
        diff < 86400000 -> SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
        diff < 604800000 -> SimpleDateFormat("EEE", Locale.getDefault()).format(Date(timestamp))
        else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(timestamp))
    }
}
