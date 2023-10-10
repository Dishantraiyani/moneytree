package com.moneytree.app.ui.verified

import android.app.Activity
import android.app.Application
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.R
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSKycRepository
import com.moneytree.app.repository.NSVoucherRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.NSSuccessResponse
import com.moneytree.app.repository.network.responses.NSVoucherListData
import com.moneytree.app.repository.network.responses.NSVoucherListResponse
import com.moneytree.app.ui.vouchers.joining.NSJoiningVoucherFragment
import com.moneytree.app.ui.vouchers.joining.NSPendingVoucherFragment
import com.moneytree.app.ui.vouchers.joining.NSReceiveVoucherFragment
import com.moneytree.app.ui.vouchers.joining.NSTransferVoucherFragment
import com.moneytree.app.ui.vouchers.product.NSProductVoucherFragment


/**
 * The view model class for voucher. It handles the business logic to communicate with the model for the voucher and provides the data to the observing UI component.
 */
class NSKycViewModel(application: Application) : NSViewModel(application) {

    fun checkKycStatus(callback: ((String, Boolean) -> Unit)) {
        showProgress()

        callCommonApi({ obj ->
            NSKycRepository.checkKycVerification(obj)
        }, { data, isSuccess ->
            hideProgress()
            if (isSuccess) {
                if (data is NSSuccessResponse) {
                    callback.invoke(data.message?:"", true)
                }
            } else {
                callback.invoke("", false)
            }
        })
    }
}