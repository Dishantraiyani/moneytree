package com.moneytree.app.ui.profile

import android.app.Activity
import android.app.Application
import com.moneytree.app.R
import com.moneytree.app.common.NSSingleLiveEvent
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.callbacks.NSUserDataCallback
import com.moneytree.app.database.MainDatabase
import com.moneytree.app.repository.NSUserRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.NSDataUser

/**
 * The view model class for profile. It handles the business logic to communicate with the model for the profile item and provides the data to the observing UI component.
 */
class NSProfileViewModel(application: Application) : NSViewModel(application) {
    var profileItemList: MutableList<String> = arrayListOf()
    var profileIconList: MutableList<Int> = arrayListOf()
    var isUserDataAvailable = NSSingleLiveEvent<NSDataUser>()
    var isLogout = NSSingleLiveEvent<Boolean>()

    /**
     * Get profile list data
     *
     * @param activity The activity's context
     */
    fun getProfileListData(activity: Activity) {
        with(activity) {
            with(resources) {
                profileItemList.clear()
                profileItemList.add(getString(R.string.kyc_status))
                profileItemList.add(getString(R.string.change_password))
                profileItemList.add(getString(R.string.change_tran_password))
                profileItemList.add(getString(R.string.contact_us_title))
                profileItemList.add(getString(R.string.share))
                profileItemList.add(getString(R.string.rate_us))
                profileItemList.add(getString(R.string.update))
                profileItemList.add(getString(R.string.terms))
                profileItemList.add(getString(R.string.policy))
                profileItemList.add(getString(R.string.refund))
                profileItemList.add(getString(R.string.logout))
                getProfileIconListData(activity)
            }
        }
    }

    private fun getProfileIconListData(activity: Activity) {
        with(activity) {
            with(resources) {
                profileIconList.clear()
                profileIconList.add(R.drawable.kyc_status)
                profileIconList.add(R.drawable.ic_lock)
                profileIconList.add(R.drawable.ic_lock)
                profileIconList.add(R.drawable.ic_contact_us)
                profileIconList.add(R.drawable.ic_share)
                profileIconList.add(R.drawable.ic_rate_us)
                profileIconList.add(R.drawable.ic_refresh)
                profileIconList.add(R.drawable.ic_terms)
                profileIconList.add(R.drawable.ic_policy)
				profileIconList.add(R.drawable.ic_refund)
                profileIconList.add(R.drawable.ic_logout)
            }
        }
    }

    fun getUserDetail() {
        MainDatabase.getUserData(object : NSUserDataCallback {
            override fun onResponse(userDetail: NSDataUser) {
                isUserDataAvailable.value = userDetail
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
