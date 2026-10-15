package com.moneytree.app.ui.repurchaseAct

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.callbacks.NSUserDataCallback
import com.moneytree.app.database.MainDatabase
import com.moneytree.app.repository.NSVoucherRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.requests.VoucherActiveSaveModel
import com.moneytree.app.repository.network.responses.*


/**
 * The view model class for redeem. It handles the business logic to communicate with the model for the redeem and provides the data to the observing UI component.
 */
class Repurchase3000ActivationModel(application: Application) : NSViewModel(application) {
	var packageList: MutableList<NSPackageData> = arrayListOf()
	var isPackageDataAvailable = MutableLiveData<Boolean>()
	var isVoucherDataAvailable = MutableLiveData<Boolean>()
	var memberDetailModel: NSMemberDetailResponse? = null
	var isMemberDataAvailable = MutableLiveData<Boolean>()
	var downlineList: List<Repurchase3000DownLineListItem>? = null
	var activationList: List<ActivationListItem>? = null

	/**
	 * Get register list data
	 *
	 */
	fun getTopUpVoucher30000Dashboard(isShowProgress: Boolean, callback: (Repurchase3000Data?) -> Unit) {
		if (isShowProgress) {
			isProgressShowing.value = true
		}
		//{"voucher_id":"2","voucher_code":"6397104171"}
		NSVoucherRepository.getTopUpVoucher30000Dashboard(object : NSGenericViewModelCallback {
			override fun <T> onSuccess(data: T) {
				isProgressShowing.value = false
				val topUpData = data as Repurchase30000Response?
				callback.invoke(topUpData?.data)
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


	fun getTopUpVoucher30000VoucherList(isShowProgress: Boolean, voucherId: String?, callback: (List<VoucherList30000DataItem>) -> Unit) {
		if (isShowProgress) {
			isProgressShowing.value = true
		}
		NSVoucherRepository.getTopUpVoucher30000VoucherList(voucherId?:"", object : NSGenericViewModelCallback {
			override fun <T> onSuccess(data: T) {
				isProgressShowing.value = false
				val topUpData = data as VoucherList30000Response?
				callback.invoke(topUpData?.data?: arrayListOf())
			}

			override fun onError(errors: List<Any>) {
				handleError(errors)
				callback.invoke(arrayListOf())
			}

			override fun onFailure(failureMessage: String?) {
				handleFailure(failureMessage)
				callback.invoke(arrayListOf())
			}

			override fun <T> onNoNetwork(localData: T) {
				callback.invoke(arrayListOf())
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
