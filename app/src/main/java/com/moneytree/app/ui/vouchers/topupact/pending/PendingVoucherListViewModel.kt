package com.moneytree.app.ui.vouchers.topupact.pending

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.callbacks.NSJoiningVoucherCallback
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSVoucherRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.NSSuccessResponse
import com.moneytree.app.repository.network.responses.PendingVoucherListData
import com.moneytree.app.repository.network.responses.PendingVoucherListResponse


/**
 * The view model class for joining voucher. It handles the business logic to communicate with the model for the joining voucher and provides the data to the observing UI component.
 */
class PendingVoucherListViewModel(application: Application) : NSViewModel(application),
    NSGenericViewModelCallback {
    var pendingVoucherList: MutableList<PendingVoucherListData> = arrayListOf()
    var isVoucherDataAvailable = MutableLiveData<Boolean>()
    var isPendingDataAvailable = MutableLiveData<Boolean>()
    var voucherResponse: PendingVoucherListResponse? = null
    private var nsJoiningVoucherCallBack: NSJoiningVoucherCallback? = null
    var successResponse: NSSuccessResponse? = null
    
    fun topupVoucherClaim(isShowProgress: Boolean, voucherId: String?, deliveryType: String, productOption: String) {
        if (voucherId.isNullOrEmpty()) {
            return
        }
        isProgressShowing.value = isShowProgress
        NSVoucherRepository.topUpVoucherClaim(voucherId, deliveryType, productOption, object : NSGenericViewModelCallback {
            override fun <T> onSuccess(data: T) {
                isProgressShowing.value = false
                successResponse = data as NSSuccessResponse?
                isPendingDataAvailable.value = true
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
    
    fun getVoucherListData(isShowProgress: Boolean, joiningVoucherCallBack: NSJoiningVoucherCallback) {
        nsJoiningVoucherCallBack = joiningVoucherCallBack
        pendingVoucherList.clear()
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        NSVoucherRepository.getPendingVoucherList(this)
    }

    override fun <T> onSuccess(data: T) {
        isProgressShowing.value = false
        val voucherMainListData = data as PendingVoucherListResponse?
        voucherResponse = voucherMainListData
        pendingVoucherList.addAll(voucherMainListData?.data?: arrayListOf())
        nsJoiningVoucherCallBack?.onResponse(pendingVoucherList.isValidList())
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