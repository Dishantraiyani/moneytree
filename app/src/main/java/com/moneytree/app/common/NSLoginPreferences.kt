package com.moneytree.app.common

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.moneytree.app.BuildConfig
import com.moneytree.app.repository.network.responses.NSUserResponse

/**
 * Class to maintain shared preference
 */
class NSLoginPreferences(context: Context) {
    private val preference: SharedPreferences =
        context.getSharedPreferences(BuildConfig.LOGIN_PREFERENCE, Context.MODE_PRIVATE)
    private val prefEdit: SharedPreferences.Editor = preference.edit()

    companion object {
        //Keys for user data
        private const val KEY_AUTH_USERNAME = "user_username"
        private const val KEY_USER_PASSWORD = "user_password"
        private const val KEY_NOTIFICATION_TOKEN = "key_notification_token"
    }

    /**
     * Property that contains the user name
     */
    var prefUserName: String?
        get() = preference.getString(KEY_AUTH_USERNAME, null)
        set(name) = prefEdit.putString(KEY_AUTH_USERNAME, name).apply()

    /**
     * Property that contains the user password
     */
    var prefPassword: String?
        get() = preference.getString(KEY_USER_PASSWORD, null)
        set(name) = prefEdit.putString(KEY_USER_PASSWORD, name).apply()

	/**
     * Property that contains the user password
     */
    var notificationToken: String?
        get() = preference.getString(KEY_NOTIFICATION_TOKEN, null)
        set(name) = prefEdit.putString(KEY_NOTIFICATION_TOKEN, name).apply()

    /**
     * To clear all preferences data
     */
    fun clearPrefData() {
        prefEdit.clear().apply()
    }
}
