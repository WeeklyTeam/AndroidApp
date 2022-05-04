package com.ottogo.weekly.api

import android.app.MediaRouteActionProvider
import android.app.appsearch.SearchResult
import com.ottogo.weekly.api.models.ApiList
import com.ottogo.weekly.api.models.Profile
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

data class Activity(@Json(name="id")var id: Int)

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
    @POST("api/account/create/")
    suspend fun signup(@Body body: Map<String, String>) : Map<String, String>

    @GET("api/profile/search")
    suspend fun search(@HeaderMap header: Map<String, String>, @Query("search") search: String): ApiList<Profile>

    @PATCH("api/plot/")
    suspend fun editPlot(@HeaderMap header: Map<String, String>)
}

object WeeklyApi {
    val retrofitService : WeeklyApiService by lazy { retrofit.create(WeeklyApiService::class.java) }
}
