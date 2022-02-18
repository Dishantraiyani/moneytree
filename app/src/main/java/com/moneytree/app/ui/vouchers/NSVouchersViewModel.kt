package com.moneytree.app.ui.vouchers

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSVoucherRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.NSVoucherListData
import com.moneytree.app.repository.network.responses.NSVoucherListResponse


/**
 * The view model class for voucher. It handles the business logic to communicate with the model for the voucher and provides the data to the observing UI component.
 */
class NSVouchersViewModel(application: Application) : NSViewModel(application),
    NSGenericViewModelCallback {
    var voucherList: MutableList<NSVoucherListData> = arrayListOf()
    var tempVoucherList: MutableList<NSVoucherListData> = arrayListOf()
    var isVoucherDataAvailable = MutableLiveData<Boolean>()
    var pageIndex: String = "1"
    var voucherResponse: NSVoucherListResponse? = null
    var tabPosition = 0
    private var isBottomProgressShow: Boolean = false
    private var searchData: String = ""

    /**
     * Get voucher list data
     *
     */
    fun getVoucherListData(pageIndex: String, search: String, isShowProgress: Boolean, isBottomProgress: Boolean) {
        if (pageIndex == "1") {
            voucherList.clear()
        }
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        if (isBottomProgress) {
            isBottomProgressShowing.value = true
        }
        isBottomProgressShow = isBottomProgress
        searchData = search
        when (tabPosition) {
            0 -> {
                NSVoucherRepository.getJoiningVoucherPendingData(pageIndex, search, this)
            }
            1 -> {
                NSVoucherRepository.getJoiningVoucherReceiveData(pageIndex, search, this)
            }
            else -> {
                NSVoucherRepository.getJoiningVoucherTransferData(pageIndex, search, this)
            }
        }
    }

    override fun <T> onSuccess(data: T) {
        isProgressShowing.value = false
        if (isBottomProgressShow) {
            isBottomProgressShowing.value = false
        }
        val voucherMainListData = data as NSVoucherListResponse
        voucherResponse = voucherMainListData
        if (voucherMainListData.data != null) {
            if (voucherMainListData.data.isValidList()) {
                voucherList.addAll(voucherMainListData.data!!)
                isVoucherDataAvailable.value = voucherList.isValidList()
            } else if (pageIndex == "1" || searchData.isNotEmpty()){
                isVoucherDataAvailable.value = false
            }
        } else if (pageIndex == "1" || searchData.isNotEmpty()){
            isVoucherDataAvailable.value = false
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