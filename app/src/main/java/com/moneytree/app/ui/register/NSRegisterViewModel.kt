package com.moneytree.app.ui.register

import android.app.Activity
import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.R
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSProductRepository
import com.moneytree.app.repository.NSRegisterRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.*


/**
 * The view model class for register. It handles the business logic to communicate with the model for the register and provides the data to the observing UI component.
 */
class NSRegisterViewModel(application: Application) : NSViewModel(application),
	NSGenericViewModelCallback {
	var registerList: MutableList<NSRegisterListData> = arrayListOf()
	var tempRegisterList: MutableList<NSRegisterListData> = arrayListOf()
	var isRegisterDataAvailable = MutableLiveData<Boolean>()
	var isRegisterSuccessAvailable = MutableLiveData<Boolean>()
	var pageIndex: String = "1"
	var registerResponse: NSRegisterListResponse? = null
	private var isBottomProgressShow: Boolean = false
	private var searchData: String = ""
	var activationPackageResponse: NSActivationPackageResponse? = null
	var activationPackageList: MutableList<NSActivationPackageData> = arrayListOf()
	var isActivationPackageDataAvailable = MutableLiveData<Boolean>()
	var dataMember: NSRegisterListData? = null
	var successResponse: NSSuccessResponse? = null

	var startingDate: String = ""
	var endingDate: String = ""
	var selectedType: String = ""

	//Spinner value for registration form
	var registrationType: MutableList<String> = arrayListOf()

	/**
	 * Get register list data
	 *
	 */
	fun saveRegisterData(fullName: String, email: String, mobile: String, password: String, isShowProgress: Boolean) {
		if (isShowProgress) {
			isProgressShowing.value = true
		}
		NSRegisterRepository.saveRegisterApi(fullName, email, mobile, password, object : NSGenericViewModelCallback {
			override fun <T> onSuccess(data: T) {
				isProgressShowing.value = false
				successResponse = data as NSSuccessResponse
				isRegisterSuccessAvailable.value = true
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
	fun getRegisterListData(pageIndex: String, search: String, isShowProgress: Boolean, isBottomProgress: Boolean) {
		if (pageIndex == "1") {
			registerList.clear()
		}
		if (isShowProgress) {
			isProgressShowing.value = true
		}
		if (isBottomProgress) {
			isBottomProgressShowing.value = true
		}
		isBottomProgressShow = isBottomProgress
		searchData = search
		NSRegisterRepository.getRegisterListData(pageIndex, search, selectedType, startingDate, endingDate, this)
	}

	override fun <T> onSuccess(data: T) {
		isProgressShowing.value = false
		if (isBottomProgressShow) {
			isBottomProgressShowing.value = false
		}
		val registerMainListData = data as NSRegisterListResponse
		registerResponse = registerMainListData
		if (registerMainListData.data != null) {
			if (registerMainListData.data.isValidList()) {
				registerList.addAll(registerMainListData.data)
				isRegisterDataAvailable.value = registerList.isValidList()
			} else if (pageIndex == "1" || searchData.isNotEmpty()){
				isRegisterDataAvailable.value = false
			}
		} else if (pageIndex == "1" || searchData.isNotEmpty()){
			isRegisterDataAvailable.value = false
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

	fun addRegistrationType(activity: Activity) {
		with(activity.resources) {
			val registration = getStringArray(R.array.registration_type)
			for (data in registration) {
				registrationType.add(data)
			}
		}
	}

	fun getActivationPackage(memberId: String, isShowProgress: Boolean) {
		if (isShowProgress) {
			isProgressShowing.value = true
		}
		NSProductRepository.getMemberActivatePackage(memberId, object : NSGenericViewModelCallback {
			override fun <T> onSuccess(data: T) {
				isProgressShowing.value = false
				if (isBottomProgressShow) {
					isBottomProgressShowing.value = false
				}
				val activationListData = data as NSActivationPackageResponse
				if (activationListData.data != null) {
					activationPackageResponse = activationListData
					if (activationListData.data.isValidList()) {
						activationPackageList.addAll(activationListData.data)
					}
				}
				isActivationPackageDataAvailable.value = activationPackageList.isValidList()
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

	fun setDefault(userId: String, isShowProgress: Boolean) {
		if (isShowProgress) {
			isProgressShowing.value = true
		}
		NSRegisterRepository.setDefault(userId, object : NSGenericViewModelCallback {
			override fun <T> onSuccess(data: T) {
				isProgressShowing.value = false

				pageIndex = "1"
				getRegisterListData(pageIndex, "", false, isBottomProgress = false)
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

	fun sendMessage(userId: String, isShowProgress: Boolean) {
		if (isShowProgress) {
			isProgressShowing.value = true
		}
		NSRegisterRepository.sendMessage(userId, object : NSGenericViewModelCallback {
			override fun <T> onSuccess(data: T) {
				isProgressShowing.value = false

				val response = data as NSSendMessageResponse
				//Show Success dialog
				handleFailure(response.message)
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
}
