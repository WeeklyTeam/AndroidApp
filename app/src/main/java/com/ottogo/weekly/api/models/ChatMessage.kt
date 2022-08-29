package com.ottogo.weekly.api.models

import com.squareup.moshi.Json
import java.util.*

data class ChatMessage(@Json(name = "user_id") val user_id: Int,
                       @Json(name= "message") val message: String? = null,
                       @Json(name = "seen") val seen: Boolean = false,
                       @Json(name = "timestamp") val timestamp: Date = Date(),
                       @Json(name="group") val group: Int? = null,
                       @Json(name="recipient") val recipient: Int? = null,
                       @Json(name= "gif") val gif: String? = null
)
