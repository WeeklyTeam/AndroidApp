package com.ottogo.weekly.api.models

import com.squareup.moshi.Json
import java.util.*

data class ModifiedPlot(
    @Json(name = "emoji") val emoji: String,
    @Json(name = "name") val name: String,
    @Json(name = "user_id") val user_id: Int,
    @Json(name = "starttime") val starttime: Date,
    @Json(name = "endtime") val endtime: Date?,

    )

data class StatusesAndAdventures(
    @Json(name = "statuses") val statuses: Any,
    @Json(name = "adventures") val adventures: Any

    )
