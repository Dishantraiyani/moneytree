package com.moneytree.app.ui.mycart.cart

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSApplication
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSProductRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.NSProductListResponse
import com.moneytree.app.repository.network.responses.ProductDataDTO


/**
 * The view model class for joining voucher. It handles the business logic to communicate with the model for the joining voucher and provides the data to the observing UI component.
 */
class NSCartViewModel(application: Application) : NSViewModel(application) {
    var productList: MutableList<ProductDataDTO> = arrayListOf()
    var isProductsDataAvailable = MutableLiveData<Boolean>()
    var pageIndex: String = "1"
	var categoryId: String? = null
	var categoryName: String? = null
    var isFromOrder: Boolean = false


    /**
     * Get voucher list data
     *
     */
    fun getProductListData() {
        productList.clear()
        val instance = NSApplication.getInstance()
		productList.addAll(if (isFromOrder) instance.getOrderList() else instance.getProductList())
		isProductsDataAvailable.value = productList.isValidList()
    }
}
