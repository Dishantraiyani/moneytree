package com.moneytree.app.ui.onlineorder.cart

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.network.responses.ProductDataDTO
import com.moneytree.app.ui.onlineorder.OnlineOrderHelper


/**
 * The view model class for joining voucher. It handles the business logic to communicate with the model for the joining voucher and provides the data to the observing UI component.
 */
class OnlineCartViewModel(application: Application) : NSViewModel(application) {
	var productList: MutableList<ProductDataDTO> = arrayListOf()
	var isProductsDataAvailable = MutableLiveData<Boolean>()
	var pageIndex: String = "1"
	var categoryId: String? = null
	var categoryName: String? = null
	
	fun getProductListData() {
		productList.clear()
		val instance = OnlineOrderHelper
		productList.addAll(instance.getOrderList())
		isProductsDataAvailable.value = productList.isValidList()
	}
}
