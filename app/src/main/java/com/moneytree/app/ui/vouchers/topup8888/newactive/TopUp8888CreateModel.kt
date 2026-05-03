package com.moneytree.app.ui.vouchers.topup8888.newactive

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
class TopUp8888CreateModel(application: Application) : NSViewModel(application) {
	var availablePins: Int = 0
	var memberList: List<TopUp8888MemberListData> = arrayListOf()
	var isPackageDataAvailable = MutableLiveData<Boolean>()
	var isVoucherDataAvailable = MutableLiveData<Boolean>()
	var voucherQuantity: String? = null
	var memberTransferId: String? = null
	var memberDetailModel: NSMemberDetailResponse? = null
	var isMemberDataAvailable = MutableLiveData<Boolean>()
	var selectedMember: TopUp8888MemberListData? = null
	
	/**
	 * Get register list data
	 *
	 */
	fun getMemberList(isShowProgress: Boolean, callback: (TopUp8888MemberData?) -> Unit) {
		if (isShowProgress) {
			isProgressShowing.value = true
		}
		NSVoucherRepository.getTopUpVoucher8888MemberList(object : NSGenericViewModelCallback {
			override fun <T> onSuccess(data: T) {
				isProgressShowing.value = false
				val response = data as TopUp8888MemberListResponse?
				callback.invoke(response?.data)
			}

			override fun onError(errors: List<Any>) {
				callback.invoke(null)
				handleError(errors)
			}

			override fun onFailure(failureMessage: String?) {
				callback.invoke(null)
				handleFailure(failureMessage)
			}

			override fun <T> onNoNetwork(localData: T) {
				callback.invoke(null)
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
	
	fun saveTopup8888(membered: String, sponsorId: String, isShowProgress: Boolean, callback: (String) -> Unit) {
		if (isShowProgress) {
			isProgressShowing.value = true
		}
		NSVoucherRepository.saveTopUpVoucher8888(membered, sponsorId,object : NSGenericViewModelCallback {
			override fun <T> onSuccess(data: T) {
				isProgressShowing.value = false
				val response = data as NSSuccessResponse?
				callback.invoke(response?.message?:"")
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
