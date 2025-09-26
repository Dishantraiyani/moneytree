package com.moneytree.app.ui.vouchers.topupact

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.callbacks.NSUserDataCallback
import com.moneytree.app.database.MainDatabase
import com.moneytree.app.repository.NSVoucherRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.requests.VoucherActiveSaveModel
import com.moneytree.app.repository.network.requests.VoucherTransferModel
import com.moneytree.app.repository.network.responses.*


/**
 * The view model class for redeem. It handles the business logic to communicate with the model for the redeem and provides the data to the observing UI component.
 */
class TopUpVoucherActivationModel(application: Application) : NSViewModel(application) {
	var packageList: MutableList<NSPackageData> = arrayListOf()
	var isPackageDataAvailable = MutableLiveData<Boolean>()
	var isVoucherDataAvailable = MutableLiveData<Boolean>()
	var memberTransferId: String? = null
	var selectedDspId: String? = null
	var selectedSponsorId: String? = null
	var memberDetailModel: NSMemberDetailResponse? = null
	var isMemberDataAvailable = MutableLiveData<Boolean>()

	/**
	 * Get register list data
	 *
	 */
	fun getVoucherList(isShowProgress: Boolean, callback: (MutableList<TopUpSVoucherAvailableListData>) -> Unit) {
		if (isShowProgress) {
			isProgressShowing.value = true
		}
		//{"voucher_id":"2","voucher_code":"6397104171"}
		NSVoucherRepository.getTopUpVoucherAvailableList(object : NSGenericViewModelCallback {
			override fun <T> onSuccess(data: T) {
				isProgressShowing.value = false
				val packageMainData = data as TopUpVoucherAvailableListResponse?
				callback.invoke(packageMainData?.data?: arrayListOf())
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

	fun checkDspSponsor(memberType: String, memberCode: String, callback: (Boolean, DspAndSponsorModel) -> Unit) {
		var dspAndSponsorModel = DspAndSponsorModel(status = false, message = if(memberType == "DSP") "Dsp Id Not Found." else "Sponsor Id Not Found.")
		isProgressShowing.value = true
		NSVoucherRepository.checkDspAndSponsor(memberType, memberCode, object : NSGenericViewModelCallback {
			override fun <T> onSuccess(data: T) {
				isProgressShowing.value = false
				var dspAndSponsor = data as DspAndSponsorModel?
				if (dspAndSponsor == null) {
					dspAndSponsor = dspAndSponsorModel
				}
				callback.invoke(dspAndSponsor.status, dspAndSponsor)
			}

			override fun onError(errors: List<Any>) {
				//callback.invoke(false, dspAndSponsorModel)
				handleError(errors)
			}

			override fun onFailure(failureMessage: String?) {
				//callback.invoke(false, dspAndSponsorModel)
				handleFailure(failureMessage)
			}

			override fun <T> onNoNetwork(localData: T) {
				//callback.invoke(false, dspAndSponsorModel)
				handleNoNetwork()
			}
		})
	}
	
	fun saveVoucherTransfer(model: VoucherActiveSaveModel, isShowProgress: Boolean, callback: () -> Unit) {
		if (isShowProgress) {
			isProgressShowing.value = true
		}
		NSVoucherRepository.saveTopUpActiveVoucher(model, object : NSGenericViewModelCallback {
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
