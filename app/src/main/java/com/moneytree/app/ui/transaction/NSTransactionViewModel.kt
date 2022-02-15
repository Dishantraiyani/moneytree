package com.moneytree.app.ui.transaction

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.network.responses.NSTransactionListData

/**
 * The view model class for transaction. It handles the business logic to communicate with the model for the transaction item and provides the data to the observing UI component.
 */
class NSTransactionViewModel(application: Application) : NSViewModel(application) {
    var transactionList: MutableList<NSTransactionListData> = arrayListOf()
    var isTransactionDataAvailable = MutableLiveData<Boolean>()

    /**
     * Get transaction list data
     *
     */
    fun getTransactionData(isShowProgress: Boolean) {
        transactionList.clear()
        if (isShowProgress) {
            isProgressShowing.value = true
        }

        val trans1 = NSTransactionListData("546383", "Credit", "21 Dec 2021 10:10 AM", 100.5)
        val trans2 = NSTransactionListData("546384", "Debit", "21 Dec 2021 10:10 AM", 50.5)

        for (data in 0..5) {
            transactionList.add(trans1)
            transactionList.add(trans2)
        }

        isProgressShowing.value = false
        isTransactionDataAvailable.value = transactionList.isValidList()
    }
}