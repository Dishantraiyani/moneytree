package com.moneytree.app.ui.mycart.products

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSDiseasesRepository
import com.moneytree.app.repository.NSProductRepository
import com.moneytree.app.repository.NSSearchRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.NSCategoryData
import com.moneytree.app.repository.network.responses.NSCategoryListResponse
import com.moneytree.app.repository.network.responses.NSDiseasesData
import com.moneytree.app.repository.network.responses.NSDiseasesResponse
import com.moneytree.app.repository.network.responses.NSProductListResponse
import com.moneytree.app.repository.network.responses.NSSearchListResponse
import com.moneytree.app.repository.network.responses.ProductDataDTO
import com.moneytree.app.repository.network.responses.SearchData


/**
 * The view model class for joining voucher. It handles the business logic to communicate with the model for the joining voucher and provides the data to the observing UI component.
 */
class NSProductViewModel(application: Application) : NSViewModel(application),
    NSGenericViewModelCallback {
    var productList: MutableList<ProductDataDTO> = arrayListOf()
    var tempProductList: MutableList<ProductDataDTO> = arrayListOf()
    var isProductsDataAvailable = MutableLiveData<Boolean>()
    var isSearchDataAvailable = MutableLiveData<MutableList<SearchData>>()
    var pageIndex: String = "1"
    var productResponse: NSProductListResponse? = null
    private var isBottomProgressShow: Boolean = false
    private var searchData: String = ""
	var categoryId: String? = ""
    var diseasesId: String? = ""

    fun searchAll(search: String) {
        if (search.length > 2) {
            NSSearchRepository.searchList(search, object : NSGenericViewModelCallback {
                override fun <T> onSuccess(data: T) {
                    if (data is NSSearchListResponse) {
                        isSearchDataAvailable.postValue(data.data)
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
    fun getProductStockListData(pageIndex: String, search: String, isShowProgress: Boolean, isBottomProgress: Boolean) {
        if (pageIndex == "1") {
            productList.clear()
        }
        if (isShowProgress) {
            isProgressShowing.value = true
			isBottomProgressShowing.value = false
        }
        if (isBottomProgress) {
            isBottomProgressShowing.value = true
        }
        isBottomProgressShow = isBottomProgress
        searchData = search
		categoryId?.let { NSProductRepository.getProductStockList(pageIndex, search, it, diseasesId?:"", this) }
    }

    override fun <T> onSuccess(data: T) {
        isProgressShowing.value = false
        if (isBottomProgressShow) {
            isBottomProgressShowing.value = false
        }
        val productListData = data as NSProductListResponse
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
