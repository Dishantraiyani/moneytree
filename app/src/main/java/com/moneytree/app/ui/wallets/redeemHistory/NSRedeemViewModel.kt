package com.moneytree.app.ui.wallets.redeemHistory

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSWalletRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.NSRedeemListResponse
import com.moneytree.app.repository.network.responses.NSWalletRedeemData


/**
 * The view model class for redeem. It handles the business logic to communicate with the model for the redeem and provides the data to the observing UI component.
 */
class NSRedeemViewModel(application: Application) : NSViewModel(application),
    NSGenericViewModelCallback {
    var redeemList: MutableList<NSWalletRedeemData> = arrayListOf()
    var tempRedeemList: MutableList<NSWalletRedeemData> = arrayListOf()
    var isRedeemDataAvailable = MutableLiveData<Boolean>()
    var pageIndex: String = "1"
    var redeemResponse: NSRedeemListResponse? = null
    private var isBottomProgressShow: Boolean = false
    private var searchData: String = ""
	var startingDate: String = ""
	var endingDate: String = ""

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
        NSWalletRepository.getWalletRedeemList(pageIndex, search, "", "",this)
    }

    override fun <T> onSuccess(data: T) {
        isProgressShowing.value = false
        if (isBottomProgressShow) {
            isBottomProgressShowing.value = false
        }
        val voucherMainListData = data as NSRedeemListResponse
        redeemResponse = voucherMainListData
		if (voucherMainListData.data.isValidList()) {
			redeemList.addAll(voucherMainListData.data)
			isRedeemDataAvailable.value = redeemList.isValidList()
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
