package com.moneytree.app.ui.packageVoucher.packageDetail

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSVoucherRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.requests.NSWalletTransferModel
import com.moneytree.app.repository.network.responses.NSPackageData
import com.moneytree.app.repository.network.responses.NSPackageResponse
import com.moneytree.app.repository.network.responses.NSPackageVoucherData
import com.moneytree.app.repository.network.responses.NSPackageVoucherQntResponse


/**
 * The view model class for register. It handles the business logic to communicate with the model for the register and provides the data to the observing UI component.
 */
class NSPackageDetailViewModel(application: Application) : NSViewModel(application),
    NSGenericViewModelCallback {
    var packageList: MutableList<NSPackageVoucherData> = arrayListOf()
    var isPackageDataAvailable = MutableLiveData<Boolean>()
	var strPackageDetail: String? = null
	var packageData: NSPackageData? = null
	var isDataAvailable = MutableLiveData<Boolean>()
	var voucherQuantity: String? = null

	fun getDetail() {
		if (strPackageDetail?.isNotEmpty() == true) {
			packageData = Gson().fromJson(strPackageDetail, NSPackageData::class.java)
			isDataAvailable.value = true
		}
	}

    /**
     * Get register list data
     *
     */
    fun getPackageListData(isShowProgress: Boolean) {
        if (packageData != null) {
			if (isShowProgress) {
				isProgressShowing.value = true
			}
			NSVoucherRepository.getPackageViseVoucherQty(packageData?.packageId!!, this)
		}
    }

    override fun <T> onSuccess(data: T) {
        isProgressShowing.value = false
		val packageMainData = data as NSPackageVoucherQntResponse
		if (packageMainData.data.isValidList()) {
			voucherQuantity = packageMainData.voucherCount.toString()
			packageList.clear()
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
