package com.moneytree.app.ui.wallets.redeemForm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSDoctorRepository
import com.moneytree.app.repository.NSVoucherRepository
import com.moneytree.app.repository.NSWalletRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.*


/**
 * The view model class for redeem. It handles the business logic to communicate with the model for the redeem and provides the data to the observing UI component.
 */
class NSRedeemSaveViewModel(application: Application) : NSViewModel(application) {
    var availableBalance: String? = "0"

    /**
     * Get redeem list data
     *
     */
    fun redeemAmountSave(amount: String, password: String, isShowProgress: Boolean, callback: (Boolean, NSSuccessResponse) -> Unit) {
        if (isShowProgress) showProgress()

        callCommonApi({ obj ->
            NSWalletRepository.redeemWalletSaveMoney(amount, password, obj)
        }, { data, isSuccess ->
            hideProgress()
            if (isSuccess && data is NSSuccessResponse) {
                callback.invoke(true, data)
            } else {
                callback.invoke(isSuccess, NSSuccessResponse())
            }
        })
    }
}
