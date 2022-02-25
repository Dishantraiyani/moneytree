package com.moneytree.app.ui.profile.edit

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSLoginRegisterEvent
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.callbacks.NSUserDataCallback
import com.moneytree.app.database.MainDatabase
import com.moneytree.app.repository.NSUserRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.*
import org.greenrobot.eventbus.EventBus


/**
 * The view model class for home. It handles the business logic to communicate with the model for the home and provides the data to the observing UI component.
 */
class NSEditViewModel(application: Application) : NSViewModel(application) {
    var nsUserData: NSDataUser? = null
    var isUserDataAvailable = MutableLiveData<Boolean>()
    var strFullName: String = ""
    var strAddress: String = ""
    var strEmail: String = ""
    var strMobile: String = ""
    var strPanNo: String = ""
    var strBankIfsc: String = ""
    var strBankName: String = ""
    var strAccNo: String = ""
    var isUpdateProfileEnable = false
    var isProfileUpdated = MutableLiveData<Boolean>()


    fun getUserDetail() {
        MainDatabase.getUserData(object : NSUserDataCallback {
            override fun onResponse(userDetail: NSDataUser) {
                nsUserData = userDetail
                isUserDataAvailable.value = true
            }
        })
    }

    /**
     * To update profile data
     *
     * @param isShowProgress The progress dialog show check
     */
    fun updateProfile(isShowProgress: Boolean) {
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        NSUserRepository.updateProfile(strFullName, strAddress, strEmail, strMobile, strPanNo, strBankIfsc, strBankName, strAccNo, object : NSGenericViewModelCallback {
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
                                    isProfileUpdated.value = true
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