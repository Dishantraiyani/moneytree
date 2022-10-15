package com.moneytree.app.ui.qrCode

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.common.utils.switchActivity
import com.moneytree.app.repository.NSRechargeRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.requests.NSRechargeSaveRequest
import com.moneytree.app.repository.network.responses.*
import com.moneytree.app.ui.success.SuccessActivity


/**
 * The view model class for redeem. It handles the business logic to communicate with the model for the redeem and provides the data to the observing UI component.
 */
class NSQRCodeViewModel(application: Application) : NSViewModel(application) {
	var name: String = ""
	var upiId: String = ""
	var qrCodeId: String? = ""
	var walletAmount: String? = "0"
	var amount: String? = "0.0"
	var note: String? = ""
    var successResponse: NSSuccessResponse? = null

	fun qrCodeRecharge(activity: Activity) {
		isProgressShowing.value = true
		NSRechargeRepository.qrCodeRecharge(qrCodeId!!, amount!!, note!!, name, upiId,
			object : NSGenericViewModelCallback {
				override fun <T> onSuccess(data: T) {
					isProgressShowing.value = false
					successResponse = data as NSSuccessResponse
					openSuccessFail(activity)
				}

				override fun onError(errors: List<Any>) {
					openSuccessFail(activity)
				}

				override fun onFailure(failureMessage: String?) {
					openSuccessFail(activity)
				}

				override fun <T> onNoNetwork(localData: T) {
					handleNoNetwork()
				}

			})
	}

	private fun openSuccessFail(activity: Activity) {
		activity.switchActivity(
			SuccessActivity::class.java,
			flags = intArrayOf(Intent.FLAG_ACTIVITY_CLEAR_TOP), bundle = bundleOf(NSConstants.KEY_SUCCESS_FAIL to if (successResponse == null) "" else Gson().toJson(successResponse))
		)
		activity.finish()
	}
}
