package com.moneytree.app.ui.recharge.history

import android.app.Activity
import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.R
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSRechargeRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.*


/**
 * The view model class for register. It handles the business logic to communicate with the model for the register and provides the data to the observing UI component.
 */
class NSRechargeHistoryViewModel(application: Application) : NSViewModel(application),
    NSGenericViewModelCallback {
    var rechargeList: MutableList<RechargeListDataItem> = arrayListOf()
    var tempRechargeList: MutableList<RechargeListDataItem> = arrayListOf()
    var isRechargeDataAvailable = MutableLiveData<Boolean>()
    var pageIndex: String = "1"
    var rechargeResponse: NSRechargeListResponse? = null
    private var isBottomProgressShow: Boolean = false
    private var searchData: String = ""
	var rechargeType: String? = ""
	var statusType: String = ""

	//Spinner value for registration form
    var registrationType: MutableList<String> = arrayListOf()

    /**
     * Get register list data
     *
     */
    fun getRechargeListData(pageIndex: String, search: String, isShowProgress: Boolean, isBottomProgress: Boolean) {
        if (pageIndex == "1") {
            rechargeList.clear()
        }
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        if (isBottomProgress) {
            isBottomProgressShowing.value = true
        }
        isBottomProgressShow = isBottomProgress
        searchData = search
        NSRechargeRepository.getRechargeListData(pageIndex, search, rechargeType!!, statusType, this)
    }

    override fun <T> onSuccess(data: T) {
        isProgressShowing.value = false
        if (isBottomProgressShow) {
            isBottomProgressShowing.value = false
        }
        val rechargeMainListData = data as NSRechargeListResponse
        rechargeResponse = rechargeMainListData
        if (rechargeMainListData.data != null) {
            if (rechargeMainListData.data.isValidList()) {
                rechargeList.addAll(rechargeMainListData.data)
                isRechargeDataAvailable.value = rechargeList.isValidList()
            } else if (pageIndex == "1" || searchData.isNotEmpty()){
                isRechargeDataAvailable.value = false
            }
        } else if (pageIndex == "1" || searchData.isNotEmpty()){
            isRechargeDataAvailable.value = false
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

    fun addRegistrationType(activity: Activity) {
        with(activity.resources) {
            val registration = getStringArray(R.array.registration_type)
            for (data in registration) {
                registrationType.add(data)
            }
        }
    }
}
