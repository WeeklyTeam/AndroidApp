package com.ottogo.weekly.api.models

import com.squareup.moshi.Json

data class FriendCalendar (
    @Json(name = "user_id") val user_id: Int?,
    @Json(name = "group_id") val group_id: Int?,
    @Json(name = "relationship_id") val relationship_id: Int?,
    @Json(name = "name") val name: String,
    @Json(name = "availabilities") val availabilities: List<Availability>?
)