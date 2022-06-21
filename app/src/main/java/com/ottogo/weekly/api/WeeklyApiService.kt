package com.ottogo.weekly.api

import android.app.MediaRouteActionProvider
import com.ottogo.weekly.api.models.*
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*
import java.util.*


private val BASE_URL = "https://plotsme.herokuapp.com"

data class Activity(@Json(name="id")val id: Int, @Json(name="activity")val activity: String, @Json(name = "liked") val liked: Boolean = false)
data class ActivityCategory(@Json(name="title")val title: String, @Json(name="activities")val activities: List<Activity>)

private val moshi = Moshi.Builder()
    .add(Date::class.java, Rfc3339DateJsonAdapter())
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface WeeklyApiService {
    @JvmSuppressWildcards
    @POST("api/calendar/")
    suspend fun createCalendar(@HeaderMap header: Map<String, String>, @Body body: Map<String, Any>)



    @GET("api/main")
    suspend fun main(@HeaderMap header: Map<String, String>): Main

    @JvmSuppressWildcards
    @POST("api/report/")
    suspend fun report(@HeaderMap header: Map<String, String>, @Body body: Map<String, Any>)

    @GET("api/profile/search")
    suspend fun search(@HeaderMap header: Map<String, String>, @Query("search") search: String): ApiList<Profile>

    @POST("api/account/login/")
    suspend fun login(@Body body: Map<String, String>): Map<String, String>

    @POST("api/account/verify/")
    suspend fun verify(@HeaderMap header: Map<String, String>, @Body body: Map<String, Int>)

    @GET("api/account/verify/")
    suspend fun createCode(@HeaderMap header: Map<String, String>)

    @POST("api/account/create/")
    suspend fun signup(@Body body: Map<String, String>): Map<String, Any>

    @Multipart
    @JvmSuppressWildcards
    @PATCH("api/profile/myprofile/")
    suspend fun patchProfile(
        @HeaderMap headers: Map<String, String>,
        @PartMap partMap: Map<String, RequestBody>,
        @Part profile_picture: MultipartBody.Part? = null
    ): Profile

    @Multipart
    @JvmSuppressWildcards
    @POST("api/group/")
    suspend fun createGroup(
        @HeaderMap headers: Map<String, String>,
        @PartMap partMap: Map<String, RequestBody>,
        @Part image: MultipartBody.Part? = null
    ): Group

    @GET("api/activities/")
    suspend fun activity(@HeaderMap header: Map<String, String>):List<ActivityCategory>

    @POST("api/activities/create/")
    suspend fun createActivity(@HeaderMap header: Map<String, String>, @Body body: Map<String, String>)

    @GET("api/activity/{id}/unfavorite/")
    suspend fun unfavoriteActivity(@HeaderMap header: Map<String, String>, @Path("id") id: Int)

    @GET("api/activity/{id}/favorite/")
    suspend fun favoriteActivity(@HeaderMap header: Map<String, String>, @Path("id") id: Int)

    @JvmSuppressWildcards
    @POST("api/profile/contacts/")
    suspend fun searchContacts(@HeaderMap header: Map<String, String>, @Body body: Map<String, List<String>>): List<Profile>

    @JvmSuppressWildcards
    @POST("api/availability/")
    suspend fun addAvailability(@HeaderMap header: Map<String, String>, @Body body: Map<String, Any>): Availability
}

object WeeklyApi {
    val retrofitService : WeeklyApiService by lazy { retrofit.create(WeeklyApiService::class.java) }
}
