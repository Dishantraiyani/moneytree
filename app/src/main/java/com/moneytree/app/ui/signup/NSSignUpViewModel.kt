package com.moneytree.app.ui.signup

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.repository.NSRegisterRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.NSSuccessResponse


/**
 * The view model class for register. It handles the business logic to communicate with the model for the register and provides the data to the observing UI component.
 */
class NSSignUpViewModel(application: Application) : NSViewModel(application) {
    var isRegisterDataAvailable = MutableLiveData<Boolean>()
    var isRegisterSuccessAvailable = MutableLiveData<Boolean>()

	/**
	 * Get register list data
	 *
	 */
	fun saveRegisterData(referCode: String, fullName: String, email: String, mobile: String, password: String, isShowProgress: Boolean) {
		if (isShowProgress) {
			isProgressShowing.value = true
		}
		NSRegisterRepository.saveRegisterDirectApi(referCode, fullName, email, mobile, password, object : NSGenericViewModelCallback {
			override fun <T> onSuccess(data: T) {
				isProgressShowing.value = false
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
}
