package com.moneytree.app.ui.mycart.purchaseComplete

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.repository.NSProductRepository
import com.moneytree.app.repository.NSUserRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.MemberDetailModel
import com.moneytree.app.repository.network.responses.NSMemberDetailResponse
import com.moneytree.app.repository.network.responses.NSSuccessResponse
import com.moneytree.app.repository.network.responses.ProductDataDTO


/**
 * The view model class for joining voucher. It handles the business logic to communicate with the model for the joining voucher and provides the data to the observing UI component.
 */
class NSPurchaseViewModel(application: Application) : NSViewModel(application) {
    var productList: MutableList<ProductDataDTO> = arrayListOf()
	var memberDetailModel: MemberDetailModel? = null
    var isMemberDataAvailable = MutableLiveData<Boolean>()
	var isProductSendDataAvailable = MutableLiveData<Boolean>()
	var successResponse: NSSuccessResponse? = null
	var selectedWalletType: String = ""

	fun getMemberDetail(memberId: String, isShowProgress: Boolean) {
		if (isShowProgress) {
			isProgressShowing.value = true
		}
		NSUserRepository.getMemberDetail(memberId, object : NSGenericViewModelCallback {
			override fun <T> onSuccess(data: T) {
				isProgressShowing.value = false
				val modelData = data as NSMemberDetailResponse
				if (modelData.status) {
					memberDetailModel = modelData.data
				}
				isMemberDataAvailable.value = true
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



	/**
	 * Get redeem list data
	 *
	 */
	fun saveMyCart(memberId: String,walletType: String, remark: String,productList: String, isShowProgress: Boolean) {
		if (isShowProgress) {
			isProgressShowing.value = true
		}
		NSProductRepository.saveMyCart(memberId, walletType, remark, productList, object: NSGenericViewModelCallback {
			override fun <T> onSuccess(data: T) {
				isProgressShowing.value = false
				successResponse = data as NSSuccessResponse
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
