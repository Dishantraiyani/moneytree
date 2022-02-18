package com.moneytree.app.ui.downlineReOffer

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSDownlineMemberReOfferRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.NSDownlineMemberDirectReOfferData
import com.moneytree.app.repository.network.responses.NSDownlineMemberDirectReOfferResponse

/**
 * The view model class for downline reOffer. It handles the business logic to communicate with the model for the downline reOffer and provides the data to the observing UI component.
 */
class NSDownlineReOfferViewModel(application: Application) : NSViewModel(application),
    NSGenericViewModelCallback {
    var downlineList: MutableList<NSDownlineMemberDirectReOfferData> = arrayListOf()
    var isDownlineDataAvailable = MutableLiveData<Boolean>()
    private var downlineResponse: NSDownlineMemberDirectReOfferResponse? = null

    /**
     * Get downline list data
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
        val downlineMainListData = data as NSDownlineMemberDirectReOfferResponse
        downlineResponse = downlineMainListData
        if (downlineMainListData.data != null) {
            if (downlineMainListData.data.isValidList()) {
                downlineList.addAll(downlineMainListData.data!!)
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