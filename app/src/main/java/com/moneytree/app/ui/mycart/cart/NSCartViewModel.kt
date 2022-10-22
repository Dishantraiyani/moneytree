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
    var productResponse: NSProductListResponse? = null
	var categoryId: String? = null
	var categoryName: String? = null


    /**
     * Get voucher list data
     *
     */
    fun getProductListData() {
        productList.clear()
		productList.addAll(NSApplication.getInstance().getProductList())
		isProductsDataAvailable.value = productList.isValidList()
    }
}
