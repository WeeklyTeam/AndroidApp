package com.ottogo.weekly.api.models

import com.squareup.moshi.Json

data class Group (@Json(name = "id") var id: Int,
                    @Json(name= "name") var name: String,
                    @Json(name = "image") var image: String?,
                    @Json(name = "messages") var messages: List<ChatMessage> = listOf(),
                    
)