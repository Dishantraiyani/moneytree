package com.moneytree.app.ui.onlineorder.orderHistoryDetail.detail

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSRePurchaseRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.OnlineOrderHistoryData
import com.moneytree.app.repository.network.responses.OnlineOrderInfoResponse
import com.moneytree.app.repository.network.responses.OrderHistoryDataItem
import com.moneytree.app.repository.network.responses.OrderInfoDataItem
import com.moneytree.app.repository.network.responses.OrderInfoResponse


/**
 * The view model class for joining voucher. It handles the business logic to communicate with the model for the joining voucher and provides the data to the observing UI component.
 */
class OnlineOrderInfoViewModel(application: Application) : NSViewModel(application),
    NSGenericViewModelCallback {
    var productList: MutableList<OrderInfoDataItem> = arrayListOf()
    var tempProductList: MutableList<OrderInfoDataItem> = arrayListOf()
    var isProductsDataAvailable = MutableLiveData<Boolean>()
    
    var orderDirectId: String? = null
    var orderDirectDetail: String? = null
    var orderHistoryDataItem: OnlineOrderHistoryData? = null

    /**
     * Get voucher list data
     *
     */
    fun getProductListData(isShowProgress: Boolean) {
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        
        NSRePurchaseRepository.getOnlineOrderHistoryDetail(orderDirectId?:"", this)
    }

    override fun <T> onSuccess(data: T) {
        isProgressShowing.value = false
        
        productList.clear()
        val onlineOrderDetail = data as OnlineOrderInfoResponse
        orderHistoryDataItem = onlineOrderDetail.data
        
        if (onlineOrderDetail.items != null) {
            productList.addAll(onlineOrderDetail.items)
        }
        
        isProductsDataAvailable.value = productList.isValidList()
        
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
