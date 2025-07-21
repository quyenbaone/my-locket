package com.mylocket.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

enum class FriendStatus {
    SENT,
    FRIENDS,
    RECEIVED
}

@Serializable
data class Friend(
    val id: String = "",
    @SerialName("user_id")
    val userId: String = "",
    @SerialName("friend_id")
    val friendId: String = "",
    @SerialName("friend_name")
    val name: String = "",
    @SerialName("friend_email")
    val email: String = "",
    @SerialName("friend_photo")
    val photo: String? = null,
    val status: String = ""
)
