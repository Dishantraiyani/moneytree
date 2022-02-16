package com.moneytree.app.ui.downlineReOffer

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSDownlineMemberReOfferRepository
import com.moneytree.app.repository.NSVoucherRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.NSDownlineMemberDirectReOfferData
import com.moneytree.app.repository.network.responses.NSDownlineMemberDirectReOfferResponse
import com.moneytree.app.repository.network.responses.NSVoucherListData
import com.moneytree.app.repository.network.responses.NSVoucherListResponse


/**
 * The view model class for trip history. It handles the business logic to communicate with the model for the trip history and provides the data to the observing UI component.
 */
class NSDownlineReOfferViewModel(application: Application) : NSViewModel(application),
    NSGenericViewModelCallback {
    var downlineList: MutableList<NSDownlineMemberDirectReOfferData> = arrayListOf()
    var tempDownlineList: MutableList<NSDownlineMemberDirectReOfferData> = arrayListOf()
    var isDownlineDataAvailable = MutableLiveData<Boolean>()
    var downlineResponse: NSDownlineMemberDirectReOfferResponse? = null

    /**
     * Get register list data
     *
     */
    fun getDownlineListData(isShowProgress: Boolean) {
        downlineList.clear()
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        NSDownlineMemberReOfferRepository.getDownlineMemberReOfferListData(this)
    }

    override fun <T> onSuccess(data: T) {
        isProgressShowing.value = false
        val registerMainListData = data as NSDownlineMemberDirectReOfferResponse
        downlineResponse = registerMainListData
        if (registerMainListData.data != null) {
            if (registerMainListData.data.isValidList()) {
                downlineList.addAll(registerMainListData.data!!)
            }
            isDownlineDataAvailable.value = downlineList.isValidList()
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