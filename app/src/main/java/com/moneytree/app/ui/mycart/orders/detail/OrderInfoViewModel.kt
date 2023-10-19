package com.moneytree.app.ui.mycart.orders.detail

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.callbacks.NSSearchCallback
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSProductRepository
import com.moneytree.app.repository.NSRePurchaseRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.NSProductListResponse
import com.moneytree.app.repository.network.responses.NSRepurchaseStockModel
import com.moneytree.app.repository.network.responses.OrderHistoryDataItem
import com.moneytree.app.repository.network.responses.OrderHistoryResponse
import com.moneytree.app.repository.network.responses.OrderInfoDataItem
import com.moneytree.app.repository.network.responses.OrderInfoResponse
import com.moneytree.app.repository.network.responses.ProductDataDTO
import com.moneytree.app.repository.network.responses.RepurchaseDataItem


/**
 * The view model class for joining voucher. It handles the business logic to communicate with the model for the joining voucher and provides the data to the observing UI component.
 */
class OrderInfoViewModel(application: Application) : NSViewModel(application),
    NSGenericViewModelCallback {
    var productList: MutableList<OrderInfoDataItem> = arrayListOf()
    var tempProductList: MutableList<OrderInfoDataItem> = arrayListOf()
    var isProductsDataAvailable = MutableLiveData<Boolean>()
    var pageIndex: String = "1"
    var productResponse: OrderInfoResponse? = null
    private var isBottomProgressShow: Boolean = false
    private var searchData: String = ""
    var orderDirectId: String? = null
    var orderDirectDetail: String? = null
    var orderHistoryDataItem: OrderHistoryDataItem? = null

    fun getOrderHistoryDataItem(orderDirectDetail: String) {
        if (orderDirectDetail.isNotEmpty()) {
            orderHistoryDataItem = Gson().fromJson(orderDirectDetail, OrderHistoryDataItem::class.java)
        }
    }

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
        NSRePurchaseRepository.getPlaceOrderInfoData(pageIndex, orderDirectId?:"", search,this)
    }

    override fun <T> onSuccess(data: T) {
        isProgressShowing.value = false
        if (isBottomProgressShow) {
            isBottomProgressShowing.value = false
        }
        val productListData = data as OrderInfoResponse
        productResponse = productListData
        if (productListData.data.isValidList()) {
            productList.addAll(productListData.data)
            isProductsDataAvailable.value = productList.isValidList()
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
