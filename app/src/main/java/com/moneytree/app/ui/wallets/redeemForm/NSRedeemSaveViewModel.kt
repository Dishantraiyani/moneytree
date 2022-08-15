package com.moneytree.app.ui.wallets.redeemForm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSVoucherRepository
import com.moneytree.app.repository.NSWalletRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.*


/**
 * The view model class for redeem. It handles the business logic to communicate with the model for the redeem and provides the data to the observing UI component.
 */
class NSRedeemSaveViewModel(application: Application) : NSViewModel(application),
    NSGenericViewModelCallback {
    var isRedeemWalletDataAvailable = MutableLiveData<Boolean>()
    var availableBalance: String? = "0"
	var successResponse: NSSuccessResponse? = null

    /**
     * Get redeem list data
     *
     */
    fun redeemAmountSave(amount: String, password: String, isShowProgress: Boolean) {
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        NSWalletRepository.redeemWalletSaveMoney(amount, password, this)
    }

    override fun <T> onSuccess(data: T) {
        isProgressShowing.value = false
        successResponse = data as NSSuccessResponse
		isRedeemWalletDataAvailable.value = true
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
