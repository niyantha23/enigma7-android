package com.ieeevit.enigma7.utils

import android.content.Context
import android.content.SharedPreferences

class PrefManager(val context: Context) {
    private val PREFS_NAME = "com.ieeevit.enigma7"
    val sharedPref: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val authorizationCode: String = "AuthorizationCode"
    val userNameExist: String = "userStatus"
    private val IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch"
    val editor: SharedPreferences.Editor = sharedPref.edit()

    fun save(KEY_NAME: String, text: String) {


        editor.putString(KEY_NAME, text)
        editor.apply()
    }

    fun setAuthCode(text: String) {
        editor.putString(authorizationCode, text)
        editor.apply()
    }

    fun setUserStatus(text: Boolean) {
        editor.putBoolean(userNameExist, text)
        editor.apply()
    }
    fun setFirstTimeLaunch(text: Boolean) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, text)
        editor.apply()
    }
    fun isFirstTimeLaunch(): Boolean {
        return sharedPref.getBoolean(IS_FIRST_TIME_LAUNCH, true)
    }
    fun setIsLoggedIn(text: Boolean) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, text)
        editor.apply()
    }
    fun isLoggedIn(): Boolean {
        return sharedPref.getBoolean(IS_FIRST_TIME_LAUNCH, false)
    }
    fun getValueString(KEY_NAME: String): String? {
        return sharedPref.getString(KEY_NAME, null)
    }

    fun getAuthCode(): String? {
        return sharedPref.getString(authorizationCode, null)
    }

    fun getUserStaus(): Boolean? {
        return sharedPref.getBoolean(userNameExist, false)
    }

    fun clearSharedPreference() {
        //sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        editor.clear()
        editor.apply()
    }

    fun removeValue(KEY_NAME: String) {
        editor.remove(KEY_NAME)
        editor.apply()
    }
}