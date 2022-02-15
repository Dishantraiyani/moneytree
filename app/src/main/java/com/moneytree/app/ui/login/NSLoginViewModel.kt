package com.moneytree.app.ui.login

import android.app.Application
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.moneytree.app.R
import com.moneytree.app.common.NSLoginRegisterEvent
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.callbacks.NSUserDataCallback
import com.moneytree.app.database.MainDatabase
import com.moneytree.app.repository.NSUserRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.NSDataUser
import com.moneytree.app.repository.network.responses.NSUserResponse
import org.greenrobot.eventbus.EventBus

/**
 * The view model class for login. It handles the business logic to communicate with the model for the login and provides the data to the observing UI component.
 */
class NSLoginViewModel(application: Application) : NSViewModel(application) {
    var util: PhoneNumberUtil? = null
    var strUserName: String? = null
    var strPassword: String? = null

    /**
     * To check all the mandatory fields are entered and valid
     *
     * @return status of all mandatory fields
     */
    private fun checkAllFieldsValid(): Boolean {
        var errorId: Int? = null
        when {
            strUserName.isNullOrBlank() -> {
                errorId = R.string.enter_user_name
            }
            strPassword.isNullOrBlank() -> {
                errorId = R.string.enter_password
            }
            (strPassword!!.length < 8) -> {
                errorId = R.string.enter_password_correct
            }
        }
        errorId?.let {
            validationErrorId.value = it
            return false
        }
        return true
    }

    /**
     * To initiate login process
     *
     */
    fun login() {
        if (checkAllFieldsValid()) {
            isProgressShowing.value = true
            NSUserRepository.login(strUserName, strPassword, object : NSGenericViewModelCallback {
                override fun <T> onSuccess(data: T) {
                    isProgressShowing.value = false
                    val userData = data as NSUserResponse
                    val isDataAvailable = userData.data != null
                    if (isDataAvailable) {
                        if (userData.status) {
                            MainDatabase.insertUserData(
                                userData.data!!,
                                object : NSUserDataCallback {
                                    override fun onResponse(userDetail: NSDataUser) {
                                        EventBus.getDefault().post(NSLoginRegisterEvent(userData.data!!))
                                    }
                                })
                        }
                    }
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
}