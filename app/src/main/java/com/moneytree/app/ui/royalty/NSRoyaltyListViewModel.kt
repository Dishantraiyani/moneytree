package com.moneytree.app.ui.royalty

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSRePurchaseRepository
import com.moneytree.app.repository.NSRoyaltyRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.NSRePurchaseListResponse
import com.moneytree.app.repository.network.responses.NSRoyaltyListData
import com.moneytree.app.repository.network.responses.NSRoyaltyListResponse
import com.moneytree.app.repository.network.responses.NSTodayRePurchaseListData


/**
 * The view model class for trip history. It handles the business logic to communicate with the model for the trip history and provides the data to the observing UI component.
 */
class NSRoyaltyListViewModel(application: Application) : NSViewModel(application) {
    var royaltyList: MutableList<NSRoyaltyListData> = arrayListOf()
    var tempRoyaltyList: MutableList<NSRoyaltyListData> = arrayListOf()
    var isRoyaltyDataAvailable = MutableLiveData<Boolean>()
    var pageIndex: String = "1"
    var royaltyResponse: NSRoyaltyListResponse? = null

    /**
     * Get register list data
     *
     */
    fun getRoyaltyListData(pageIndex: String, search: String, isShowProgress: Boolean, isBottomProgress: Boolean) {
        if (pageIndex == "1") {
            royaltyList.clear()
        }
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        if (isBottomProgress) {
            isBottomProgressShowing.value = true
        }
        NSRoyaltyRepository.getRoyaltyListData(pageIndex, search, object : NSGenericViewModelCallback {
            override fun <T> onSuccess(data: T) {
                isProgressShowing.value = false
                if (isBottomProgress) {
                    isBottomProgressShowing.value = false
                }
                val rePurchaseMainListData = data as NSRoyaltyListResponse
                royaltyResponse = rePurchaseMainListData
                if (rePurchaseMainListData.data != null) {
                    if (rePurchaseMainListData.data.isValidList()) {
                        royaltyList.addAll(rePurchaseMainListData.data!!)
                        isRoyaltyDataAvailable.value = royaltyList.isValidList()
                    } else if (pageIndex == "1" || search.isNotEmpty()){
                        isRoyaltyDataAvailable.value = false
                    }
                } else if (pageIndex == "1" || search.isNotEmpty()){
                    isRoyaltyDataAvailable.value = false
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