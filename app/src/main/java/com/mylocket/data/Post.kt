package com.mylocket.data

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Post(
    val id: String = "",
    @SerialName("user_id")
    val userId: String = "",
    val content: String = "",
    val photo: String = "",
    val time: Instant? = null,
    @SerialName("to_who")
    val toWho: List<String> = emptyList()
)
