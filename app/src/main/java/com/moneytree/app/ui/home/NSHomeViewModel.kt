package com.moneytree.app.ui.home

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.moneytree.app.R
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.callbacks.NSUserDataCallback
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.database.MainDatabase
import com.moneytree.app.repository.NSDashboardRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.*
import com.moneytree.app.ui.slide.SliderFragment


/**
 * The view model class for home. It handles the business logic to communicate with the model for the home and provides the data to the observing UI component.
 */
class NSHomeViewModel(application: Application) : NSViewModel(application) {
    var isDashboardDataAvailable = MutableLiveData<Boolean>()
    var dashboardData: NSDashboardResponse? = null
    var strHomeData : String? = null
    private var homeDetail: NSNotificationListData? = null
    var nsUserData: NSDataUser? = null
    var isUserDataAvailable = MutableLiveData<Boolean>()
    private val mFragmentTitleList: MutableList<String> = ArrayList()
    val mFragmentList: MutableList<Fragment> = ArrayList()
    var fieldName: Array<String> = arrayOf()
    var fieldImage = arrayOf(
        R.drawable.mobile,
        R.drawable.dth,
        R.drawable.data_card,
        R.drawable.landlne,
        R.drawable.broadband,
        R.drawable.gas,
        R.drawable.electricity,
        R.drawable.water,
        R.drawable.insurance,
        R.drawable.fee,
        R.drawable.creditcard,
        R.drawable.google_play
    )

    /**
     * To get the home detail
     */
    fun getHomeDetail() {
        if (!strHomeData.isNullOrEmpty()) {
            homeDetail = Gson().fromJson(strHomeData, NSNotificationListData::class.java)
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
     * Get dashboard data
     *
     * @param isShowProgress The progress dialog show check
     */
    fun getDashboardData(isShowProgress: Boolean) {
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        NSDashboardRepository.dashboardData(object : NSGenericViewModelCallback {
            override fun <T> onSuccess(data: T) {
                isProgressShowing.value = false
                val dashboardMainData = data as NSDashboardResponse
                dashboardData = dashboardMainData
                isDashboardDataAvailable.value = true
            }

            override fun onError(errors: List<Any>) {
                isDashboardDataAvailable.value = false
                handleError(errors)
            }

            override fun onFailure(failureMessage: String?) {
                isDashboardDataAvailable.value = false
                handleFailure(failureMessage)
            }

            override fun <T> onNoNetwork(localData: T) {
                isDashboardDataAvailable.value = false
                handleNoNetwork()
            }
        })
    }

    fun setFragmentData() {
        mFragmentTitleList.clear()
        mFragmentTitleList.add("")
        mFragmentList.clear()
        mFragmentList.add(SliderFragment())
        mFragmentList.add(SliderFragment())
        mFragmentList.add(SliderFragment())
    }

    fun setDownLine(): String {
        with(dashboardData) {
            with(this?.data!!) {
                return if (dwnTotal.isValidList()) {
                    dwnTotal?.get(0)?.cnt ?: "0"
                } else {
                    "0"
                }
            }
        }
    }

    fun setVoucher(): String {
        with(dashboardData) {
            with(this?.data!!) {
                return if (voucherTotal.isValidList()) {
                    voucherTotal?.get(0)?.cnt ?: "0"
                } else {
                    "0"
                }
            }
        }
    }

    fun setJoinVoucher(): String {
        with(dashboardData) {
            with(this?.data!!) {
                return if (availableJoiningVoucher.isValidList()) {
                    availableJoiningVoucher?.get(0)?.cnt ?: "0"
                } else {
                    "0"
                }
            }
        }
    }

    fun setWallet(): String {
        with(dashboardData) {
            with(this?.data!!) {
                return if (wltAmt.isValidList()) {
                    wltAmt?.get(0)?.amount ?: "0"
                } else {
                    "0"
                }
            }
        }
    }
}