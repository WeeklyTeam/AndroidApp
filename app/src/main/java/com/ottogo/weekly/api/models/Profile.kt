package com.ottogo.weekly.api.models

import com.squareup.moshi.Json

data class Profile (@Json(name = "user_id") var user_id: Int,
                    @Json(name= "name") var name: String,
                    @Json(name = "username") var username: String,
                    @Json(name = "profile_picture") var profile_picture: String,
                    @Json(name = "requesting") var requesting: Boolean?,
                    @Json(name = "urequested") var urequested: Boolean?,
                    @Json(name = "blocked") var blocked: Boolean?,
                    @Json(name = "friend") var friend: Boolean?,
                    @Json(name = "messages") var messages: List<ChatMessage>,
                    )
