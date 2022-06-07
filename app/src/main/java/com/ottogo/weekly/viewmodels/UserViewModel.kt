package com.ottogo.weekly.viewmodels

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ottogo.weekly.api.ActivityCategory
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.api.WeeklyApiService
import com.ottogo.weekly.api.models.*
import kotlinx.coroutines.launch

class UserViewModel(): ViewModel() {

    var token by mutableStateOf<String?>("8375e2ec5ea97021bcf0ecb5bad9304cce0b6ef7")
    var profile by mutableStateOf<Profile?>(null)
    var friendsOrder by mutableStateOf<MutableList<Int>?>(null)
    var friends by mutableStateOf<MutableMap<Int, Profile>?>(null)
    var groupsOrder by mutableStateOf<MutableList<Int>?>(null)
    var groups by mutableStateOf<MutableMap<Int, Group>?>(null)
    var calendars by mutableStateOf<MutableList<FriendCalendar>?>(null)
    var availability by mutableStateOf<MutableList<Availability>?>(null)
    var plots by mutableStateOf<MutableList<Plot>?>(null)
    var requests by mutableStateOf<MutableList<Profile>>(mutableListOf())

    init {
        viewModelScope.launch {
            getMain()

        }
    }

    suspend fun getMain(){
        val data = WeeklyApi.retrofitService.main(mapOf("Authorization" to "token $token"))
        Log.d("ViewModel", data.toString())

        profile = data.profile

        friends = mutableMapOf()
        friendsOrder = mutableListOf()
        for (friend in data.friends) {
            friends!![friend.user_id] = friend
            friendsOrder!!.add(friend.user_id)
        }
        Log.d("friends", friends.toString())
        Log.d("friends", friendsOrder.toString())

        groups = mutableMapOf()
        groupsOrder = mutableListOf()
        for (group in data.groups) {
            groups!![group.id] = group
            groupsOrder!!.add(group.id)
        }

        requests = data.requests.toMutableList()
        calendars = data.calendars.toMutableList()
        plots = data.plots.toMutableList()

    }

    fun sendMessage(recipientId: Int, message: ChatMessage){

        friendsOrder?.remove(recipientId)
        friendsOrder?.add(0, recipientId)

        val temporaryProfile = friends?.get(recipientId)
        var messages = temporaryProfile?.messages?.toMutableList() ?: mutableListOf()
        messages.add(0, message)
        Log.d("FRIENDS", messages.toString())

        temporaryProfile?.messages = messages.toList()
        Log.d("FRIENDS", temporaryProfile.toString())
        friends!![recipientId] = temporaryProfile!!
        friends = friends


    }

}