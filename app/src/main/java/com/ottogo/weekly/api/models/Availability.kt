package com.ottogo.weekly.api.models

import com.squareup.moshi.Json
import java.time.LocalDateTime

data class Availability(@Json(name = "user_id") var user_id: Int,
                        @Json(name= "available") var available: Boolean=false,
                        @Json(name = "starttime") var starttime: LocalDateTime,
                        @Json(name = "endtime") var endtime: LocalDateTime,
)
