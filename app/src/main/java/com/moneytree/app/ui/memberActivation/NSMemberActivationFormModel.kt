package com.moneytree.app.ui.memberActivation

import android.app.Activity
import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.moneytree.app.R
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSProductRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.*


/**
 * The view model class for redeem. It handles the business logic to communicate with the model for the redeem and provides the data to the observing UI component.
 */
class NSMemberActivationFormModel(application: Application) : NSViewModel(application) {
	var packageList: MutableList<NSActivationPackageData> = arrayListOf()
	var strPackageList: MutableList<String> = arrayListOf()
	var isPackageDataAvailable = MutableLiveData<Boolean>()
	var isActivePackageForm = MutableLiveData<Boolean>()
	var isMemberFormActive: Boolean = false
	var activationFormDetail: String? = null
	var memberFormDetail: String? = null
	var activationPackageResponse: NSActivationPackageResponse? = null
	//Spinner value for registration form
	var registrationType: MutableList<String> = arrayListOf()
	var activationResponse: NSSuccessResponse? = null
	var memberListData: NSRegisterListData? = null

	fun getMemberDetail() {
		if (memberFormDetail != null) {
			memberListData = Gson().fromJson(memberFormDetail, NSRegisterListData::class.java)
		}
	}

	fun getActivationDetail() {
		if (activationFormDetail != null) {
			activationPackageResponse = Gson().fromJson(activationFormDetail, NSActivationPackageResponse::class.java)
			strPackageList.clear()
			strPackageList.add("Select Package")
			if (activationPackageResponse != null) {
				packageList.addAll(activationPackageResponse!!.data)
				for (dt in activationPackageResponse!!.data) {
					strPackageList.add(dt.packageName!!)
				}

				isPackageDataAvailable.value = packageList.isValidList()
			}
		}
	}

	/**
	 * Get register list data
	 *
	 */
	fun saveActivation(memberId: String, registrationType: String, packageId: String, isShowProgress: Boolean) {
		if (isShowProgress) {
			isProgressShowing.value = true
		}
		NSProductRepository.getActivateDirectSave(memberId, registrationType, packageId, object : NSGenericViewModelCallback {
			override fun <T> onSuccess(data: T) {
				isProgressShowing.value = false
				activationResponse = data as NSSuccessResponse
				isActivePackageForm.value = true
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

	fun addRegistrationType(activity: Activity) {
		with(activity.resources) {
			val registration = getStringArray(R.array.registration_type_voucher)
			for (data in registration) {
				registrationType.add(data)
			}
		}
	}
}
