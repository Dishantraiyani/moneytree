package com.moneytree.app.repository

import com.moneytree.app.common.NSApplication
import com.moneytree.app.common.NSUserManager
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.callbacks.NSRetrofitCallback
import com.moneytree.app.repository.network.error.NSApiErrorHandler
import com.moneytree.app.repository.network.requests.NSChangePasswordRequest
import com.moneytree.app.repository.network.responses.NSChangePasswordResponse
import com.moneytree.app.repository.network.responses.NSUserResponse
import retrofit2.Response

/**
 * Repository class to handle data operations related to change password
 */
object NSChangePasswordRepository {
    private val apiManager by lazy { NSApplication.getInstance().getApiManager() }
    private var errorMessageList: MutableList<Any> = mutableListOf()

    /**
     * To make change password API to change the password
     *
     * @param currentPassword  The currentPassword provided by the current password
     * @param newPassword      The newPassword provided by the new password
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun changePassword(
        currentPassword: String?,
        newPassword: String?,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        val changePasswordRequest = NSChangePasswordRequest(currentPassword, newPassword)
        apiManager.changePassword(changePasswordRequest, object :
            NSRetrofitCallback<NSChangePasswordResponse>(viewModelCallback, NSApiErrorHandler.ERROR_CHANGE_PASSWORD) {
            override fun <T> onResponse(response: Response<T>) {
                val data = response.body() as NSChangePasswordResponse
                if (data.status) {
                    viewModelCallback.onSuccess(response.body())
                } else {
					errorMessageList.clear()
                    errorMessageList.add(data.message!!)
                    viewModelCallback.onError(errorMessageList)
                }
            }
        })
    }

    /**
     * To make change tran password API to change the password
     *
     * @param currentPassword  The currentPassword provided by the current password
     * @param newPassword      The newPassword provided by the new password
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun changeTranPassword(
        currentPassword: String?,
        newPassword: String?,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        val changePasswordRequest = NSChangePasswordRequest(currentPassword, newPassword)
        apiManager.changeTranPassword(changePasswordRequest, object :
            NSRetrofitCallback<NSChangePasswordResponse>(viewModelCallback, NSApiErrorHandler.ERROR_CHANGE_TRAN_PASSWORD) {
            override fun <T> onResponse(response: Response<T>) {
                val data = response.body() as NSChangePasswordResponse
                if (data.status) {
                    viewModelCallback.onSuccess(response.body())
                } else {
					errorMessageList.clear()
                    errorMessageList.add(data.message!!)
                    viewModelCallback.onError(errorMessageList)
                }
            }
        })
    }
}
