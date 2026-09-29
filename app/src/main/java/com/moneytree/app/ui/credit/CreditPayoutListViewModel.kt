package com.moneytree.app.ui.credit

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.CreditPayoutRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.CreditPayoutListResponse
import com.moneytree.app.repository.network.responses.CreditPayoutData
import com.moneytree.app.repository.network.responses.CreditPayoutSummary


/**
 * The view model class for repurchase. It handles the business logic to communicate with the model for the repurchase and provides the data to the observing UI component.
 */
class CreditPayoutListViewModel(application: Application) : NSViewModel(application) {
    var payoutList: MutableList<CreditPayoutData> = arrayListOf()
    var payoutSummary: CreditPayoutSummary? = null
    var tempPayoutList: MutableList<CreditPayoutData> = arrayListOf()
    var payoutDataAvailable = MutableLiveData<Boolean>()
    var pageIndex: String = "1"
    var searchedText: String = ""
    var selectedPaymentStatus: String = ""
    var payoutListResponse: CreditPayoutListResponse? = null
    
    fun getPayoutListData(pageIndex: String, isShowProgress: Boolean, isBottomProgress: Boolean) {
        if (pageIndex == "1") {
            payoutList.clear()
        }
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        if (isBottomProgress) {
            isBottomProgressShowing.value = true
        }
        CreditPayoutRepository.getCreditPayoutList(pageIndex, searchedText, selectedPaymentStatus, object : NSGenericViewModelCallback {
            override fun <T> onSuccess(data: T) {
                isProgressShowing.value = false
                if (isBottomProgress) {
                    isBottomProgressShowing.value = false
                }
                val rePurchaseMainListData = data as CreditPayoutListResponse
                payoutListResponse = rePurchaseMainListData
                if (rePurchaseMainListData.data != null) {
                    payoutSummary = if(rePurchaseMainListData.summary.isValidList()) rePurchaseMainListData.summary?.first() else null
                    
                    if (rePurchaseMainListData.data.isValidList()) {
                        payoutList.addAll(rePurchaseMainListData.data!!)
                        payoutDataAvailable.value = payoutList.isValidList()
                    } else if (pageIndex == "1" || searchedText.isNotEmpty()){
                        payoutDataAvailable.value = false
                    }
                } else if (pageIndex == "1" || searchedText.isNotEmpty()){
                    payoutDataAvailable.value = false
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
