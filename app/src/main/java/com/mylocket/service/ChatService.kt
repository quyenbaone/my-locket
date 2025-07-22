package com.mylocket.service

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
    private val databaseService: SupabaseDatabaseService
) {
    private val _conversations = MutableStateFlow<List<ChatConversation>>(emptyList())
    val conversations: StateFlow<List<ChatConversation>> = _conversations.asStateFlow()
    
    private val _messages = MutableStateFlow<Map<String, List<Message>>>(emptyMap())
    val messages: StateFlow<Map<String, List<Message>>> = _messages.asStateFlow()
    
    private val _onlineUsers = MutableStateFlow<Set<String>>(emptySet())
    val onlineUsers: StateFlow<Set<String>> = _onlineUsers.asStateFlow()

    // Check if two users are friends before allowing chat
    suspend fun canChatWith(currentUserId: String, targetUserId: String): Result<Boolean> {
        return try {
            Log.d("ChatService", "Checking if $currentUserId can chat with $targetUserId")

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
            
            // Add message to local storage
            val currentMessages = _messages.value[conversationId] ?: emptyList()
            _messages.value = _messages.value.toMutableMap().apply {
                put(conversationId, currentMessages + message)
            }
            
            // Update conversation's last message
            updateConversationLastMessage(conversationId, message)

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
        val mockFriendId = "friend_123"
        val conversationId = "${listOf(currentUserId, mockFriendId).sorted().joinToString("_")}"
        
        val mockMessages = listOf(
            Message(
                id = "msg_1",
                senderId = mockFriendId,
                receiverId = currentUserId,
                content = "Hey! How are you doing?",
                timestamp = System.currentTimeMillis() - 3600000, // 1 hour ago
                isRead = true
            ),
            Message(
                id = "msg_2",
                senderId = currentUserId,
                receiverId = mockFriendId,
                content = "I'm doing great! Thanks for asking 😊",
                timestamp = System.currentTimeMillis() - 3000000, // 50 minutes ago
                isRead = true
            ),
            Message(
                id = "msg_3",
                senderId = mockFriendId,
                receiverId = currentUserId,
                content = "That's awesome! Want to hang out later?",
                timestamp = System.currentTimeMillis() - 1800000, // 30 minutes ago
                isRead = false
            )
        )
        
        _messages.value = mapOf(conversationId to mockMessages)
        
        val mockConversation = ChatConversation(
            id = conversationId,
            participants = listOf(currentUserId, mockFriendId),
            lastMessage = mockMessages.last(),
            lastActivity = mockMessages.last().timestamp,
            unreadCount = 1
        )
        
        _conversations.value = listOf(mockConversation)
    }
}
