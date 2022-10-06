package com.moneytree.app.ui.recharge

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.common.utils.switchActivity
import com.moneytree.app.repository.NSRechargeRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.requests.NSRechargeSaveRequest
import com.moneytree.app.repository.network.responses.*
import com.moneytree.app.ui.success.SuccessActivity


/**
 * The view model class for redeem. It handles the business logic to communicate with the model for the redeem and provides the data to the observing UI component.
 */
class NSRechargeViewModel(application: Application) : NSViewModel(application),
    NSGenericViewModelCallback {
    var isServiceProviderDataAvailable = MutableLiveData<Boolean>()
    var rechargeSelectedType: String? = ""
    var rechargeDetail: String? = ""
	var serviceProviderResponse: NSServiceProviderResponse? = null
	var serviceProviderDataList: MutableList<ServiceProviderDataItem> = arrayListOf()
	var serviceProvidersList: MutableList<String> = arrayListOf()
	var dataItemModel: ServiceProviderDataItem? = null
	var rechargeRequest: NSRechargeSaveRequest? = null
	var successResponse: NSSuccessResponse? = null

	/**
	 * for Recharge Detail Screen
	 * */
	fun setRechargeData() {
		if (!rechargeDetail.isNullOrEmpty()) {
			rechargeRequest = Gson().fromJson(rechargeDetail, NSRechargeSaveRequest::class.java)
		}
	}

    /**
     * Get redeem list data
     *
     */
    fun setServiceProvider(rechargeType: String, rechargeMemberId: String = "", isShowProgress: Boolean) {
        if (isShowProgress) {
            isProgressShowing.value = true
        }
		NSRechargeRepository.getServiceProvider(rechargeType, rechargeMemberId, this)
    }

    override fun <T> onSuccess(data: T) {
        isProgressShowing.value = false
        serviceProviderResponse = data as NSServiceProviderResponse
		setServiceProvider()
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

	private fun setServiceProvider() {
		serviceProvidersList.clear()
		if (serviceProviderResponse != null) {
			serviceProviderDataList.clear()
			serviceProviderDataList.add(ServiceProviderDataItem(serviceProvider = "Select Provider"))
			serviceProviderDataList.addAll(serviceProviderResponse!!.data)
			for (data in serviceProviderDataList) {
				data.serviceProvider?.let { serviceProvidersList.add(it) }
			}
		}
		isServiceProviderDataAvailable.value = serviceProvidersList.isValidList()
	}

	fun saveRecharge(activity: Activity) {
		if (rechargeRequest != null) {
			isProgressShowing.value = true
			NSRechargeRepository.saveRecharge(rechargeRequest!!,
				object : NSGenericViewModelCallback {
					override fun <T> onSuccess(data: T) {
						isProgressShowing.value = false
						successResponse = data as NSSuccessResponse
						openSuccessFail(activity)
					}

					override fun onError(errors: List<Any>) {
						openSuccessFail(activity)
					}

					override fun onFailure(failureMessage: String?) {
						openSuccessFail(activity)
					}

					override fun <T> onNoNetwork(localData: T) {
						handleNoNetwork()
					}

				})
		}
	}

	private fun openSuccessFail(activity: Activity) {
		activity.switchActivity(
			SuccessActivity::class.java,
			flags = intArrayOf(Intent.FLAG_ACTIVITY_CLEAR_TOP), bundle = bundleOf(NSConstants.KEY_SUCCESS_FAIL to if (successResponse == null) "" else Gson().toJson(successResponse))
		)
		activity.finish()
	}
}
