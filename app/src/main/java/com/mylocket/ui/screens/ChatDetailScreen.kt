package com.mylocket.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mylocket.data.Message
import com.mylocket.service.ChatService
import com.mylocket.ui.components.ChatHeader
import com.mylocket.ui.components.MessageBubble
import com.mylocket.ui.components.MessageInput
import com.mylocket.ui.components.TypingIndicator
import com.mylocket.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    navController: NavController,
    friendId: String,
    friendName: String,
    chatService: ChatService,
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    
    // State variables
    var messageText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var conversationId by remember { mutableStateOf<String?>(null) }
    var canChat by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Get current user
    val currentUser by authViewModel.currentUser.collectAsState()
    val currentUserId = currentUser?.id ?: ""
    
    // Collect messages for this conversation
    val messages by chatService.getMessagesForConversation(conversationId ?: "").collectAsState(initial = emptyList())
    
    // Initialize conversation
    LaunchedEffect(currentUserId, friendId) {
        if (currentUserId.isNotEmpty()) {
            // Check if users can chat
            val chatPermission = chatService.canChatWith(currentUserId, friendId)
            if (chatPermission.isSuccess && chatPermission.getOrDefault(false)) {
                canChat = true
                
                // Get or create conversation
                val conversationResult = chatService.getOrCreateConversation(currentUserId, friendId)
                if (conversationResult.isSuccess) {
                    conversationId = conversationResult.getOrNull()
                    
                    // Mark messages as read
                    conversationId?.let { convId ->
                        chatService.markMessagesAsRead(convId, currentUserId)
                    }
                } else {
                    errorMessage = "Failed to load conversation"
                }
            } else {
                canChat = false
                errorMessage = "You can only chat with friends"
            }
        }
    }
    
    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }
    
    Scaffold(
        topBar = {
            ChatHeader(
                friendName = friendName,
                isOnline = false, // TODO: Implement online status
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = {
            if (canChat) {
                MessageInput(
                    value = messageText,
                    onValueChange = { messageText = it },
                    onSendClick = {
                        if (messageText.trim().isNotEmpty() && conversationId != null) {
                            scope.launch {
                                isLoading = true
                                val result = chatService.sendMessage(
                                    conversationId = conversationId!!,
                                    senderId = currentUserId,
                                    receiverId = friendId,
                                    content = messageText.trim()
                                )
                                
                                if (result.isSuccess) {
                                    messageText = ""
                                    // Auto-scroll to bottom after sending
                                    listState.animateScrollToItem(messages.size)
                                } else {
                                    errorMessage = "Failed to send message"
                                }
                                isLoading = false
                            }
                        }
                    },
                    enabled = !isLoading && canChat
                )
            }
        },
        containerColor = Color.Black
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(paddingValues)
        ) {
            when {
                !canChat -> {
                    // Show error when users are not friends
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "❌",
                            style = MaterialTheme.typography.displayMedium
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Cannot start conversation",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = errorMessage ?: "You can only chat with confirmed friends",
                            color = Color.White.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                
                messages.isEmpty() -> {
                    // Show empty state
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "💬",
                            style = MaterialTheme.typography.displayMedium
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Start your conversation",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Send a message to start chatting with $friendName",
                            color = Color.White.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                
                else -> {
                    // Show messages
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(messages) { message ->
                            MessageBubble(
                                message = message,
                                isCurrentUser = message.senderId == currentUserId
                            )
                        }
                        
                        // Add some space at the bottom
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
            
            // Show error message if any
            errorMessage?.let { error ->
                LaunchedEffect(error) {
                    // Clear error after showing
                    kotlinx.coroutines.delay(3000)
                    errorMessage = null
                }
            }
        }
    }
}
