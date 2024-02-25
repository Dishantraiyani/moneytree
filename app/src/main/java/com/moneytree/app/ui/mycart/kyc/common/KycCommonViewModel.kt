package com.moneytree.app.ui.mycart.kyc.common

import android.app.Application
import com.moneytree.app.common.NSSingleLiveEvent
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.callbacks.NSUserDataCallback
import com.moneytree.app.database.MainDatabase
import com.moneytree.app.repository.NSUserRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.DistrictResponse
import com.moneytree.app.repository.network.responses.NSDataUser
import com.moneytree.app.repository.network.responses.NSUserResponse
import com.moneytree.app.repository.network.responses.StateResponse

/**
 * The view model class for change password. It handles the business logic to communicate with the model for the change password item and provides the data to the observing UI component.
 */
class KycCommonViewModel(application: Application) : NSViewModel(application) {
    var selectedGender: String? = null
    var selectedRelation: String? = null
    var selectedState = ""
    var selectedDistrict = ""

    var isChangeDataAvailable = NSSingleLiveEvent<Boolean>()


    fun updateProfile(isShowProgress: Boolean, map: HashMap<String, String>, callback: (Boolean, String) -> Unit) {
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
                                    callback.invoke(true, userData.message?:"")
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

    fun getUserDetail(callback: (NSDataUser) -> Unit) {
        MainDatabase.getUserData(object : NSUserDataCallback {
            override fun onResponse(userDetail: NSDataUser) {
               callback.invoke(userDetail)
            }
        })
    }

    fun getStateList(callback: (List<String>, List<String>) -> Unit) {
        isProgressShowing.value = true
        NSUserRepository.getStateList(object : NSGenericViewModelCallback {
            override fun <T> onSuccess(data: T) {
                if (data is StateResponse) {
                    val list = data.data.map { it.stateName }
                    getDistrictList(list, callback)
                } else {
                    getDistrictList(arrayListOf(), callback)
                }
            }

            override fun onError(errors: List<Any>) {
                getDistrictList(arrayListOf(), callback)
            }

            override fun onFailure(failureMessage: String?) {
                getDistrictList(arrayListOf(), callback)
            }

            override fun <T> onNoNetwork(localData: T) {
                getDistrictList(arrayListOf(), callback)
            }
        })
    }

    fun getDistrictList(list: List<String>, callback: (List<String>, List<String>) -> Unit) {
        NSUserRepository.getDistrictList(object : NSGenericViewModelCallback {
            override fun <T> onSuccess(data: T) {
                isProgressShowing.value = false
                if (data is DistrictResponse) {
                    val districtList = data.data.map { it.districtName }
                    callback.invoke(list, districtList)
                } else {
                    callback.invoke(list, arrayListOf())
                }
            }

            override fun onError(errors: List<Any>) {
                isProgressShowing.value = false
                callback.invoke(list, arrayListOf())
            }

            override fun onFailure(failureMessage: String?) {
                isProgressShowing.value = false
                callback.invoke(list, arrayListOf())
            }

            override fun <T> onNoNetwork(localData: T) {
                isProgressShowing.value = false
                callback.invoke(list, arrayListOf())
            }
        })
    }
}