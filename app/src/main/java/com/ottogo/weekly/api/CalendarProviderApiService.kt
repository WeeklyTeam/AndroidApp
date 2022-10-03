package com.ottogo.weekly.api

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.provider.CalendarContract
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap


private val EVENT_PROJECTION: Array<String> = arrayOf(
    CalendarContract.Events._ID,              // 0
    CalendarContract.Events.TITLE,            // 1
    CalendarContract.Events.DTSTART,           // 2
)

private const val PROJECTION_ID_INDEX: Int = 0
private const val PROJECTION_TITLE_INDEX: Int = 1
private const val PROJECTION_DSTART_INDEX: Int = 2

class CalendarEvent(val id: Long, val title: String, val dstart: Long)

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun CalendarProviderApiService() { // Test Composable
    var events = getCalendarProviderEvents(LocalContext.current)

    LazyColumn(content = {
        events.forEach {
            item {
                Text(text = it.key, style = MaterialTheme.typography.h5)
            }

            items(it.value.count()) { index ->
                Text(text = "${it.value[index].title} at ${getDate(it.value[index].dstart)}")
            }

            item {
                Divider()
            }
        }
    })
}

@RequiresApi(Build.VERSION_CODES.N)
fun getCalendarProviderEvents(context: Context): HashMap<String, MutableList<CalendarEvent>> {

    val callbackId = 42;
    checkPermission(callbackId, context,  Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR)

    val calendarStartTime = System.currentTimeMillis()
    var resolver: ContentResolver = context.contentResolver
    val uri: Uri = CalendarContract.Events.CONTENT_URI


    val selection = "${CalendarContract.Events.DTSTART} > $calendarStartTime"

        // Query to get top N rows from each group - didn't work
        //"exists (select * from (select title, dtstart, _id, row_number() over (partition by title order by dtstart asc) as title_rank from view_events) as ranks where title_rank <= 3 and view_events._id  = ranks._id)"

        // Query to group by title
        //"(${CalendarContract.Events.DTSTART} > ${System.currentTimeMillis()}))) GROUP BY (( ${CalendarContract.Events.TITLE}"

    val cur: Cursor? = resolver.query(uri, EVENT_PROJECTION, selection, null, CalendarContract.Events.DTSTART)



    // Use the cursor to step through the returned records
    val maxRecurring = 3
    var events = hashMapOf<String, MutableList<CalendarEvent>>() // Stores first 3 instances of each event
    if (cur != null) {
        while (cur.moveToNext()) {
            try {
                val id: Long = cur.getLong(PROJECTION_ID_INDEX)
                val title: String = cur.getString(PROJECTION_TITLE_INDEX)
                val dstart: String = cur.getString(PROJECTION_DSTART_INDEX)


                var calendarEvent = CalendarEvent(id, title, dstart.toLong())
                if (events.contains(title)) {
                    if (events[title]!!.count() < maxRecurring) {
                        events[title]!!.add(calendarEvent)
                    }
                }  else {
                    events[title] = mutableListOf(calendarEvent)
                }

            } catch (ex: Exception) {
                Log.e("calendar", ex.toString())
            }
        }
    }

    return events
}


@RequiresApi(Build.VERSION_CODES.N)
fun getDate(milliSeconds: Long): String? {
    val formatter = SimpleDateFormat("MM/dd/yyyy hh:mm:ss.SSS")

    var calendar = Calendar.getInstance()
    calendar.timeInMillis = milliSeconds
    return formatter.format(calendar.time)
}


private fun checkPermission(callbackId: Int, context: Context, vararg permissionsId: String) {
    var permissions = true
    for (p in permissionsId) {
        permissions =
            permissions && ContextCompat.checkSelfPermission(context, p) == PERMISSION_GRANTED
    }
    if (!permissions) ActivityCompat.requestPermissions(context as Activity, permissionsId, callbackId)
}