package com.moneytree.app.ui.products

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.callbacks.NSSearchResponseCallback
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSProductRepository
import com.moneytree.app.repository.NSSearchRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.NSProductListResponse
import com.moneytree.app.repository.network.responses.NSSearchListResponse
import com.moneytree.app.repository.network.responses.ProductDataDTO


/**
 * The view model class for joining voucher. It handles the business logic to communicate with the model for the joining voucher and provides the data to the observing UI component.
 */
class MTProductViewModel(application: Application) : NSViewModel(application),
    NSGenericViewModelCallback {
    var productList: MutableList<ProductDataDTO> = arrayListOf()
    var tempProductList: MutableList<ProductDataDTO> = arrayListOf()
    var isProductsDataAvailable = MutableLiveData<MutableList<ProductDataDTO>>()
    var pageIndex: String = "1"
    var productResponse: NSProductListResponse? = null
    private var isBottomProgressShow: Boolean = false
    private var searchData: String = ""
	var categoryId: String? = null
	var categoryName: String? = null
    var pageList: MutableList<String> = arrayListOf()
    var diseasesId: String? = ""
    var selectedStock: String = "All"

    fun searchAll(search: String, callback: NSSearchResponseCallback) {
        if (search.length > 2) {
            NSSearchRepository.searchList(search, object : NSGenericViewModelCallback {
                override fun <T> onSuccess(data: T) {
                    if (data is NSSearchListResponse) {
                        callback.onSearch(data.data)
                        //isSearchDataAvailable.postValue(data.data)
                    }
                }

                override fun onError(errors: List<Any>) {

                }

                override fun onFailure(failureMessage: String?) {

                }

                override fun <T> onNoNetwork(localData: T) {

                }

            })
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
        if (categoryId != null) {
			NSProductRepository.getProductList(pageIndex, search, categoryId!!, this)
		}
    }

    override fun <T> onSuccess(data: T) {
        isProgressShowing.value = false
        if (isBottomProgressShow) {
            isBottomProgressShowing.value = false
        }
        val productListData = data as NSProductListResponse
        productResponse = productListData
        if (productListData.data != null) {
            if (productListData.data.isValidList()) {
                productList.addAll(productListData.data)
                isProductsDataAvailable.value = productList
            } else if (pageIndex == "1" || searchData.isNotEmpty()){
				isProductsDataAvailable.value = arrayListOf()
            }
        } else if (pageIndex == "1" || searchData.isNotEmpty()){
			isProductsDataAvailable.value = arrayListOf()
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
