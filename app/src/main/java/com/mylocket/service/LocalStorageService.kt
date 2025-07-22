package com.mylocket.service

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.mylocket.data.ChatConversation
import com.mylocket.data.Comment
import com.mylocket.data.Message
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

class LocalStorageService(private val context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "mylocket_chat_storage", 
        Context.MODE_PRIVATE
    )
    
    private val json = Json { 
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    
    companion object {
        private const val KEY_MESSAGES = "messages_"
        private const val KEY_CONVERSATIONS = "conversations"
        private const val KEY_COMMENTS = "comments_"
        private const val TAG = "LocalStorageService"
    }
    
    // Save messages for a conversation
    fun saveMessages(conversationId: String, messages: List<Message>) {
        try {
            val messagesJson = json.encodeToString(messages)
            prefs.edit()
                .putString(KEY_MESSAGES + conversationId, messagesJson)
                .apply()
            Log.d(TAG, "Saved ${messages.size} messages for conversation $conversationId")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving messages for conversation $conversationId", e)
        }
    }
    
    // Load messages for a conversation
    fun loadMessages(conversationId: String): List<Message> {
        return try {
            val messagesJson = prefs.getString(KEY_MESSAGES + conversationId, null)
            if (messagesJson != null) {
                val messages = json.decodeFromString<List<Message>>(messagesJson)
                Log.d(TAG, "Loaded ${messages.size} messages for conversation $conversationId")
                messages
            } else {
                Log.d(TAG, "No saved messages found for conversation $conversationId")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading messages for conversation $conversationId", e)
            emptyList()
        }
    }
    
    // Save conversations list
    fun saveConversations(conversations: List<ChatConversation>) {
        try {
            val conversationsJson = json.encodeToString(conversations)
            prefs.edit()
                .putString(KEY_CONVERSATIONS, conversationsJson)
                .apply()
            Log.d(TAG, "Saved ${conversations.size} conversations")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving conversations", e)
        }
    }
    
    // Load conversations list
    fun loadConversations(): List<ChatConversation> {
        return try {
            val conversationsJson = prefs.getString(KEY_CONVERSATIONS, null)
            if (conversationsJson != null) {
                val conversations = json.decodeFromString<List<ChatConversation>>(conversationsJson)
                Log.d(TAG, "Loaded ${conversations.size} conversations")
                conversations
            } else {
                Log.d(TAG, "No saved conversations found")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading conversations", e)
            emptyList()
        }
    }
    
    // Save a single message (for real-time updates)
    fun addMessage(conversationId: String, message: Message) {
        try {
            val currentMessages = loadMessages(conversationId).toMutableList()

            // Check if message already exists (avoid duplicates)
            if (currentMessages.none { it.id == message.id }) {
                currentMessages.add(message)
                saveMessages(conversationId, currentMessages)
                Log.d(TAG, "Added new message ${message.id} to conversation $conversationId. Total messages: ${currentMessages.size}")
            } else {
                Log.d(TAG, "Message ${message.id} already exists in conversation $conversationId")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error adding message ${message.id} to conversation $conversationId", e)
        }
    }
    
    // Update conversation's last message
    fun updateConversationLastMessage(conversationId: String, message: Message) {
        val conversations = loadConversations().toMutableList()
        val conversationIndex = conversations.indexOfFirst { it.id == conversationId }
        
        if (conversationIndex != -1) {
            val updatedConversation = conversations[conversationIndex].copy(
                lastMessage = message,
                lastActivity = message.timestamp
            )
            conversations[conversationIndex] = updatedConversation
            saveConversations(conversations)
            Log.d(TAG, "Updated last message for conversation $conversationId")
        }
    }
    
    // Mark messages as read
    fun markMessagesAsRead(conversationId: String, userId: String) {
        val messages = loadMessages(conversationId).toMutableList()
        var hasChanges = false
        
        for (i in messages.indices) {
            if (messages[i].receiverId == userId && !messages[i].isRead) {
                messages[i] = messages[i].copy(isRead = true)
                hasChanges = true
            }
        }
        
        if (hasChanges) {
            saveMessages(conversationId, messages)
            
            // Update conversation unread count
            val conversations = loadConversations().toMutableList()
            val conversationIndex = conversations.indexOfFirst { it.id == conversationId }
            if (conversationIndex != -1) {
                val unreadCount = messages.count { !it.isRead && it.senderId != userId }
                val updatedConversation = conversations[conversationIndex].copy(
                    unreadCount = unreadCount
                )
                conversations[conversationIndex] = updatedConversation
                saveConversations(conversations)
            }
            
            Log.d(TAG, "Marked messages as read for conversation $conversationId")
        }
    }
    
    // Get all conversation IDs that have saved messages
    fun getAllConversationIds(): Set<String> {
        return prefs.all.keys
            .filter { it.startsWith(KEY_MESSAGES) }
            .map { it.removePrefix(KEY_MESSAGES) }
            .toSet()
    }
    
    // Clear all chat data (for logout or reset)
    fun clearAllChatData() {
        val editor = prefs.edit()
        prefs.all.keys.forEach { key ->
            if (key.startsWith(KEY_MESSAGES) || key == KEY_CONVERSATIONS) {
                editor.remove(key)
            }
        }
        editor.apply()
        Log.d(TAG, "Cleared all chat data")
    }
    
    // Clear data for specific conversation
    fun clearConversation(conversationId: String) {
        prefs.edit()
            .remove(KEY_MESSAGES + conversationId)
            .apply()
        
        // Remove from conversations list
        val conversations = loadConversations().toMutableList()
        conversations.removeAll { it.id == conversationId }
        saveConversations(conversations)
        
        Log.d(TAG, "Cleared data for conversation $conversationId")
    }

    // Comments storage functions
    fun saveComments(postId: String, comments: List<Comment>) {
        try {
            val commentsJson = json.encodeToString(comments)
            prefs.edit()
                .putString(KEY_COMMENTS + postId, commentsJson)
                .apply()
            Log.d(TAG, "💾 Saved ${comments.size} comments for post $postId")
            Log.d(TAG, "💾 Storage key: ${KEY_COMMENTS + postId}")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error saving comments for post $postId", e)
        }
    }

    fun loadComments(postId: String): List<Comment> {
        return try {
            val storageKey = KEY_COMMENTS + postId
            val commentsJson = prefs.getString(storageKey, null)
            Log.d(TAG, "📖 Loading comments for post $postId with key: $storageKey")

            if (commentsJson != null) {
                val comments = json.decodeFromString<List<Comment>>(commentsJson)
                Log.d(TAG, "📖 Loaded ${comments.size} comments for post $postId")
                comments.forEach { comment ->
                    Log.d(TAG, "📖 Comment: ${comment.userName} - ${comment.content}")
                }
                comments
            } else {
                Log.d(TAG, "📖 No saved comments found for post $postId")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error loading comments for post $postId", e)
            emptyList()
        }
    }

    fun addComment(postId: String, comment: Comment) {
        try {
            val currentComments = loadComments(postId).toMutableList()

            // Check if comment already exists (avoid duplicates)
            if (currentComments.none { it.id == comment.id }) {
                currentComments.add(comment)
                saveComments(postId, currentComments)
                Log.d(TAG, "✅ Added new comment ${comment.id} to post $postId. Total comments: ${currentComments.size}")

                // Verify the save worked
                val verifyComments = loadComments(postId)
                Log.d(TAG, "✅ Verification: ${verifyComments.size} comments now saved for post $postId")
            } else {
                Log.d(TAG, "⚠️ Comment ${comment.id} already exists for post $postId")
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error adding comment ${comment.id} to post $postId", e)
        }
    }

    fun clearComments(postId: String) {
        prefs.edit()
            .remove(KEY_COMMENTS + postId)
            .apply()
        Log.d(TAG, "Cleared comments for post $postId")
    }
}
