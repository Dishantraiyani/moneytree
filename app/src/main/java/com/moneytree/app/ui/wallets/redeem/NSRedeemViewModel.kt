package com.moneytree.app.ui.wallets.redeem

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSVoucherRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.NSVoucherListData
import com.moneytree.app.repository.network.responses.NSVoucherListResponse


/**
 * The view model class for redeem. It handles the business logic to communicate with the model for the redeem and provides the data to the observing UI component.
 */
class NSRedeemViewModel(application: Application) : NSViewModel(application),
    NSGenericViewModelCallback {
    var redeemList: MutableList<NSVoucherListData> = arrayListOf()
    var tempRedeemList: MutableList<NSVoucherListData> = arrayListOf()
    var isRedeemDataAvailable = MutableLiveData<Boolean>()
    var pageIndex: String = "1"
    var redeemResponse: NSVoucherListResponse? = null
    private var isBottomProgressShow: Boolean = false
    private var searchData: String = ""

    /**
     * Get redeem list data
     *
     */
    fun getRedeemListData(pageIndex: String, search: String, isShowProgress: Boolean, isBottomProgress: Boolean) {
        if (pageIndex == "1") {
            redeemList.clear()
        }
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        if (isBottomProgress) {
            isBottomProgressShowing.value = true
        }
        isBottomProgressShow = isBottomProgress
        searchData = search
        NSVoucherRepository.getJoiningVoucherPendingData(pageIndex, search, this)
    }

    override fun <T> onSuccess(data: T) {
        isProgressShowing.value = false
        if (isBottomProgressShow) {
            isBottomProgressShowing.value = false
        }
        val voucherMainListData = data as NSVoucherListResponse
        redeemResponse = voucherMainListData
        if (voucherMainListData.data != null) {
            if (voucherMainListData.data.isValidList()) {
                redeemList.addAll(voucherMainListData.data!!)
                isRedeemDataAvailable.value = redeemList.isValidList()
            } else if (pageIndex == "1" || searchData.isNotEmpty()){
                isRedeemDataAvailable.value = false
            }
        } else if (pageIndex == "1" || searchData.isNotEmpty()){
            isRedeemDataAvailable.value = false
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
}