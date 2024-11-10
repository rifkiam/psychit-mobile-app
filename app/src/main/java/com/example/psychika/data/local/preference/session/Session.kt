package com.example.psychika.data.local.preference.session

import android.content.Context

class Session(context: Context) {
    private val preference = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setSessionId(sessionId: String) {
        val editor = preference.edit()
        editor.apply {
            putString(SESSION_ID, sessionId)
            apply()
        }
    }

    fun getSessionId(): String {
        val sessionId = ""

        return sessionId
    }

    companion object {
        private const val PREFS_NAME = "user_pref"
        private const val SESSION_ID = "session_id"
    }
}