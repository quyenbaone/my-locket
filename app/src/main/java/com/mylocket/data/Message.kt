package com.mylocket.data

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val messageType: MessageType = MessageType.TEXT
)

@Serializable
enum class MessageType {
    TEXT,
    IMAGE,
    SYSTEM
}

@Serializable
data class ChatConversation(
    val id: String = "",
    val participants: List<String> = emptyList(),
    val lastMessage: Message? = null,
    val lastActivity: Long = System.currentTimeMillis(),
    val unreadCount: Int = 0
)

@Serializable
data class ChatUser(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val isOnline: Boolean = false,
    val lastSeen: Long = System.currentTimeMillis()
)
