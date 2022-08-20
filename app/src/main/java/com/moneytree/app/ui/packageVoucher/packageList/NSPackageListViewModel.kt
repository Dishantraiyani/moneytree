package com.moneytree.app.ui.packageVoucher.packageList

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSVoucherRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.NSPackageData
import com.moneytree.app.repository.network.responses.NSPackageResponse


/**
 * The view model class for register. It handles the business logic to communicate with the model for the register and provides the data to the observing UI component.
 */
class NSPackageListViewModel(application: Application) : NSViewModel(application),
    NSGenericViewModelCallback {
    var packageList: MutableList<NSPackageData> = arrayListOf()
    var tempPackageList: MutableList<NSPackageData> = arrayListOf()
    var isPackageDataAvailable = MutableLiveData<Boolean>()

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
			packageList.addAll(packageMainData.data)
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
