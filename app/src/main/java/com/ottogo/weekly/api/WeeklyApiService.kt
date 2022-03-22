package com.ottogo.weekly.api

import android.app.MediaRouteActionProvider
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.HeaderMap

data class Activity(@Json(name="id")var id: Int, @Json(name="activity")var activity: String)
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

    @GET("api/activity/1/unfavorite/")
    suspend fun unfavoriteActivity(@HeaderMap header: Map<String, String>)

    @GET("api/activity/1/favorite/")
    suspend fun favoriteActivity(@HeaderMap header: Map<String, String>)
}

object WeeklyApi {
    val retrofitService : WeeklyApiService by lazy { retrofit.create(WeeklyApiService::class.java) }
}
