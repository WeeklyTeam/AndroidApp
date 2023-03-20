package com.ottogo.weekly.api.models

import com.squareup.moshi.Json

// Similar to Activity/ActivityCategory
class HolidayCategory(
    @Json(name="holiday_category_name") val holiday_category_name: String,
    @Json(name="holidays") val holidays: List<Holiday>
)