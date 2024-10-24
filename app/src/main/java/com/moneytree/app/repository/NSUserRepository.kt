package com.moneytree.app.repository

import com.moneytree.app.common.NSApplication
import com.moneytree.app.common.NSUserManager
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.callbacks.NSRetrofitCallback
import com.moneytree.app.repository.network.error.NSApiErrorHandler
import com.moneytree.app.repository.network.requests.NSLoginRequest
import com.moneytree.app.repository.network.requests.NSUpdateProfileRequest
import com.moneytree.app.repository.network.responses.NSLogoutResponse
import com.moneytree.app.repository.network.responses.NSUserResponse
import retrofit2.Response

/**
 * Repository class to handle data operations related to user
 */
object NSUserRepository {
    private val apiManager by lazy { NSApplication.getInstance().getApiManager() }
    private var errorMessageList: MutableList<Any> = mutableListOf()
    /**
     * To make login API to authenticate the user
     *
     * @param userName  The username provided by the user
     * @param password      The password provided by the user
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun login(
        userName: String?,
        password: String?,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        val loginRequest = NSLoginRequest(userName, password)
        apiManager.login(loginRequest, object :
            NSRetrofitCallback<NSUserResponse>(viewModelCallback, NSApiErrorHandler.ERROR_LOGIN) {
            override fun <T> onResponse(response: Response<T>) {
                val data = response.body() as NSUserResponse
                if (data.status) {
                    NSUserManager.saveUserInPreference(response)
                    NSUserManager.saveHeadersInPreference(response)
                    viewModelCallback.onSuccess(response.body())
                } else {
                    errorMessageList.add(data.message!!)
                    viewModelCallback.onError(errorMessageList)
                }
            }
        })
    }

    /**
     * To make update profile API to the user
     *
     * @param userName  The username provided by the user
     * @param password      The password provided by the user
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun updateProfile(
        fullName: String?,
        address: String?,
        email: String?,
        mobile: String?,
        panno: String?,
        ifscCode: String?,
        bankName: String?,
        acNo: String?,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        val updateRequest = NSUpdateProfileRequest(fullName, address, email, mobile, panno, ifscCode, bankName, acNo)
        apiManager.updateProfile(updateRequest, object :
            NSRetrofitCallback<NSUserResponse>(viewModelCallback, NSApiErrorHandler.ERROR_UPDATE_PROFILE) {
            override fun <T> onResponse(response: Response<T>) {
                val data = response.body() as NSUserResponse
                if (data.status) {
                    NSUserManager.saveUserInPreference(response)
                    viewModelCallback.onSuccess(response.body())
                } else {
                    errorMessageList.add(data.message!!)
                    viewModelCallback.onError(errorMessageList)
                }
            }
        })
    }

    /**
     * To make logout
     *
     * @param viewModelCallback The callback to communicate back to view model
     */
    fun logout(viewModelCallback: NSGenericViewModelCallback) {
        apiManager.logout(object : NSRetrofitCallback<NSLogoutResponse>(
            viewModelCallback, NSApiErrorHandler.ERROR_LOGOUT
        ) {
            override fun <T> onResponse(response: Response<T>) {
                val data = response.body() as NSLogoutResponse
                if (data.status) {
                    NSApplication.getInstance().getPrefs().clearPrefData()
                    viewModelCallback.onSuccess(response.body())
                } else {
                    errorMessageList.add(data.message!!)
                    viewModelCallback.onError(errorMessageList)
                }
            }
        })
    }
}