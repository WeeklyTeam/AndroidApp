package com.ottogo.weekly.api


import android.provider.ContactsContract
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    @Multipart
    @JvmSuppressWildcards
    @PATCH("api/profile/myprofile/")
    suspend fun patchProfile(
        @HeaderMap headers: Map<String, String>,
        @PartMap partMap: Map<String, RequestBody>,
        @Part profile_picture: MultipartBody.Part? = null
    ): ContactsContract.Profile
}

object WeeklyApi {
    val retrofitService : WeeklyApiService by lazy { retrofit.create(WeeklyApiService::class.java) }
}
