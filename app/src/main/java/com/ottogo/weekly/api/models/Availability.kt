package com.ottogo.weekly.api.models

import com.squareup.moshi.Json
import java.util.*

data class Availability(
    @Json(name = "id") val id: Int,
    @Json(name = "user_id") val user_id: Int,
    @Json(name = "busy") val busy: Boolean = true,
    @Json(name = "starttime") val starttime: Date,
    @Json(name = "endtime") val endtime: Date,
    @Json(name = "title") val title: String,
)
