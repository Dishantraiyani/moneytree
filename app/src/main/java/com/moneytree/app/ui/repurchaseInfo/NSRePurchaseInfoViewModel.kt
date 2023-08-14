package com.moneytree.app.ui.repurchaseInfo

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSRePurchaseRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.NSRePurchaseInfoData
import com.moneytree.app.repository.network.responses.NSRePurchaseInfoResponse


/**
 * The view model class for repurchase info. It handles the business logic to communicate with the model for the repurchase info and provides the data to the observing UI component.
 */
class NSRePurchaseInfoViewModel(application: Application) : NSViewModel(application) {
    var rePurchaseInfoList: MutableList<NSRePurchaseInfoData> = arrayListOf()
    var isRePurchaseDataAvailable = MutableLiveData<Boolean>()
    var pageIndex: String = "1"
    var rePurchaseResponse: NSRePurchaseInfoResponse? = null
    var repurchaseId: String? = null

    /**
     * Get repurchase list data
     *
     */
    fun getRePurchaseListData(pageIndex: String, repurchaseId: String, isShowProgress: Boolean, isBottomProgress: Boolean) {
        if (pageIndex == "1") {
            rePurchaseInfoList.clear()
        }
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        if (isBottomProgress) {
            isBottomProgressShowing.value = true
        }
        NSRePurchaseRepository.getRePurchaseInfoData(pageIndex, repurchaseId, object : NSGenericViewModelCallback {
            override fun <T> onSuccess(data: T) {
                isProgressShowing.value = false
                if (isBottomProgress) {
                    isBottomProgressShowing.value = false
                }
                val rePurchaseMainListData = data as NSRePurchaseInfoResponse
                rePurchaseResponse = rePurchaseMainListData
                if (rePurchaseMainListData.data != null) {
                    if (rePurchaseMainListData.data.isValidList()) {
                        rePurchaseInfoList.addAll(rePurchaseMainListData.data)
                        isRePurchaseDataAvailable.value = rePurchaseInfoList.isValidList()
                    } else if (pageIndex == "1" || repurchaseId.isNotEmpty()){
                        isRePurchaseDataAvailable.value = false
                    }
                } else if (pageIndex == "1" || repurchaseId.isNotEmpty()){
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