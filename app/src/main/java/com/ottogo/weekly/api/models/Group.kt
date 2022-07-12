package com.ottogo.weekly.api.models

import com.squareup.moshi.Json
import java.util.*

data class Group (@Json(name = "id") var id: Int,
                  @Json(name= "name") var name: String,
                  @Json(name = "image") var image: String?,
                  @Json(name = "messages") var messages: List<ChatMessage> = listOf(),
                  @Json(name = "seen") var seen: Boolean = false,
                  @Json(name = "members") var members: List<Profile>,
                  @Json(name = "timestamp") var timestamp: Date = Date(),

                  )