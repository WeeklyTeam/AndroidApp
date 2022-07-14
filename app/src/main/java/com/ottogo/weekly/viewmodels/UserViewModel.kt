package com.ottogo.weekly.viewmodels

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ottogo.weekly.api.ActivityCategory
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.api.WeeklyApiService
import com.ottogo.weekly.api.models.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

class UserViewModel(): ViewModel() {

    var token by mutableStateOf<String?>("8375e2ec5ea97021bcf0ecb5bad9304cce0b6ef7")
    var profile by mutableStateOf<Profile?>(null)
    private val _friends = mutableStateMapOf<Int, Profile>()
    val friends: Map<Int, Profile>
        get() = _friends
    private val _chats = mutableStateListOf<Any>()
    val chats: List<Any>
        get() = _chats
    private val _groups = mutableStateMapOf<Int, Group>()
    val groups: Map<Int, Group>
        get() = _groups
    private val _calendars = mutableStateListOf<FriendCalendar>()
    val calendars: List<FriendCalendar>
        get() = _calendars
    private val _availability = mutableStateListOf<Availability>()
    val availability: List<Availability>
        get() = _availability
    private val _plots = mutableStateListOf<Plot>()
    val plots: List<Plot>
        get() = _plots
    var requests by mutableStateOf<MutableList<Profile>>(mutableListOf())




    @JvmName("setToken1")
    fun setToken(string: String){
        viewModelScope.launch {
            token = string
            getMain()

        }
    }

    fun addGroup(group: Group){
        _chats.add(0, group)
        _groups[group.id] = group
    }

    fun addAvailability(availability: Availability){
        _availability.add(availability)
    }

    fun addCalendar(calendar: FriendCalendar){
        _calendars.add(calendar)
    }

    suspend fun getMain(){
        val data = WeeklyApi.retrofitService.main(mapOf("Authorization" to "token $token"))
        Log.d("ViewModel", data.toString())

        clear()

        profile = data.profile

        for (friend in data.friends) {
            _friends[friend.user_id] = friend
            _chats.add(friend)
        }

        for (group in data.groups) {
            _groups[group.id] = group
            _chats.add(group)
        }

        _chats.sortByDescending {
            when(it) {
                is Group -> it.messages.firstOrNull()?.timestamp ?: it.timestamp
                is Profile -> it.messages.firstOrNull()?.timestamp ?: it.timestamp
                else -> {
                    Date()
                }
            }
        }

        _availability.addAll(data.availability)
        requests = data.requests.toMutableList()
        _calendars.addAll(data.calendars)

        _plots.addAll(data.plots)

    }

    private fun clear() {
        _friends.clear()
        _plots.clear()
        _groups.clear()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun addPrivateMessage(message: ChatMessage){

        val recipientId: Int = if (message.recipient != profile?.user_id) {
            message.recipient!!
        } else {
            message.user_id
        }

        _chats.removeAll { it is Profile && it.user_id == recipientId }

        var temporaryProfile = _friends[recipientId]
        val messages = temporaryProfile?.messages?.toMutableList() ?: mutableListOf()
        messages.add(0, message)

        temporaryProfile = temporaryProfile?.copy(messages = messages.toList())
        _friends.remove(recipientId)
        _friends[recipientId] = temporaryProfile!!

        _chats.add(0, temporaryProfile!!)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun removeFriend(userId: Int){
        _chats.removeIf {
            it is Profile && it.user_id == userId
        }
        _friends.remove(userId)
    }

    fun addFriend(profile: Profile){
        _chats.add(0, profile)
        _friends[profile.user_id] = profile
    }

    fun addPlot(plot: Plot){
        _plots.add(plot)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun addGroupMessage(message: ChatMessage){

        _chats.removeAll { it is Group && it.id == message.group }

        val temporaryGroup = _groups[message.group]
        val messages = temporaryGroup?.messages?.toMutableList() ?: mutableListOf()
        messages.add(0, message)

        temporaryGroup?.messages = messages.toList()
        _groups.remove(message.group)
        _groups[message.group!!] = temporaryGroup!!

        _chats.add(0, temporaryGroup!!)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    suspend fun leaveGroup(group: Group){
        WeeklyApi.retrofitService.leaveGroup(mapOf("Authorization" to "token $token"), group.id)
        _groups.remove(group.id)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun addMessage(message: ChatMessage){
        if (message.recipient != null) {
            addPrivateMessage(message = message)
        } else {
            addGroupMessage(message = message)
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    suspend fun acceptPlotInvite(plot: Plot){
        WeeklyApi.retrofitService.acceptPlotInvitation(mapOf("Authorization" to "token $token"), plot.id)
        _plots.remove(plot)
        var newInvitedList = plot.invited
        var newGoingList = plot.going
        newInvitedList.toMutableList().removeIf { it.user_id == profile?.user_id }
        profile?.let { newGoingList.toMutableList().add(it) }

        _plots.add(plot.copy(is_going = true, invited = newInvitedList, going = newGoingList))

    }

    suspend fun rejectPlotInvite(plot: Plot){
        WeeklyApi.retrofitService.rejectPlotInvitation(mapOf("Authorization" to "token $token"), plot.id)
        _plots.remove(plot)
    }

}