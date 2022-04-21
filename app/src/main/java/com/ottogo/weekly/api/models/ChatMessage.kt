package com.ottogo.weekly.api.models

import com.squareup.moshi.Json
import java.util.*

data class ChatMessage (@Json(name = "user_id") var user_id: Int,
                    @Json(name= "message") var message: String,
                    @Json(name = "seen") var seen: Boolean,
                    @Json(name = "timestamp") var timestamp: Date = Date(),
)
