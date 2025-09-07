package com.moneytree.app.ui.vouchers.topupact.list

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.repository.NSVoucherRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.TopUpDashboardData
import com.moneytree.app.repository.network.responses.TopUpDashboardResponse


/**
 * The view model class for joining voucher. It handles the business logic to communicate with the model for the joining voucher and provides the data to the observing UI component.
 */
class TopUpVoucherActivationListViewModel(application: Application) : NSViewModel(application),
    NSGenericViewModelCallback {
    var isVoucherDataAvailable = MutableLiveData<TopUpDashboardData?>()
    var voucherResponse: TopUpDashboardData? = null
    
    fun getVoucherListData(isShowProgress: Boolean) {
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        NSVoucherRepository.getTopUpDashboardList(this)
    }

    override fun <T> onSuccess(data: T) {
        isProgressShowing.value = false
        val voucherMainListData = data as TopUpDashboardResponse?
        voucherResponse = voucherMainListData?.data
        isVoucherDataAvailable.value = voucherResponse
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