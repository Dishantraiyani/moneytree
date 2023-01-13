package com.moneytree.app.common

import com.moneytree.app.repository.network.responses.NSUserResponse
import retrofit2.Response

/**
 * The class used to handle user preference like token
 */
object NSUserManager {
    private val prefs = NSApplication.getInstance().getPrefs()

    //Status of user logged in
    val isUserLoggedIn: Boolean get() = !getAuthToken().isBlank()

    /**
     * To get authentication token
     */
    fun getAuthToken() = prefs.authToken?:""

    /**
     * To save headers in preference
     *
     * @param T template of response
     * @param response response class
     */
    fun <T> saveHeadersInPreference(response: Response<T>) {
        val headers = response.body() as NSUserResponse
        prefs.authToken = headers.tokenId!!
    }

    /**
     * To save users response and headers in preference
     *
     * @param T template of response
     * @param response login response
     */
    fun <T> saveUserInPreference(response: Response<T>) {
        val userResponse = response.body() as NSUserResponse
        prefs.userData = userResponse
    }
}
