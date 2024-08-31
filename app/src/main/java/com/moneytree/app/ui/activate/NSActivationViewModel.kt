package com.moneytree.app.ui.activate

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSProductRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.NSActivationData
import com.moneytree.app.repository.network.responses.NSActivationListResponse
import com.moneytree.app.repository.network.responses.NSActivationPackageData
import com.moneytree.app.repository.network.responses.NSActivationPackageResponse


/**
 * The view model class for joining voucher. It handles the business logic to communicate with the model for the joining voucher and provides the data to the observing UI component.
 */
class NSActivationViewModel(application: Application) : NSViewModel(application),
    NSGenericViewModelCallback {
    var activationList: MutableList<NSActivationData> = arrayListOf()
    var activationPackageList: MutableList<NSActivationPackageData> = arrayListOf()
    var tempActivationList: MutableList<NSActivationData> = arrayListOf()
    var isActivationDataAvailable = MutableLiveData<Boolean>()
    var isActivationPackageDataAvailable = MutableLiveData<Boolean>()
    var pageIndex: String = "1"
    var productResponse: NSActivationListResponse? = null
    var activationPackageResponse: NSActivationPackageResponse? = null
    private var isBottomProgressShow: Boolean = false
    private var searchData: String = ""

	fun getActivationPackage(isShowProgress: Boolean) {
		if (isShowProgress) {
			isProgressShowing.value = true
		}
		NSProductRepository.getActivatePackage(object : NSGenericViewModelCallback {
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

    /**
     * Get voucher list data
     *
     */
    fun getActivationListData(pageIndex: String, search: String, isShowProgress: Boolean, isBottomProgress: Boolean) {
        if (pageIndex == "1") {
            activationList.clear()
        }
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        if (isBottomProgress) {
            isBottomProgressShowing.value = true
        }
        isBottomProgressShow = isBottomProgress
        searchData = search
        NSProductRepository.getActivationList(pageIndex, search, this)
    }

    override fun <T> onSuccess(data: T) {
        isProgressShowing.value = false
        if (isBottomProgressShow) {
            isBottomProgressShowing.value = false
        }
        val activationListData = data as NSActivationListResponse
        productResponse = activationListData
        if (activationListData.data.isValidList()) {
            activationList.addAll(activationListData.data)
            isActivationDataAvailable.value = activationList.isValidList()
        } else if (pageIndex == "1" || searchData.isNotEmpty()){
            isActivationDataAvailable.value = false
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
