package com.moneytree.app.ui.wallets.transfer

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSUserRepository
import com.moneytree.app.repository.NSVoucherRepository
import com.moneytree.app.repository.NSWalletRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.requests.NSWalletTransferModel
import com.moneytree.app.repository.network.responses.*


/**
 * The view model class for redeem. It handles the business logic to communicate with the model for the redeem and provides the data to the observing UI component.
 */
class NSTransferModel(application: Application) : NSViewModel(application),
	NSGenericViewModelCallback {
	var packageList: MutableList<NSPackageData> = arrayListOf()
	var strPackageList: MutableList<String> = arrayListOf()
	var quantityList: MutableList<NSPackageVoucherData> = arrayListOf()
	var isPackageDataAvailable = MutableLiveData<Boolean>()
	var isVoucherDataAvailable = MutableLiveData<Boolean>()
	var voucherQuantity: String? = null

	/**
	 * Get register list data
	 *
	 */
	fun getQuantity(packageId: String, isShowProgress: Boolean) {
		if (isShowProgress) {
			isProgressShowing.value = true
		}
		NSVoucherRepository.getPackageViseVoucherQty(packageId, object : NSGenericViewModelCallback {
			override fun <T> onSuccess(data: T) {
				isProgressShowing.value = false
				val packageMainData = data as NSPackageVoucherQntResponse
				if (packageMainData.data.isValidList()) {
					voucherQuantity = packageMainData.voucherCount.toString()
					quantityList.clear()
					quantityList.addAll(packageMainData.data)
					isVoucherDataAvailable.value = quantityList.isValidList()
				} else {
					voucherQuantity = "0"
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

		})
	}


	/**
	 * Get register list data
	 *
	 */
	fun getPackageListData(isShowProgress: Boolean) {
		if (isShowProgress) {
			isProgressShowing.value = true
		}
		NSVoucherRepository.packageMasterList(this)
	}

	override fun <T> onSuccess(data: T) {
		isProgressShowing.value = false
		val packageMainData = data as NSPackageResponse
		if (packageMainData.data.isValidList()) {
			packageList.clear()
			packageList.addAll(packageMainData.data)
			strPackageList.clear()
			strPackageList.add("Select Package Name")
			for (dt in packageList) {
				strPackageList.add(dt.packageName!!)
			}

			isPackageDataAvailable.value = packageList.isValidList()
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
