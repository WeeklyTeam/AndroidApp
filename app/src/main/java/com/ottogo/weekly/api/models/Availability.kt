package com.ottogo.weekly.api.models

import java.util.*

data class Availability(
    val id: Int,
    val user_id: Int,
    val bust: Boolean = true,
    val starttime: Date,
    val endtime: Date,
    val title: String,
    val days_of_week: List<Int>
)
