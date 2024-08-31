package com.moneytree.app.ui.coins.transaction

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSWalletRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.NSWalletData
import com.moneytree.app.repository.network.responses.NSWalletListResponse


/**
 * The view model class for transaction. It handles the business logic to communicate with the model for the transaction and provides the data to the observing UI component.
 */
class NSCoinTransactionViewModel(application: Application) : NSViewModel(application),
    NSGenericViewModelCallback {
    var transactionList: MutableList<NSWalletData> = arrayListOf()
    var tempTransactionList: MutableList<NSWalletData> = arrayListOf()
    var isTransactionDataAvailable = MutableLiveData<Boolean>()
    var pageIndex: String = "1"
    var transactionResponse: NSWalletListResponse? = null
    private var isBottomProgressShow: Boolean = false
    private var searchData: String = ""
	var startingDate: String = ""
	var endingDate: String = ""
	var selectedType: String = ""

    /**
     * Get transaction list data
     *
     */
    fun getTransactionListData(pageIndex: String, search: String, isShowProgress: Boolean, isBottomProgress: Boolean) {
        if (pageIndex == "1") {
            transactionList.clear()
        }
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        if (isBottomProgress) {
            isBottomProgressShowing.value = true
        }
        isBottomProgressShow = isBottomProgress
        searchData = search
        NSWalletRepository.getCoinWalletList(pageIndex, search, startingDate, endingDate, selectedType, this)
    }

    override fun <T> onSuccess(data: T) {
        isProgressShowing.value = false
        if (isBottomProgressShow) {
            isBottomProgressShowing.value = false
        }
        val transactionMainListData = data as NSWalletListResponse
        transactionResponse = transactionMainListData
        if (transactionMainListData.data.isValidList()) {
			transactionList.addAll(transactionMainListData.data)
			isTransactionDataAvailable.value = transactionList.isValidList()
		} else if (pageIndex == "1" || searchData.isNotEmpty()){
			isTransactionDataAvailable.value = false
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
