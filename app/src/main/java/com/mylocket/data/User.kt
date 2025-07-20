package com.mylocket.data

import kotlinx.serialization.Serializable

@Serializable
open class User(
    var id: String = "",
    var name: String = "",
    var email: String = "",
    var photo: String? = null
)
