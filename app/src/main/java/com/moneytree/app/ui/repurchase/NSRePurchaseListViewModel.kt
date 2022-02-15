package com.moneytree.app.ui.repurchase

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSRePurchaseRepository
import com.moneytree.app.repository.NSRegisterRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.NSRePurchaseListResponse
import com.moneytree.app.repository.network.responses.NSTodayRePurchaseListData


/**
 * The view model class for trip history. It handles the business logic to communicate with the model for the trip history and provides the data to the observing UI component.
 */
class NSRePurchaseListViewModel(application: Application) : NSViewModel(application) {
    var rePurchaseList: MutableList<NSTodayRePurchaseListData> = arrayListOf()
    var tempRePurchaseList: MutableList<NSTodayRePurchaseListData> = arrayListOf()
    var isRePurchaseDataAvailable = MutableLiveData<Boolean>()
    var pageIndex: String = "1"
    var rePurchaseResponse: NSRePurchaseListResponse? = null

    /**
     * Get register list data
     *
     */
    fun getRePurchaseListData(pageIndex: String, search: String, isShowProgress: Boolean, isBottomProgress: Boolean) {
        if (pageIndex == "1") {
            rePurchaseList.clear()
        }
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        if (isBottomProgress) {
            isBottomProgressShowing.value = true
        }
        NSRePurchaseRepository.getRePurchaseListData(pageIndex, search, object : NSGenericViewModelCallback {
            override fun <T> onSuccess(data: T) {
                isProgressShowing.value = false
                if (isBottomProgress) {
                    isBottomProgressShowing.value = false
                }
                val rePurchaseMainListData = data as NSRePurchaseListResponse
                rePurchaseResponse = rePurchaseMainListData
                if (rePurchaseMainListData.data != null) {
                    if (rePurchaseMainListData.data.isValidList()) {
                        rePurchaseList.addAll(rePurchaseMainListData.data!!)
                        isRePurchaseDataAvailable.value = rePurchaseList.isValidList()
                    } else if (pageIndex == "1" || search.isNotEmpty()){
                        isRePurchaseDataAvailable.value = false
                    }
                } else if (pageIndex == "1" || search.isNotEmpty()){
                    isRePurchaseDataAvailable.value = false
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