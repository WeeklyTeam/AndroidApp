package com.ottogo.weekly.api

import android.app.MediaRouteActionProvider
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.HeaderMap

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

    @POST("api/account/verify/")
    suspend fun verify(@HeaderMap header: Map<String, String>, @Body body: Map<String, Int>)
}

object WeeklyApi {
    val retrofitService : WeeklyApiService by lazy { retrofit.create(WeeklyApiService::class.java) }
}
