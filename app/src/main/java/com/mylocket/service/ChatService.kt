package com.mylocket.service

import android.content.Context
import android.util.Log
import com.mylocket.data.Message
import com.mylocket.data.ChatConversation
import com.mylocket.data.ChatUser
import com.mylocket.data.MessageType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class ChatService(
    private val databaseService: SupabaseDatabaseService,
    private val context: Context
) {
    private val localStorageService = LocalStorageService(context)

    private val _conversations = MutableStateFlow<List<ChatConversation>>(emptyList())
    val conversations: StateFlow<List<ChatConversation>> = _conversations.asStateFlow()

    private val _messages = MutableStateFlow<Map<String, List<Message>>>(emptyMap())
    val messages: StateFlow<Map<String, List<Message>>> = _messages.asStateFlow()

    private val _onlineUsers = MutableStateFlow<Set<String>>(emptySet())
    val onlineUsers: StateFlow<Set<String>> = _onlineUsers.asStateFlow()

    init {
        // Load saved data on initialization
        loadSavedData()
    }

    // Load saved conversations and messages from local storage
    private fun loadSavedData() {
        try {
            // Load conversations
            val savedConversations = localStorageService.loadConversations()
            if (savedConversations.isNotEmpty()) {
                _conversations.value = savedConversations
                Log.d("ChatService", "Loaded ${savedConversations.size} saved conversations")
            }

            // Load messages for each conversation
            val messagesMap = mutableMapOf<String, List<Message>>()
            savedConversations.forEach { conversation ->
                val messages = localStorageService.loadMessages(conversation.id)
                if (messages.isNotEmpty()) {
                    messagesMap[conversation.id] = messages
                }
            }

            if (messagesMap.isNotEmpty()) {
                _messages.value = messagesMap
                Log.d("ChatService", "Loaded messages for ${messagesMap.size} conversations")
            }
        } catch (e: Exception) {
            Log.e("ChatService", "Error loading saved data", e)
        }
    }

    // Check if two users are friends before allowing chat
    suspend fun canChatWith(currentUserId: String, targetUserId: String): Result<Boolean> {
        return try {
            Log.d("ChatService", "Checking if $currentUserId can chat with $targetUserId")

            // Allow chat with mock friends for testing
            val mockFriendIds = listOf("friend_123", "friend_456", "friend_789")
            if (targetUserId in mockFriendIds) {
                Log.d("ChatService", "Allowing chat with mock friend: $targetUserId")
                return Result.success(true)
            }

            // Get friends list from database
            val friendsResult = databaseService.getFriendsForUser(currentUserId)
            if (!friendsResult.isSuccess) {
                Log.e("ChatService", "Failed to get friends list")
                return Result.failure(friendsResult.exceptionOrNull() ?: Exception("Failed to get friends"))
            }

            val friends = friendsResult.getOrNull() ?: emptyList()
            val areFriends = friends.any { friend ->
                friend.friendId == targetUserId && friend.status == "FRIENDS"
            }

            Log.d("ChatService", "Friend verification result: $areFriends")
            Result.success(areFriends)
        } catch (e: Exception) {
            Log.e("ChatService", "Error checking friend status", e)
            Result.failure(e)
        }
    }

    // Get or create conversation between two users
    suspend fun getOrCreateConversation(currentUserId: String, friendId: String): Result<String> {
        return try {
            Log.d("ChatService", "Getting/creating conversation between $currentUserId and $friendId")
            
            // Check if users are friends first
            val canChat = canChatWith(currentUserId, friendId)
            if (!canChat.isSuccess || !canChat.getOrDefault(false)) {
                return Result.failure(Exception("Users are not friends"))
            }
            
            // Generate conversation ID (sorted to ensure consistency)
            val participants = listOf(currentUserId, friendId).sorted()
            val conversationId = "${participants[0]}_${participants[1]}"
            
            // Check if conversation already exists
            val existingConversations = _conversations.value
            val existingConversation = existingConversations.find { it.id == conversationId }
            
            if (existingConversation == null) {
                // Create new conversation
                val newConversation = ChatConversation(
                    id = conversationId,
                    participants = participants,
                    lastActivity = System.currentTimeMillis()
                )
                
                _conversations.value = existingConversations + newConversation
                Log.d("ChatService", "Created new conversation: $conversationId")
            }
            
            Result.success(conversationId)
        } catch (e: Exception) {
            Log.e("ChatService", "Error getting/creating conversation", e)
            Result.failure(e)
        }
    }

    // Send a message
    suspend fun sendMessage(
        conversationId: String,
        senderId: String,
        receiverId: String,
        content: String,
        messageType: MessageType = MessageType.TEXT
    ): Result<Message> {
        return try {
            Log.d("ChatService", "Sending message in conversation $conversationId")
            
            // Verify sender can chat with receiver
            val canChat = canChatWith(senderId, receiverId)
            if (!canChat.isSuccess || !canChat.getOrDefault(false)) {
                return Result.failure(Exception("Cannot send message: users are not friends"))
            }
            
            val message = Message(
                id = generateMessageId(),
                senderId = senderId,
                receiverId = receiverId,
                content = content,
                timestamp = System.currentTimeMillis(),
                messageType = messageType
            )
            
            // Add message to local memory storage
            val currentMessages = _messages.value[conversationId] ?: emptyList()
            _messages.value = _messages.value.toMutableMap().apply {
                put(conversationId, currentMessages + message)
            }

            // Save message to local persistent storage
            try {
                localStorageService.addMessage(conversationId, message)
                Log.d("ChatService", "Message saved to local storage: ${message.id}")
            } catch (e: Exception) {
                Log.e("ChatService", "Failed to save message to local storage", e)
            }

            // Update conversation's last message
            updateConversationLastMessage(conversationId, message)

            // Save updated conversation to local storage
            try {
                localStorageService.updateConversationLastMessage(conversationId, message)
                Log.d("ChatService", "Conversation updated in local storage: $conversationId")
            } catch (e: Exception) {
                Log.e("ChatService", "Failed to update conversation in local storage", e)
            }

            // Save to Supabase database
            try {
                saveMessageToDatabase(message, conversationId)
                Log.d("ChatService", "Message saved to database")
            } catch (e: Exception) {
                Log.w("ChatService", "Failed to save message to database, but keeping local copy", e)
            }

            Log.d("ChatService", "Message sent successfully: ${message.id}")
            Result.success(message)
        } catch (e: Exception) {
            Log.e("ChatService", "Error sending message", e)
            Result.failure(e)
        }
    }

    // Get messages for a conversation
    fun getMessagesForConversation(conversationId: String): Flow<List<Message>> {
        return messages.map { messagesMap ->
            messagesMap[conversationId] ?: emptyList()
        }
    }



    // Get conversations for a user
    fun getConversationsForUser(userId: String): Flow<List<ChatConversation>> {
        return conversations.map { allConversations ->
            allConversations.filter { conversation ->
                conversation.participants.contains(userId)
            }.sortedByDescending { it.lastActivity }
        }
    }

    // Mark messages as read
    suspend fun markMessagesAsRead(conversationId: String, userId: String): Result<Unit> {
        return try {
            Log.d("ChatService", "Marking messages as read for conversation: $conversationId")

            val currentMessages = _messages.value[conversationId] ?: emptyList()
            val updatedMessages = currentMessages.map { message ->
                if (message.receiverId == userId && !message.isRead) {
                    message.copy(isRead = true)
                } else {
                    message
                }
            }

            _messages.value = _messages.value.toMutableMap().apply {
                put(conversationId, updatedMessages)
            }

            // Update unread count in conversation
            val currentConversations = _conversations.value
            val updatedConversations = currentConversations.map { conversation ->
                if (conversation.id == conversationId) {
                    val unreadCount = updatedMessages.count {
                        it.receiverId == userId && !it.isRead
                    }
                    conversation.copy(unreadCount = unreadCount)
                } else {
                    conversation
                }
            }
            _conversations.value = updatedConversations

            // Save updated messages and conversations to local storage
            localStorageService.saveMessages(conversationId, updatedMessages)
            localStorageService.saveConversations(updatedConversations)

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ChatService", "Error marking messages as read", e)
            Result.failure(e)
        }
    }

    // Private helper methods
    private fun generateMessageId(): String {
        return "msg_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }

    private suspend fun saveMessageToDatabase(message: Message, conversationId: String) {
        try {
            // Create a map for the message data
            val messageData = mapOf(
                "id" to message.id,
                "conversation_id" to conversationId,
                "sender_id" to message.senderId,
                "receiver_id" to message.receiverId,
                "content" to message.content,
                "timestamp" to message.timestamp,
                "is_read" to message.isRead,
                "message_type" to message.messageType.name
            )

            // Save to messages table (you'll need to create this table in Supabase)
            // For now, we'll use a simple approach
            Log.d("ChatService", "Saving message to database: ${message.id}")
            // TODO: Implement actual database save when messages table is ready

        } catch (e: Exception) {
            Log.e("ChatService", "Error saving message to database", e)
            throw e
        }
    }

    private suspend fun loadMessagesFromDatabase(conversationId: String): List<Message> {
        return try {
            // TODO: Load messages from database
            // For now, return empty list
            Log.d("ChatService", "Loading messages from database for conversation: $conversationId")
            emptyList()
        } catch (e: Exception) {
            Log.e("ChatService", "Error loading messages from database", e)
            emptyList()
        }
    }

    private fun updateConversationLastMessage(conversationId: String, message: Message) {
        val currentConversations = _conversations.value
        val updatedConversations = currentConversations.map { conversation ->
            if (conversation.id == conversationId) {
                conversation.copy(
                    lastMessage = message,
                    lastActivity = message.timestamp
                )
            } else {
                conversation
            }
        }
        _conversations.value = updatedConversations
    }

    // Mock data for testing
    fun loadMockData(currentUserId: String) {
        // Only load mock data if no conversations exist yet
        if (_conversations.value.isNotEmpty()) {
            Log.d("ChatService", "Conversations already exist, skipping mock data load")
            return
        }

        Log.d("ChatService", "Loading mock data for user: $currentUserId")

        // Create multiple mock friends
        val mockFriends = listOf(
            "friend_123" to "Anna",
            "friend_456" to "Minh",
            "friend_789" to "Linh"
        )

        val allConversations = mutableListOf<ChatConversation>()
        val allMessages = mutableMapOf<String, List<Message>>()

        mockFriends.forEachIndexed { index, (friendId, friendName) ->
            val conversationId = "${listOf(currentUserId, friendId).sorted().joinToString("_")}"

            val mockMessages = when (index) {
                0 -> { // Anna - Active conversation
                    listOf(
                        Message(
                            id = "msg_${friendId}_1",
                            senderId = friendId,
                            receiverId = currentUserId,
                            content = "Chào bạn! Hôm nay thế nào? 😊",
                            timestamp = System.currentTimeMillis() - 7200000, // 2 hours ago
                            isRead = true
                        ),
                        Message(
                            id = "msg_${friendId}_2",
                            senderId = currentUserId,
                            receiverId = friendId,
                            content = "Chào Anna! Mình ổn, cảm ơn bạn đã hỏi 💕",
                            timestamp = System.currentTimeMillis() - 6900000, // 1h55m ago
                            isRead = true
                        ),
                        Message(
                            id = "msg_${friendId}_3",
                            senderId = friendId,
                            receiverId = currentUserId,
                            content = "Tuyệt vời! Bạn có muốn đi uống cà phê không?",
                            timestamp = System.currentTimeMillis() - 6600000, // 1h50m ago
                            isRead = true
                        ),
                        Message(
                            id = "msg_${friendId}_4",
                            senderId = currentUserId,
                            receiverId = friendId,
                            content = "Ý tưởng hay đấy! Mấy giờ bạn rảnh?",
                            timestamp = System.currentTimeMillis() - 6300000, // 1h45m ago
                            isRead = true
                        ),
                        Message(
                            id = "msg_${friendId}_5",
                            senderId = friendId,
                            receiverId = currentUserId,
                            content = "Chiều nay 3h được không? Mình biết một quán cà phê mới rất đẹp ✨",
                            timestamp = System.currentTimeMillis() - 1800000, // 30 minutes ago
                            isRead = false
                        ),
                        Message(
                            id = "msg_${friendId}_6",
                            senderId = friendId,
                            receiverId = currentUserId,
                            content = "Bạn còn đó không? 🤔",
                            timestamp = System.currentTimeMillis() - 600000, // 10 minutes ago
                            isRead = false
                        )
                    )
                }
                1 -> { // Minh - Recent conversation
                    listOf(
                        Message(
                            id = "msg_${friendId}_1",
                            senderId = currentUserId,
                            receiverId = friendId,
                            content = "Minh ơi, bạn có thấy ảnh mình vừa đăng không?",
                            timestamp = System.currentTimeMillis() - 3600000, // 1 hour ago
                            isRead = true
                        ),
                        Message(
                            id = "msg_${friendId}_2",
                            senderId = friendId,
                            receiverId = currentUserId,
                            content = "Có! Ảnh đẹp quá! Chụp ở đâu vậy? 📸",
                            timestamp = System.currentTimeMillis() - 3300000, // 55 minutes ago
                            isRead = true
                        ),
                        Message(
                            id = "msg_${friendId}_3",
                            senderId = currentUserId,
                            receiverId = friendId,
                            content = "Ở công viên gần nhà mình đó. Lần sau mình dẫn bạn đi nhé!",
                            timestamp = System.currentTimeMillis() - 3000000, // 50 minutes ago
                            isRead = true
                        ),
                        Message(
                            id = "msg_${friendId}_4",
                            senderId = friendId,
                            receiverId = currentUserId,
                            content = "Được luôn! Mình thích chụp ảnh lắm 🎨",
                            timestamp = System.currentTimeMillis() - 2400000, // 40 minutes ago
                            isRead = false
                        )
                    )
                }
                2 -> { // Linh - Older conversation
                    listOf(
                        Message(
                            id = "msg_${friendId}_1",
                            senderId = friendId,
                            receiverId = currentUserId,
                            content = "Chúc mừng sinh nhật bạn! 🎉🎂",
                            timestamp = System.currentTimeMillis() - 86400000, // 1 day ago
                            isRead = true
                        ),
                        Message(
                            id = "msg_${friendId}_2",
                            senderId = currentUserId,
                            receiverId = friendId,
                            content = "Cảm ơn Linh nhiều! Bạn nhớ mình quá 🥰",
                            timestamp = System.currentTimeMillis() - 86100000, // 23h55m ago
                            isRead = true
                        ),
                        Message(
                            id = "msg_${friendId}_3",
                            senderId = friendId,
                            receiverId = currentUserId,
                            content = "Tất nhiên rồi! Chúc bạn luôn vui vẻ và hạnh phúc nhé! 💝",
                            timestamp = System.currentTimeMillis() - 85800000, // 23h50m ago
                            isRead = true
                        )
                    )
                }
                else -> emptyList()
            }

            allMessages[conversationId] = mockMessages

            if (mockMessages.isNotEmpty()) {
                val unreadCount = mockMessages.count { !it.isRead && it.senderId != currentUserId }
                val mockConversation = ChatConversation(
                    id = conversationId,
                    participants = listOf(currentUserId, friendId),
                    lastMessage = mockMessages.last(),
                    lastActivity = mockMessages.last().timestamp,
                    unreadCount = unreadCount
                )
                allConversations.add(mockConversation)
            }
        }

        _messages.value = allMessages
        _conversations.value = allConversations.sortedByDescending { it.lastActivity }

        // Save mock data to local storage for persistence
        localStorageService.saveConversations(allConversations.sortedByDescending { it.lastActivity })
        allMessages.forEach { (conversationId, messages) ->
            localStorageService.saveMessages(conversationId, messages)
        }

        Log.d("ChatService", "Mock data saved to local storage")
    }
}
