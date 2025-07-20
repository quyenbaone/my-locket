package com.mylocket.data

import kotlinx.serialization.Serializable

enum class FriendStatus {
    SENT,
    FRIENDS,
    RECEIVED
}

@Serializable
data class Friend(
    var status: String = "",
    var id: String = "",
    var name: String = "",
    var email: String = "",
    var photo: String? = null
)
