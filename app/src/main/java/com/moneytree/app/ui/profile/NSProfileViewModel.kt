package com.moneytree.app.ui.profile

import android.app.Activity
import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.R
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.callbacks.NSUserDataCallback
import com.moneytree.app.database.MainDatabase
import com.moneytree.app.repository.NSUserRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.*

/**
 * The view model class for profile. It handles the business logic to communicate with the model for the profile item and provides the data to the observing UI component.
 */
class NSProfileViewModel(application: Application) : NSViewModel(application) {
    var profileItemList: MutableList<String> = arrayListOf()
    var isUserDataAvailable = MutableLiveData<Boolean>()
    var nsUserData: NSDataUser? = null
    var apiValue: Int = 0
    var isLogout = MutableLiveData<Boolean>()

    /**
     * Get profile list data
     *
     * @param activity The activity's context
     */
    fun getProfileListData(activity: Activity) {
        with(activity) {
            with(resources) {
                profileItemList.clear()
                profileItemList.add(getString(R.string.member_tree))
                profileItemList.add(getString(R.string.level_member_tree))
               // profileItemList.add(getString(R.string.notification_title))
              //  profileItemList.add(getString(R.string.trip_history_title))
              //  profileItemList.add(getString(R.string.transactions_title))
              //  profileItemList.add(getString(R.string.contact_us_title))
                profileItemList.add(getString(R.string.logout))
            }
        }
    }

    fun getUserDetail() {
        MainDatabase.getUserData(object : NSUserDataCallback {
            override fun onResponse(userDetail: NSDataUser) {
                nsUserData = userDetail
                isUserDataAvailable.value = true
            }
        })
    }

    /**
     * logout data
     *
     */
    fun logout() {
        NSUserRepository.logout(object : NSGenericViewModelCallback {
            override fun <T> onSuccess(data: T) {
                isLogout.value = true
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