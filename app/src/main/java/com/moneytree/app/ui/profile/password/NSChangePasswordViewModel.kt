package com.moneytree.app.ui.profile.password

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSSingleLiveEvent
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.repository.NSChangePasswordRepository
import com.moneytree.app.repository.NSUserRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback

/**
 * The view model class for change password. It handles the business logic to communicate with the model for the change password item and provides the data to the observing UI component.
 */
class NSChangePasswordViewModel(application: Application) : NSViewModel(application) {
    var isChangeDataAvailable = NSSingleLiveEvent<Boolean>()
    var isChangePassword: Boolean = false
    var strCurrentPassword: String? = null
    var strNewPassword: String? = null

    fun isValid(): Boolean{
        return !strCurrentPassword.isNullOrEmpty() && !strNewPassword.isNullOrEmpty()
    }

    /**
     * change password data
     *
     */
    fun changePassword() {
        isProgressShowing.value = true
        NSChangePasswordRepository.changePassword(strCurrentPassword, strNewPassword, object : NSGenericViewModelCallback {
            override fun <T> onSuccess(data: T) {
                isProgressShowing.value = false
                isChangeDataAvailable.value = true
            }

            override fun onError(errors: List<Any>) {
                handleError(errors)
            }

            override fun onFailure(failureMessage: String?) {
                handleFailure(failureMessage)
            }

            override fun <T> onNoNetwork(localData: T) {
                handleNoNetwork()
            }
        })
    }

    /**
     * change trans password data
     *
     */
    fun changeTransPassword() {
        isProgressShowing.value = true
        NSChangePasswordRepository.changeTranPassword(strCurrentPassword, strNewPassword, object : NSGenericViewModelCallback {
            override fun <T> onSuccess(data: T) {
                isProgressShowing.value = false
                isChangeDataAvailable.value = true
            }

            override fun onError(errors: List<Any>) {
                handleError(errors)
            }

            override fun onFailure(failureMessage: String?) {
                handleFailure(failureMessage)
            }

            override fun <T> onNoNetwork(localData: T) {
                handleNoNetwork()
            }
        })
    }
}