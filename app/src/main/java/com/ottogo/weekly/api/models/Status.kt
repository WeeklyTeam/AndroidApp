package com.ottogo.weekly.api.models

import com.squareup.moshi.Json
import java.util.*

data class Status(
//    @Json(name = "id") val id: Int,
    @Json(name = "emoji") val emoji: String,
    @Json(name = "title") val title: String,

)
