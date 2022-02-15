package com.moneytree.app.ui.royaltyInfo

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSRoyaltyRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.NSRoyaltyInfoData
import com.moneytree.app.repository.network.responses.NSRoyaltyInfoResponse


/**
 * The view model class for trip history. It handles the business logic to communicate with the model for the trip history and provides the data to the observing UI component.
 */
class NSRoyaltyInfoViewModel(application: Application) : NSViewModel(application) {
    var royaltyInfoList: MutableList<NSRoyaltyInfoData> = arrayListOf()
    var isRoyaltyDataAvailable = MutableLiveData<Boolean>()
    var pageIndex: String = "1"
    var royaltyResponse: NSRoyaltyInfoResponse? = null
    var royaltyId: String? = null

    /**
     * Get register list data
     *
     */
    fun getRoyaltyListData(pageIndex: String, royaltyId: String, isShowProgress: Boolean, isBottomProgress: Boolean) {
        if (pageIndex == "1") {
            royaltyInfoList.clear()
        }
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        if (isBottomProgress) {
            isBottomProgressShowing.value = true
        }
        NSRoyaltyRepository.getRoyaltyInfoData(pageIndex, royaltyId, object : NSGenericViewModelCallback {
            override fun <T> onSuccess(data: T) {
                isProgressShowing.value = false
                if (isBottomProgress) {
                    isBottomProgressShowing.value = false
                }
                val royaltyMainListData = data as NSRoyaltyInfoResponse
                royaltyResponse = royaltyMainListData
                if (royaltyMainListData.data != null) {
                    if (royaltyMainListData.data.isValidList()) {
                        royaltyInfoList.addAll(royaltyMainListData.data!!)
                        isRoyaltyDataAvailable.value = royaltyInfoList.isValidList()
                    } else if (pageIndex == "1" || royaltyId.isNotEmpty()){
                        isRoyaltyDataAvailable.value = false
                    }
                } else if (pageIndex == "1" || royaltyId.isNotEmpty()){
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