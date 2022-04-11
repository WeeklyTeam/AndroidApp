package com.ottogo.weekly.api

import android.app.MediaRouteActionProvider
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.ottogo.weekly.R
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

data class Activity(@Json(name="id")var id: Int, @Json(name="activity")var activity: String, var isSelected: Boolean = false)
data class ActivityCategory(@Json(name="title")var title: String, @Json(name="activities")var activities: List<Activity>)

private val BASE_URL = "https://plotsme.herokuapp.com"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface WeeklyApiService {
    @POST("api/account/login/")
    suspend fun login(@Body body: Map<String, String>)

    @GET("api/activities/")
    suspend fun activity(@HeaderMap header: Map<String, String>):List<ActivityCategory>

    @GET("api/activity/{id}/unfavorite/")
    suspend fun unfavoriteActivity(@HeaderMap header: Map<String, String>, @Path("id") id: Int)

    @GET("api/activity/{id}/favorite/")
    suspend fun favoriteActivity(@HeaderMap header: Map<String, String>, @Path("id") id: Int)
}

object WeeklyApi {
    val retrofitService : WeeklyApiService by lazy { retrofit.create(WeeklyApiService::class.java) }
}
