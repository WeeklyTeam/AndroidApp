package com.ottogo.weekly.alarms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ottogo.weekly.R
import com.ottogo.weekly.ui.calendar.DateFunctions.isSameDay
import java.text.SimpleDateFormat
import java.util.*


class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val plotName = intent?.extras!!.getString("plot")
        val starttime = intent?.extras!!.getString("starttime")
        val calendar = Calendar.getInstance()
        Log.d("alarmStatus", "Alarm receiver created with starttime: ${starttime}")
        val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy")
        var title: String
        if (isSameDay(sdf.parse(starttime.toString()), Date())){
            title = "$plotName starts in 2 hours"
        } else {
            title = plotName.toString() + " is tomorrow at " + SimpleDateFormat("h:mma").format(sdf.parse(starttime.toString()))
        }


        var builder = NotificationCompat.Builder(context!!, "WeeklyPlotReminders")
            .setSmallIcon(R.drawable.ic_calendar_event_line)
            .setContentTitle(title)
            .setContentText("Get ready!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(123, builder.build())
    }

}