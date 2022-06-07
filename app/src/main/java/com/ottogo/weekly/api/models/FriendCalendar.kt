package com.ottogo.weekly.api.models

data class FriendCalendar (
    val user_id: Int?,
    val group_id: Int?,
    val relationship_id: Int?,
    val name: String,
    val availabilities: List<Availability>?
)