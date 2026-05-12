package com.example.stashed.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    companion object {
        private const val PREF_NAME = "stashed_session"
        private const val KEY_USER_ID = "logged_in_user_id"
        private const val KEY_USER_NAME = "logged_in_user_name"
        private const val NO_USER = -1
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun login(userId: Int, userName: String) {
        prefs.edit()
            .putInt(KEY_USER_ID, userId)
            .putString(KEY_USER_NAME, userName)
            .apply()
    }

    fun logout() {
        prefs.edit()
            .remove(KEY_USER_ID)
            .remove(KEY_USER_NAME)
            .apply()
    }

    fun isLoggedIn(): Boolean = prefs.getInt(KEY_USER_ID, NO_USER) != NO_USER

    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, NO_USER)

    fun getUserName(): String = prefs.getString(KEY_USER_NAME, "") ?: ""
}
