package com.moneytree.app.ui.home

import android.app.Application
import android.text.method.LinkMovementMethod
import androidx.core.text.HtmlCompat
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
        R.drawable.ic_mobile_ico,
        R.drawable.ic_dth,
        R.drawable.ic_cable,
        R.drawable.ic_fast_tag,
        R.drawable.ic_broadband,
        R.drawable.ic_gas,
        R.drawable.ic_electricity,
        R.drawable.ic_emi,
        R.drawable.ic_insurance_ico,
        R.drawable.ic_lic_ico
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

    fun setEarningAmount(): String {
        with(dashboardData) {
            with(this?.data!!) {
                return earningAmount.ifEmpty {
                    "0"
                }
            }
        }
    }

    fun setRoyaltyStatus(): String {
        with(dashboardData) {
            with(this?.data!!) {
                return royaltyName!!.ifEmpty {
                    "Not Available"
                }
            }
        }
    }
}