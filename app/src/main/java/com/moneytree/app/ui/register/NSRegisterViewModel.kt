package com.moneytree.app.ui.register

import android.app.Activity
import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.R
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSRegisterRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.NSRegisterListData
import com.moneytree.app.repository.network.responses.NSRegisterListResponse
import com.moneytree.app.repository.network.responses.NSSuccessResponse


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
				val registerMainData = data as NSSuccessResponse
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
        NSRegisterRepository.getRegisterListData(pageIndex, search, this)
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
                registerList.addAll(registerMainListData.data!!)
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
}
