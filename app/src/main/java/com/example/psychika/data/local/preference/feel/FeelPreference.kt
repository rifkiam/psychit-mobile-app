package com.example.psychika.data.local.preference.feel

import android.content.Context
import com.example.psychika.data.entity.Feel

class FeelPreference(context: Context) {
    private val preference = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setInitialSelected(isInitialSelected: Boolean) {
        preference.edit().putBoolean(INITIAL_SELECTED, isInitialSelected).apply()
    }

    fun isInitialSelected(): Boolean {
        return preference.getBoolean(INITIAL_SELECTED, false)
    }

    fun setFeel(data: Feel, position: Int) {
        val editor = preference.edit()
        val isSelectedKey = "$IS_SELECTED$position"
        editor.putBoolean(isSelectedKey, data.isSelected)
        editor.apply()
    }

    fun getFeel(position: Int): Feel {
        val isSelectedKey = "$IS_SELECTED$position"
        val feel = Feel()
        feel.isSelected = preference.getBoolean(isSelectedKey, false)
        return feel
    }

    companion object {
        private const val PREFS_NAME = "feel_pref"
        private const val IS_SELECTED = "is_selected"
        private const val INITIAL_SELECTED = "initial_selected"
    }
}