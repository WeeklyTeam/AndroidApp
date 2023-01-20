package com.ottogo.weekly.api.models

import com.squareup.moshi.Json

data class Main (
    @Json(name = "profile") var profile: Profile,
    @Json(name = "friends") var friends: List<Profile>,
    @Json(name = "requests") var requests: List<Profile>,
    @Json(name = "groups") var groups: List<Group>,
    @Json(name = "plots") var plots: List<Plot>,
    @Json(name = "recommendations") var recommendations: List<Plot>,
    )