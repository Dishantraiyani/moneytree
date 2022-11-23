package com.moneytree.app.ui.mycart.stockDetail

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSProductRepository
import com.moneytree.app.repository.NSRePurchaseRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.*


/**
 * The view model class for member tree. It handles the business logic to communicate with the model for the member tree and provides the data to the observing UI component.
 */
class StockDetailViewModel(application: Application) : NSViewModel(application),
    NSGenericViewModelCallback {
    var isStock: Boolean = false
    var stockList: MutableList<NSRePurchaseInfoData> = arrayListOf()
    var isStockDataAvailable = MutableLiveData<Boolean>()
    private var stockResponse: NSRePurchaseInfoResponse? = null
	var stockId: String? = null

    /**
     * Get member tree data
     *
     */
    fun getStockInfo(isShowProgress: Boolean) {
        stockList.clear()


		stockId?.let {
			if (isShowProgress) {
				isProgressShowing.value = true
			}
			if (isStock == true) {
				NSProductRepository.getStockTransferInfo(it, this)
			} else {
				NSRePurchaseRepository.getRePurchaseInfoData("1", it, this)
			}

		}
    }

    override fun <T> onSuccess(data: T) {
        isProgressShowing.value = false
        val memberMainListData = data as NSRePurchaseInfoResponse
        stockResponse = memberMainListData
		if (memberMainListData.data.isValidList()) {
			stockList.addAll(memberMainListData.data)
		}
		isStockDataAvailable.value = stockList.isValidList()
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
