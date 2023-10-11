package com.moneytree.app.ui.mycart.placeOrder

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSApplication
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.callbacks.NSUserDataCallback
import com.moneytree.app.database.MainDatabase
import com.moneytree.app.repository.NSRechargeRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.NSDataUser
import com.moneytree.app.repository.network.responses.NSSuccessResponse


/**
 * The view model class for joining voucher. It handles the business logic to communicate with the model for the joining voucher and provides the data to the observing UI component.
 */
class NSPlaceOrderViewModel(application: Application) : NSViewModel(application) {
	var isProductSendDataAvailable = MutableLiveData<Boolean>()
	var successResponse: NSSuccessResponse? = null
	var finalAmount: String = "0"
	var isFromOrder: Boolean = false
	var isDefaultAddress: Boolean = false

	fun getUserDetail(callback: (NSDataUser) -> Unit) {
		MainDatabase.getUserData(object : NSUserDataCallback {
			override fun onResponse(userDetail: NSDataUser) {
				callback.invoke(userDetail)
			}
		})
	}

	/**
	 * Get redeem list data
	 *
	 */
	fun placeOrder(orderId: String, paymentData: String, address: String, productList: String, amount: String, isShowProgress: Boolean) {
		if (isShowProgress) {
			isProgressShowing.value = true
		}
		NSRechargeRepository.placeOrder(orderId, paymentData, amount, address, productList, object: NSGenericViewModelCallback {
			override fun <T> onSuccess(data: T) {
				isProgressShowing.value = false
				successResponse = data as NSSuccessResponse
				if (successResponse?.status == true) {
					NSApplication.getInstance().clearOrderList()
				}
				isProductSendDataAvailable.value = true
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
		} )
	}
}
