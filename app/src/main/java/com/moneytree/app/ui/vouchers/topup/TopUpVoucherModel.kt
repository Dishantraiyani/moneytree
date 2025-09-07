package com.moneytree.app.ui.vouchers.topup

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.callbacks.NSUserDataCallback
import com.moneytree.app.database.MainDatabase
import com.moneytree.app.repository.NSUserRepository
import com.moneytree.app.repository.NSVoucherRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.requests.VoucherTransferModel
import com.moneytree.app.repository.network.responses.*


/**
 * The view model class for redeem. It handles the business logic to communicate with the model for the redeem and provides the data to the observing UI component.
 */
class TopUpVoucherModel(application: Application) : NSViewModel(application) {
	var packageList: MutableList<NSPackageData> = arrayListOf()
	var isPackageDataAvailable = MutableLiveData<Boolean>()
	var isVoucherDataAvailable = MutableLiveData<Boolean>()
	var voucherQuantity: String? = null
	var memberTransferId: String? = null
	var memberDetailModel: NSMemberDetailResponse? = null
	var isMemberDataAvailable = MutableLiveData<Boolean>()

	/**
	 * Get register list data
	 *
	 */
	fun getQuantity(isShowProgress: Boolean) {
		if (isShowProgress) {
			isProgressShowing.value = true
		}
		NSVoucherRepository.getTopUpVoucherQuantity(object : NSGenericViewModelCallback {
			override fun <T> onSuccess(data: T) {
				isProgressShowing.value = false
				val packageMainData = data as TopUpVoucherQntResponse?
				voucherQuantity = packageMainData?.voucherQty?:"0"
				isVoucherDataAvailable.value = true
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

	fun getMemberDetail(memberId: String, isShowProgress: Boolean) {
		if (isShowProgress) {
			isProgressShowing.value = true
		}
		NSUserRepository.getMemberDetail(memberId, object : NSGenericViewModelCallback {
			override fun <T> onSuccess(data: T) {
				isProgressShowing.value = false
				memberDetailModel = data as NSMemberDetailResponse?
				if (memberDetailModel == null) {
					memberDetailModel = NSMemberDetailResponse(status = false, message = "Member Not Found.")
				}

				isMemberDataAvailable.value = memberDetailModel?.status == true
			}

			override fun onError(errors: List<Any>) {
				memberDetailModel = null
				isMemberDataAvailable.value = false
				handleError(errors)
			}

			override fun onFailure(failureMessage: String?) {
				memberDetailModel = null
				isMemberDataAvailable.value = false
				handleFailure(failureMessage)
			}

			override fun <T> onNoNetwork(localData: T) {
				memberDetailModel = null
				isMemberDataAvailable.value = false
				handleNoNetwork()
			}
		})
	}
	
	fun saveVoucherTransfer(model: VoucherTransferModel, isShowProgress: Boolean, callback: () -> Unit) {
		if (isShowProgress) {
			isProgressShowing.value = true
		}
		NSVoucherRepository.saveTopUpVoucher(model, object : NSGenericViewModelCallback {
			override fun <T> onSuccess(data: T) {
				isProgressShowing.value = false
				callback.invoke()
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

	fun getUserDetail(callback: (Boolean) -> Unit) {
		MainDatabase.getUserData(object : NSUserDataCallback {
			override fun onResponse(userDetail: NSDataUser) {
				callback.invoke(userDetail.fullName?.isNotEmpty() == true)
			}
		})
	}
}
