package com.moneytree.app.ui.mycart.history

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.callbacks.NSSearchCallback
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSProductRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.NSProductListResponse
import com.moneytree.app.repository.network.responses.NSRepurchaseStockModel
import com.moneytree.app.repository.network.responses.ProductDataDTO
import com.moneytree.app.repository.network.responses.RepurchaseDataItem


/**
 * The view model class for joining voucher. It handles the business logic to communicate with the model for the joining voucher and provides the data to the observing UI component.
 */
class RSHistoryViewModel(application: Application) : NSViewModel(application),
    NSGenericViewModelCallback {
    var productList: MutableList<RepurchaseDataItem> = arrayListOf()
    var tempProductList: MutableList<RepurchaseDataItem> = arrayListOf()
    var isProductsDataAvailable = MutableLiveData<Boolean>()
    var pageIndex: String = "1"
    var productResponse: NSRepurchaseStockModel? = null
    private var isBottomProgressShow: Boolean = false
    private var searchData: String = ""
	var stockType: String? = null

    /**
     * Get voucher list data
     *
     */
    fun getProductListData(pageIndex: String, search: String, isShowProgress: Boolean, isBottomProgress: Boolean) {
        if (pageIndex == "1") {
            productList.clear()
        }
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        if (isBottomProgress) {
            isBottomProgressShowing.value = true
        }
        isBottomProgressShow = isBottomProgress
        searchData = search
		if (stockType != null) {
			if (stockType.equals(NSConstants.SOCKET_HISTORY)) {
				NSProductRepository.getStockTransferHistoryList(pageIndex, search, this)
			} else {
				NSProductRepository.getRepurchaseHistoryList(pageIndex, search, this)
			}
		}
    }

    override fun <T> onSuccess(data: T) {
        isProgressShowing.value = false
        if (isBottomProgressShow) {
            isBottomProgressShowing.value = false
        }
        val productListData = data as NSRepurchaseStockModel
        productResponse = productListData
        if (productListData.data != null) {
            if (productListData.data.isValidList()) {
                productList.addAll(productListData.data)
                isProductsDataAvailable.value = productList.isValidList()
            } else if (pageIndex == "1" || searchData.isNotEmpty()){
				isProductsDataAvailable.value = false
            }
        } else if (pageIndex == "1" || searchData.isNotEmpty()){
			isProductsDataAvailable.value = false
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
