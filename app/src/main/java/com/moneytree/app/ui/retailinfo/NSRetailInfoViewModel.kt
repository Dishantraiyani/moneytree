package com.moneytree.app.ui.retailinfo

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSRetailRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.NSRetailInfoData
import com.moneytree.app.repository.network.responses.NSRetailInfoResponse


/**
 * The view model class for trip history. It handles the business logic to communicate with the model for the trip history and provides the data to the observing UI component.
 */
class NSRetailInfoViewModel(application: Application) : NSViewModel(application) {
    var retailInfoList: MutableList<NSRetailInfoData> = arrayListOf()
    var isRetailDataAvailable = MutableLiveData<Boolean>()
    var pageIndex: String = "1"
    var retailResponse: NSRetailInfoResponse? = null
    var retailId: String? = null

    /**
     * Get register list data
     *
     */
    fun getRetailListData(pageIndex: String, retailId: String, isShowProgress: Boolean, isBottomProgress: Boolean) {
        if (pageIndex == "1") {
            retailInfoList.clear()
        }
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        if (isBottomProgress) {
            isBottomProgressShowing.value = true
        }
        NSRetailRepository.getRetailInfoData(pageIndex, retailId, object : NSGenericViewModelCallback {
            override fun <T> onSuccess(data: T) {
                isProgressShowing.value = false
                if (isBottomProgress) {
                    isBottomProgressShowing.value = false
                }
                val retailMainListData = data as NSRetailInfoResponse
                retailResponse = retailMainListData
                if (retailMainListData.data != null) {
                    if (retailMainListData.data.isValidList()) {
                        retailInfoList.addAll(retailMainListData.data!!)
                        isRetailDataAvailable.value = retailInfoList.isValidList()
                    } else if (pageIndex == "1" || retailId.isNotEmpty()){
                        isRetailDataAvailable.value = false
                    }
                } else if (pageIndex == "1" || retailId.isNotEmpty()){
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