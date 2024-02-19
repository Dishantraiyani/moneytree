package com.moneytree.app.ui.mycart.kyc.common

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSApplication
import com.moneytree.app.common.NSSingleLiveEvent
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.callbacks.NSUserDataCallback
import com.moneytree.app.database.MainDatabase
import com.moneytree.app.repository.NSChangePasswordRepository
import com.moneytree.app.repository.NSUserRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.NSDataUser
import com.moneytree.app.repository.network.responses.NSUserResponse

/**
 * The view model class for change password. It handles the business logic to communicate with the model for the change password item and provides the data to the observing UI component.
 */
class KycCommonViewModel(application: Application) : NSViewModel(application) {
    var selectedGender: String? = null

    var isChangeDataAvailable = NSSingleLiveEvent<Boolean>()


    fun updateProfile(isShowProgress: Boolean, map: HashMap<String, String>, callback: (Boolean) -> Unit) {
        if (isShowProgress) {
            isProgressShowing.value = true
        }

        NSUserRepository.updateProfile(map, object : NSGenericViewModelCallback {
            override fun <T> onSuccess(data: T) {
                isProgressShowing.value = false
                val userData = data as NSUserResponse
                val isDataAvailable = userData.data != null
                if (isDataAvailable) {
                    if (userData.status) {
                       // NSApplication.getInstance().getPrefs().isKycVerified = userData.kycStatus
                        MainDatabase.insertUserData(
                            userData.data!!,
                            object : NSUserDataCallback {
                                override fun onResponse(userDetail: NSDataUser) {
                                    callback.invoke(true)
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