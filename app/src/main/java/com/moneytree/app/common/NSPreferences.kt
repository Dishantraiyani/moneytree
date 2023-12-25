package com.moneytree.app.common

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.moneytree.app.BuildConfig
import com.moneytree.app.repository.network.responses.NSAddressCreateResponse
import com.moneytree.app.repository.network.responses.NSUserResponse

/**
 * Class to maintain shared preference
 */
class NSPreferences(context: Context) {
    private val preference: SharedPreferences =
        context.getSharedPreferences(BuildConfig.PREFERENCE, Context.MODE_PRIVATE)
    private val prefEdit: SharedPreferences.Editor = preference.edit()

    companion object {
        //Keys for user data
        private const val KEY_AUTH_TOKEN = "token"
        private const val KEY_USER_DATA = "key_user_data"
        private const val KEY_OFFER_TAB_POSITION = "key_offer_tab_position"
        private const val KEY_IS_ACTIVE_USER = "key_is_active_user"
        private const val KEY_DISPLAY_POPUP = "key_is_display_popup"
        private const val KEY_SELECTED_ADDRESS = "key_is_selected_address"
        private const val KEY_KYC_VERIFIED = "key_is_kyc_verified"
        private const val KEY_KYC_VERIFIED_SKIP = "key_is_kyc_verified_skip"
        private const val KEY_REWARD_COIN_PERIOD = "key_is_reward_coin_period"
    }

    /**
     * Property that contains the authentication token to used for authentication web service call
     */
    var authToken: String?
        get() = preference.getString(KEY_AUTH_TOKEN, null)
        set(token) = prefEdit.putString(KEY_AUTH_TOKEN, token).apply()

    var isKycVerified: String?
        get() = preference.getString(KEY_KYC_VERIFIED, null)
        set(token) = prefEdit.putString(KEY_KYC_VERIFIED, token).apply()

    var isKycVerifiedSkip: Boolean
        get() = preference.getBoolean(KEY_KYC_VERIFIED_SKIP, false)
        set(token) = prefEdit.putBoolean(KEY_KYC_VERIFIED_SKIP, token).apply()

	var isActive: Boolean
        get() = preference.getBoolean(KEY_IS_ACTIVE_USER, false)
        set(active) = prefEdit.putBoolean(KEY_IS_ACTIVE_USER, active).apply()

	var isPopupDisplay: Boolean
        get() = preference.getBoolean(KEY_DISPLAY_POPUP, false)
        set(active) = prefEdit.putBoolean(KEY_DISPLAY_POPUP, active).apply()

    /**
     * Property that contains login user data
     */
    var userData: NSUserResponse?
        get() {
            val json: String? = preference.getString(KEY_USER_DATA, null)
            return Gson().fromJson(json, NSUserResponse::class.java)
        }
        set(loginResponse) {
            val json: String = Gson().toJson(loginResponse)
            prefEdit.putString(KEY_USER_DATA, json).apply()
        }

    var selectedAddress: NSAddressCreateResponse?
        get() {
            val json: String? = preference.getString(KEY_SELECTED_ADDRESS, null)
            return Gson().fromJson(json, NSAddressCreateResponse::class.java)
        }
        set(loginResponse) {
            val json: String = Gson().toJson(loginResponse)
            prefEdit.putString(KEY_SELECTED_ADDRESS, json).apply()
        }

    /**
     * Property that contains language selected data
     */
    var offerTabPosition: Int?
        get() {
            return preference.getInt(KEY_OFFER_TAB_POSITION, 0)
        }
        set(language) {
            prefEdit.putInt(KEY_OFFER_TAB_POSITION, language!!).apply()
        }

    var rewardCoinPeriod: String?
        get() = preference.getString(KEY_REWARD_COIN_PERIOD, null)
        set(period) = prefEdit.putString(KEY_REWARD_COIN_PERIOD, period).apply()

    /**
     * To clear all preferences data
     */
    fun clearPrefData() {
        prefEdit.clear().apply()
    }
}
