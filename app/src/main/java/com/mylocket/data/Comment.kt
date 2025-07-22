package com.mylocket.data

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Comment(
    val id: String = "",
    @SerialName("post_id")
    val postId: String = "",
    @SerialName("user_id")
    val userId: String = "",
    @SerialName("user_name")
    val userName: String = "",
    val content: String = "",
    val time: Instant? = null,
    @SerialName("created_at")
    val createdAt: Instant? = null,
    @SerialName("updated_at")
    val updatedAt: Instant? = null
)
