package com.ottogo.weekly.api.models

import com.squareup.moshi.Json
import java.util.*

data class Profile (@Json(name = "user_id") val user_id: Int,
                    @Json(name= "name") val name: String,
                    @Json(name = "username") val username: String,
                    @Json(name = "profile_picture") val profile_picture: String?,
                    @Json(name = "messages") val messages: List<ChatMessage> = listOf(),
                    @Json(name = "requesting") var requesting: Boolean?,
                    @Json(name = "urequested") var urequested: Boolean?,
                    @Json(name = "friend") var friend: Boolean?,
                    @Json(name = "blocked") var blocked: Boolean?,
                    @Json(name = "timestamp") val timestamp: Date = Date(),
                    @Json(name = "relationship_id") val relationship_id: Int?,
                    )