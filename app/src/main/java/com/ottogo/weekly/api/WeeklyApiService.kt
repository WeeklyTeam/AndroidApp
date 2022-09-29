package com.ottogo.weekly.api

import android.app.MediaRouteActionProvider
import com.ottogo.weekly.api.models.*
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*
import java.lang.reflect.Modifier
import java.util.*


private val BASE_URL = "https://www.theweeklyapp.com"

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

    @GET("api/plot/{id}/going/")
    suspend fun acceptPlotInvitation(@HeaderMap header: Map<String, String>, @Path("id") id: Int)
    @GET("api/plot/{id}/notgoing/")
    suspend fun rejectPlotInvitation(@HeaderMap header: Map<String, String>, @Path("id") id: Int)

    @GET("api/profile/{id}/add/")
    suspend fun add(@HeaderMap header: Map<String, String>, @Path("id") id: Int)
    @GET("api/profile/{id}/accept/")
    suspend fun accept(@HeaderMap header: Map<String, String>, @Path("id") id: Int)
    @GET("api/profile/{id}/reject/")
    suspend fun reject(@HeaderMap header: Map<String, String>, @Path("id") id: Int)
    @GET("api/profile/{id}/block/")
    suspend fun block(@HeaderMap header: Map<String, String>, @Path("id") id: Int)
    @GET("api/profile/{id}/report/")
    suspend fun report(@HeaderMap header: Map<String, String>, @Path("id") id: Int)
    @GET("api/profile/{id}/unblock/")
    suspend fun unblock(@HeaderMap header: Map<String, String>, @Path("id") id: Int)

    @Multipart
    @JvmSuppressWildcards
    @POST("api/group/")
    suspend fun createGroup(
        @HeaderMap headers: Map<String, String>,
        @PartMap partMap: Map<String, RequestBody>,
        @Part image: MultipartBody.Part? = null
    ): Group

    @Multipart
    @JvmSuppressWildcards
    @PATCH("api/group/{id}/")
    suspend fun editGroupImage(
        @HeaderMap headers: Map<String, String>,
        @Path("id") id: Int,
        @PartMap partMap: Map<String, RequestBody>,
        @Part profile_picture: MultipartBody.Part? = null
    ): Group

    @JvmSuppressWildcards
    @PATCH("api/group/{id}/")
    suspend fun patchGroup(@HeaderMap header: Map<String, String>, @Path("id") id: Int, @Body body: Map<String, Any>)

    @GET("api/group/{id}/leave/")
    suspend fun leaveGroup(@HeaderMap header: Map<String, String>, @Path("id") id: Int)
    @GET("api/group/{id}/accept/")
    suspend fun acceptGroupInvite(@HeaderMap header: Map<String, String>, @Path("id") id: Int)

    @Multipart
    @JvmSuppressWildcards
    @POST("api/group/message/")
    suspend fun uploadGroupImage(
        @HeaderMap headers: Map<String, String>,
        @Part("group") group: Int,
        @Part("user_id") user_id: Int,
        @Part("message") message: String?,
        @Part image: MultipartBody.Part? = null
    ): ChatMessage

    @Multipart
    @JvmSuppressWildcards
    @POST("api/chat/message/")
    suspend fun uploadChatImage(
        @HeaderMap headers: Map<String, String>,
        @Part("private") private: Int,
        @Part("user_id") user_id: Int,
        @Part("message") message: String?,
        @Part image: MultipartBody.Part? = null
    ): ChatMessage

    @JvmSuppressWildcards
    @POST("api/plot/")
    suspend fun createPlot(
        @HeaderMap headers: Map<String, String>,
        @Body body: Map<String, Any?>,
    ): Plot

    @JvmSuppressWildcards
    @GET("api/plot/")
    suspend fun getStatusesAndAdventures(
        @HeaderMap headers: Map<String, String>,
        @Query("date", encoded = true) date: String,
        @Query("time", encoded = true) time: String,
    ): StatusesAndAdventures

    @JvmSuppressWildcards
    @PATCH("api/plot/{id}/")
    suspend fun patchPlot(@HeaderMap header: Map<String, String>, @Path("id") id: Int, @Body body: Map<String, Any>)

    @DELETE("api/plot/{id}/")
    suspend fun deletePlot(@HeaderMap header: Map<String, String>, @Path("id") id: Int)

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

    @GET("api/profile/invite/{phoneNumber}/")
    suspend fun inviteContact(@HeaderMap header: Map<String, String>, @Path("phoneNumber") phoneNumber: String)

    @DELETE("api/account/delete/")
    suspend fun deleteAccount(@HeaderMap header: Map<String, String>)
}

object WeeklyApi {
    val retrofitService : WeeklyApiService by lazy { retrofit.create(WeeklyApiService::class.java) }
}
