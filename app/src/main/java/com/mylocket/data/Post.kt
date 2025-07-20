package com.mylocket.data

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val id: String = "",
    val content: String = "",
    val time: Instant? = null,
    val photo: String = "",
    val toWho: List<String> = emptyList(),
    val userId: String = ""
)
