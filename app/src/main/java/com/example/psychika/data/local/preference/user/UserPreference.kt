package com.example.psychika.data.local.preference.user

import android.content.Context

class UserPreference(context: Context) {
    private val preference = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setUser(data: User) {
        val editor = preference.edit()
        editor.apply {
            putString(ID, data.id)
            putBoolean(REMEMBER_ME, data.rememberMe)
            putBoolean(GOOGLE_AUTH, data.googleAuth)
            apply()
        }
    }

    fun getUser(): User {
        val user = User()
        user.apply {
            id = preference.getString(ID, "")
            rememberMe = preference.getBoolean(REMEMBER_ME, false)
            googleAuth = preference.getBoolean(GOOGLE_AUTH, false)
        }

        return user
    }

    companion object {
        private const val PREFS_NAME = "user_pref"
        private const val ID = "id"
        private const val REMEMBER_ME = "remember_me"
        private const val GOOGLE_AUTH = "google_auth"
    }
}