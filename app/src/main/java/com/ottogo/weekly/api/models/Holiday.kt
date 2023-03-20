package com.ottogo.weekly.api.models

import com.squareup.moshi.Json

data class Holiday(
    @Json(name = "id") val id: Int,
    @Json(name = "holiday_name") val holiday_name: String,
    @Json(name = "emoji") val emoji: String,
    @Json(name = "day_of_month") val day_of_month: Int? = null,
    @Json(name = "month_of_year") val month_of_year: Int? = null,
    @Json(name = "day_of_week") val day_of_week: Int? = null,
    @Json(name = "week_of_month") val week_of_month: Int? = null,
    @Json(name = "date") val date: String,
    @Json(name = "active") val active: Boolean,
)