package com.ottogo.weekly.api.models

import com.squareup.moshi.Json
import java.util.*

data class Profile (@Json(name = "user_id") val user_id: Int,
                    @Json(name= "name") val name: String,
                    @Json(name = "username") val username: String,
                    @Json(name = "profile_picture") val profile_picture: String?,
                    @Json(name = "messages") var messages: List<ChatMessage> = listOf(),
                    @Json(name = "requesting") val requesting: Boolean?,
                    @Json(name = "urequested") val urequested: Boolean?,
                    @Json(name = "friend") val friend: Boolean?,
                    @Json(name = "blocked") val blocked: Boolean?,
                    @Json(name = "timestamp") val timestamp: Date = Date(),
                    @Json(name = "relationship_id") val relationship_id: Int?,
                    )