package com.moneytree.app.ui.retail

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSRetailRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.NSRetailListData
import com.moneytree.app.repository.network.responses.NSRetailListResponse


/**
 * The view model class for trip history. It handles the business logic to communicate with the model for the trip history and provides the data to the observing UI component.
 */
class NSRetailListViewModel(application: Application) : NSViewModel(application) {
    var retailList: MutableList<NSRetailListData> = arrayListOf()
    var tempRetailList: MutableList<NSRetailListData> = arrayListOf()
    var isRetailDataAvailable = MutableLiveData<Boolean>()
    var pageIndex: String = "1"
    var retailResponse: NSRetailListResponse? = null

    /**
     * Get register list data
     *
     */
    fun getRetailListData(pageIndex: String, search: String, isShowProgress: Boolean, isBottomProgress: Boolean) {
        if (pageIndex == "1") {
            retailList.clear()
        }
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        if (isBottomProgress) {
            isBottomProgressShowing.value = true
        }
        NSRetailRepository.getRetailListData(pageIndex, search, object : NSGenericViewModelCallback {
            override fun <T> onSuccess(data: T) {
                isProgressShowing.value = false
                if (isBottomProgress) {
                    isBottomProgressShowing.value = false
                }
                val rePurchaseMainListData = data as NSRetailListResponse
                retailResponse = rePurchaseMainListData
                if (rePurchaseMainListData.data != null) {
                    if (rePurchaseMainListData.data.isValidList()) {
                        retailList.addAll(rePurchaseMainListData.data!!)
                        isRetailDataAvailable.value = retailList.isValidList()
                    } else if (pageIndex == "1" || search.isNotEmpty()){
                        isRetailDataAvailable.value = false
                    }
                } else if (pageIndex == "1" || search.isNotEmpty()){
                    isRetailDataAvailable.value = false
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