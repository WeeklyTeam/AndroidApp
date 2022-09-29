package com.ottogo.weekly.ui.calendar.DateFunctions

import java.util.*


fun addMonth(date: Date, amount: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.time = date
    calendar.add(Calendar.MONTH, amount)
    return calendar.time
}


fun addDay(date: Date, amount: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.time = date
    calendar.add(Calendar.DATE, amount)
    return calendar.time
}

fun setDay(date: Date, day: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.time = date
    calendar[Calendar.DAY_OF_MONTH] = day

    return calendar.time
}

fun initialMonth(): Date {
    val calendar = Calendar.getInstance()
    calendar.time = Date()
    // set day to minimum
    // set day to minimum
    calendar[Calendar.DAY_OF_MONTH] = calendar.getActualMinimum(Calendar.DAY_OF_MONTH)
    calendar[Calendar.HOUR_OF_DAY] = 0
    calendar[Calendar.MINUTE] = 0
    calendar[Calendar.SECOND] = 0
    calendar[Calendar.MILLISECOND] = 0
    return calendar.time
}

fun calendarRange(month: Date): IntRange {
    val calendar = Calendar.getInstance()
    calendar.time = month
    return (calendar.get(Calendar.DAY_OF_WEEK)*-1+2)..calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
}

fun isSameDay(date1: Date?, date2: Date?): Boolean {
    val calendar1 = Calendar.getInstance()
    calendar1.time = date1 ?: Date()
    val calendar2 = Calendar.getInstance()
    calendar2.time = date2 ?: Date()
    return calendar1[Calendar.YEAR] == calendar2[Calendar.YEAR] && calendar1[Calendar.MONTH] == calendar2[Calendar.MONTH] && calendar1[Calendar.DAY_OF_MONTH] == calendar2[Calendar.DAY_OF_MONTH]
}
fun isSameYear(date1: Date?, date2: Date?): Boolean {
    val calendar1 = Calendar.getInstance()
    calendar1.time = date1 ?: Date()
    val calendar2 = Calendar.getInstance()
    calendar2.time = date2 ?: Date()
    return calendar1[Calendar.YEAR] == calendar2[Calendar.YEAR]
}
fun beginningOfWeek(): Date {
    val calendar = Calendar.getInstance()
    calendar.time = Date()
    // set day to minimum
    // set day to minimum

    calendar[Calendar.HOUR_OF_DAY] = 0
    calendar[Calendar.MINUTE] = 0
    calendar[Calendar.SECOND] = 0
    calendar[Calendar.MILLISECOND] = 0
    return addDay(calendar.time, -(calendar.get(Calendar.DAY_OF_WEEK) - 1))
}


fun beginningOfDay(date: Date): Date {
    val calendar = Calendar.getInstance()
    calendar.time = date
    // set day to minimum
    // set day to minimum

    calendar[Calendar.HOUR_OF_DAY] = 0
    calendar[Calendar.MINUTE] = 0
    calendar[Calendar.SECOND] = 0
    calendar[Calendar.MILLISECOND] = 0
    return calendar.time
}
