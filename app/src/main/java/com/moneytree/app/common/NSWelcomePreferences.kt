package com.moneytree.app.common

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.moneytree.app.BuildConfig
import com.moneytree.app.repository.network.responses.NSUserResponse

/**
 * Class to maintain shared preference
 */
class NSWelcomePreferences(context: Context) {
    private val preference: SharedPreferences =
        context.getSharedPreferences(BuildConfig.WELCOME_PREFERENCE, Context.MODE_PRIVATE)
    private val prefEdit: SharedPreferences.Editor = preference.edit()

    companion object {
        //Keys for user data
        private const val KEY_WELCOME_SCREEN = "welcome_screen"
    }


	var isWelcome: Boolean
        get() = preference.getBoolean(KEY_WELCOME_SCREEN, false)
        set(active) = prefEdit.putBoolean(KEY_WELCOME_SCREEN, active).apply()

    /**
     * To clear all preferences data
     */
    fun clearPrefData() {
        prefEdit.clear().apply()
    }
}
