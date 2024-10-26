package com.example.psychika.utils

import android.content.Context
import android.util.Log
import android.util.Patterns
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object Utils {
    private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    private const val API_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS'Z'"
    private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())
    private val chatTimeFormat = "HH:mm"
    private val historyChatTimeFormat = "dd MMMM yyyy"

    fun isValidEmail(email: CharSequence): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun createCustomTempFile(context: Context): File {
        val filesDir = context.externalCacheDir
        return File.createTempFile(timeStamp, ".jpg", filesDir)
    }

    fun getCurrentTime(): String {
        val currentTime = Calendar.getInstance().time
        return SimpleDateFormat(chatTimeFormat, Locale.getDefault()).format(currentTime)
    }

    fun getCurrentDate(): String {
        val currentTime = Calendar.getInstance().time
        return SimpleDateFormat(historyChatTimeFormat, Locale.getDefault()).format(currentTime)
    }

    fun String.convertTimeStampChatApi(): String {
        val apiFormat = SimpleDateFormat(API_DATE_FORMAT, Locale.getDefault())
        apiFormat.timeZone= TimeZone.getTimeZone("UTC")
        return try {
            val date = apiFormat.parse(this)
            SimpleDateFormat(chatTimeFormat, Locale.getDefault()).format(date)
        } catch (e: ParseException) {
            Log.e("Utils", "Error parsing date: $this", e)
            ""
        }
    }
}