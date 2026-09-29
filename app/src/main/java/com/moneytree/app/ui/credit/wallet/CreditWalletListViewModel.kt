package com.moneytree.app.ui.credit.wallet

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.CreditWalletRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.CreditWalletData
import com.moneytree.app.repository.network.responses.CreditWalletListResponse


/**
 * The view model class for repurchase. It handles the business logic to communicate with the model for the repurchase and provides the data to the observing UI component.
 */
class CreditWalletListViewModel(application: Application) : NSViewModel(application) {
    var walletList: MutableList<CreditWalletData> = arrayListOf()
    var tempWalletList: MutableList<CreditWalletData> = arrayListOf()
    var walletDataAvailable = MutableLiveData<Boolean>()
    var pageIndex: String = "1"
    var searchedText: String = ""
    var selectedType: String = ""
    var startingDate: String = ""
    var endingDate: String = ""
    var walletBalance: String? = null
    var payoutListResponse: CreditWalletListResponse? = null
    
    fun getCreditWalletListData(pageIndex: String, isShowProgress: Boolean, isBottomProgress: Boolean) {
        if (pageIndex == "1") {
            walletList.clear()
        }
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        if (isBottomProgress) {
            isBottomProgressShowing.value = true
        }
        CreditWalletRepository.getCreditWalletList(pageIndex, searchedText, startingDate, endingDate, selectedType, object : NSGenericViewModelCallback {
            override fun <T> onSuccess(data: T) {
                isProgressShowing.value = false
                if (isBottomProgress) {
                    isBottomProgressShowing.value = false
                }
                val list = data as CreditWalletListResponse
                payoutListResponse = list
                if (list.walletAmount.isValidList()) {
                    walletBalance = list.walletAmount[0].amount
                }
                if (list.data != null) {
                    
                    if (list.data.isValidList()) {
                        walletList.addAll(list.data?: arrayListOf())
                        walletDataAvailable.value = walletList.isValidList()
                    } else if (pageIndex == "1" || searchedText.isNotEmpty()){
                        walletDataAvailable.value = false
                    }
                } else if (pageIndex == "1" || searchedText.isNotEmpty()){
                    walletDataAvailable.value = false
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
