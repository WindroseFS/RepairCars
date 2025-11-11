package com.thorapps.repaircars.auth

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesHelper(context: Context) {

    private val sharedPref: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_PHONE = "user_phone"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_ACCESS_TOKEN = "access_token"
    }

    fun saveLoginData(userId: Long, userName: String, userEmail: String, userPhone: String?, token: String) {
        with(sharedPref.edit()) {
            putLong(KEY_USER_ID, userId)
            putString(KEY_USER_NAME, userName)
            putString(KEY_USER_EMAIL, userEmail)
            putString(KEY_USER_PHONE, userPhone ?: "")
            putString(KEY_ACCESS_TOKEN, token)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun isLoggedIn(): Boolean {
        return sharedPref.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getUserId(): Long {
        return sharedPref.getLong(KEY_USER_ID, -1L)
    }

    fun getUserName(): String {
        return sharedPref.getString(KEY_USER_NAME, "") ?: ""
    }

    fun getUserEmail(): String {
        return sharedPref.getString(KEY_USER_EMAIL, "") ?: ""
    }

    fun getUserPhone(): String {
        return sharedPref.getString(KEY_USER_PHONE, "") ?: ""
    }

    fun hasPhone(): Boolean {
        return sharedPref.getString(KEY_USER_PHONE, "")?.isNotEmpty() == true
    }

    fun getAccessToken(): String {
        return sharedPref.getString(KEY_ACCESS_TOKEN, "") ?: ""
    }

    fun logout() {
        with(sharedPref.edit()) {
            clear()
            apply()
        }
    }

    fun updateUserPhone(phone: String) {
        sharedPref.edit().putString(KEY_USER_PHONE, phone).apply()
    }
}