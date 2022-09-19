package com.ottogo.weekly.viewmodels

import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.AccountManagerCallback
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ottogo.weekly.MainActivity
import com.ottogo.weekly.api.ActivityCategory
import com.ottogo.weekly.api.WeeklyApi
import com.ottogo.weekly.api.WeeklyApiService
import com.ottogo.weekly.api.models.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

class UserViewModel: ViewModel() {

    var token by mutableStateOf<String?>(null)
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
    private val _requests = mutableStateListOf<Profile>()
    val requests: List<Profile>
        get() = _requests




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

    fun removeGroup(groupId: Int){
        _chats.removeIf {
            it is Group && it.id == groupId
        }
        _groups.remove(groupId)
    }

    fun removeCalendar(index: Int){
        _calendars.removeAt(index)
    }

    fun addAvailability(availability: Availability){
        _availability.add(availability)
    }

    fun removeAvailability(availability: Availability){
        _availability.remove(availability)
    }

    fun addCalendar(calendar: FriendCalendar){
        _calendars.add(calendar)
    }

    suspend fun getMain(){
        val data = WeeklyApi.retrofitService.main(mapOf("Authorization" to "token $token"))
        Log.d("ViewModel", data.toString())


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
        _requests.addAll(data.requests)
        _calendars.addAll(data.calendars)

        _plots.addAll(data.plots)

    }

    fun clear() {
        _friends.clear()
        _chats.clear()
        _requests.clear()
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

    fun inviteToGroup(groupId: Int, selectedIds: List<Int>){
        var tempGroup = _groups[groupId]
        val invitees = (tempGroup?.invited ?: listOf<Profile>()).toMutableList()
        for (id in selectedIds) {
            _friends[id]?.let { invitees.add(it) }
        }
        tempGroup = tempGroup?.copy(invited = invitees)
        _groups.put(groupId, tempGroup!!)
    }

    fun updateGroup(groupId: Int, group: Group){

        _groups.put(groupId, group)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun removeFriend(userId: Int){
        _chats.removeIf {
            it is Profile && it.user_id == userId
        }
        _friends.remove(userId)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun removeRequest(userId: Int){
        _requests.removeIf {
            it is Profile && it.user_id == userId
        }
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

    

    fun addMessage(message: ChatMessage){
        if (message.recipient != null) {

            addPrivateMessage(message = message)
        } else {
            addGroupMessage(message = message)
        }

    }


    suspend fun acceptPlotInvite(plot: Plot){

        WeeklyApi.retrofitService.acceptPlotInvitation(mapOf("Authorization" to "token ${token}"), plot.id)
        _plots.remove(plot)
        var newInvitedList = plot.invited
        var newGoingList = plot.going
        newInvitedList.toMutableList().removeIf { it.user_id == profile?.user_id }
        profile?.let { newGoingList.toMutableList().add(it) }

        _plots.add(plot.copy(is_going = true, invited = newInvitedList, going = newGoingList))

    }

    suspend fun rejectPlotInvite(plot: Plot){
        WeeklyApi.retrofitService.rejectPlotInvitation(mapOf("Authorization" to "token ${token}"), plot.id)
        _plots.remove(plot)

    }

    fun removePlot(plotId: Int){
        _plots.removeAll { it.id == plotId }

    }

    fun updatePlot(plotId: Int, plot: Plot){
        _plots[_plots.indexOfFirst { plotId == it.id }] = plot
    }

    fun addCalendarAvailabilities(selectedCalendar: Int, availabilities: List<Availability>) {
        val newCalendar = _calendars[selectedCalendar].copy(availabilities = availabilities)
        _calendars[selectedCalendar] = newCalendar
    }

    fun seenChatMessages(userId: Int? = null, groupId: Int? = null){
        if (userId != null) {
            _chats.replaceAll {
                if (it is Profile && it.user_id == userId){
                    val messages = it.messages.toMutableList()
                    messages.replaceAll { it.copy(seen = true) }
                    it.copy(messages = messages)
                }
                else {
                    it
                }
            }
        } else {
            _chats.replaceAll {
                if (it is Group && it.id == groupId){
                    val messages = it.messages.toMutableList()
                    messages.replaceAll { it.copy(seen = true) }
                    it.copy(messages = messages)
                }
                else {
                    it
                }
            }
        }
    }

    val ACCOUNT_TYPE = "com.ottogo.weekly"

    fun getToken(context: Context) {
        val accountManager = AccountManager.get(context)
        try {


            val account = accountManager.getAccountsByType(ACCOUNT_TYPE)[0]

            accountManager.getAuthToken(account, ACCOUNT_TYPE, null, false, AccountManagerCallback {
                //val token = it.getResult()
//                    token = token
                Log.d("tokenGet", it.getResult().toString())
                Log.d("tokenGet", account.toString())
                Log.d("tokenGet", it.isDone.toString())
                token = it.getResult().get("authtoken") as String


                viewModelScope.launch {
                    getMain()

                }
            }, null)


        } catch (ignored: Exception) {
            Log.d("tokenGet", ignored.toString())


        }
    }

    fun setToken(username: String, token: String, context: Context){
        Log.d("tokenSet", token)
        val accountManager = AccountManager.get(context)
        val account = Account(username, ACCOUNT_TYPE)
        accountManager.addAccountExplicitly(account, "", null);
        accountManager.setAuthToken(account, ACCOUNT_TYPE, token)
        this.token = token
        viewModelScope.launch {
            getMain()
        }
    }

    fun logout(context: Context){
        val accountManager = AccountManager.get(context)
        val account = accountManager.getAccountsByType(ACCOUNT_TYPE)[0]
        accountManager.removeAccount(account, AccountManagerCallback {
            profile = null
            _friends.clear()
            _chats.clear()
            _groups.clear()
            _calendars.clear()
            _availability.clear()
            _plots.clear()
            _requests.clear()
            token = null

        }, null)

    }

}