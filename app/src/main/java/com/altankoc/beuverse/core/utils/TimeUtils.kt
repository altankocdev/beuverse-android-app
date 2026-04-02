package com.altankoc.beuverse.core.utils

import android.content.Context
import com.altankoc.beuverse.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun String.toTimeAgo(context: Context): String {
    return try {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val date: Date = format.parse(this.take(19)) ?: return this
        
        val now = System.currentTimeMillis()
        val diff = now - date.time

        if (diff < 0) return context.getString(R.string.time_just_now)

        val minutes = diff / 60000
        val hours = diff / 3600000
        val days = diff / 86400000
        val weeks = days / 7
        val months = days / 30

        when {
            minutes < 1 -> context.getString(R.string.time_just_now)
            minutes < 60 -> context.getString(R.string.time_minutes_ago, minutes)
            hours < 24 -> context.getString(R.string.time_hours_ago, hours)
            days < 7 -> context.getString(R.string.time_days_ago, days)
            weeks < 4 -> context.getString(R.string.time_weeks_ago, weeks)
            months < 12 -> context.getString(R.string.time_months_ago, months)
            else -> context.getString(R.string.time_years_ago, days / 365)
        }
    } catch (e: Exception) {
        this
    }
}

fun String.toJoinedDate(): String {
    return try {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val date: Date = format.parse(this.take(19)) ?: return this
        val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        monthFormat.format(date)
    } catch (e: Exception) {
        this
    }
}