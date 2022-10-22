package com.moneytree.app.ui.productCategory

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSProductRepository
import com.moneytree.app.repository.NSVoucherRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.NSCategoryData
import com.moneytree.app.repository.network.responses.NSCategoryListResponse
import com.moneytree.app.repository.network.responses.NSVoucherListData
import com.moneytree.app.repository.network.responses.NSVoucherListResponse


/**
 * The view model class for joining voucher. It handles the business logic to communicate with the model for the joining voucher and provides the data to the observing UI component.
 */
class MTProductCategoryViewModel(application: Application) : NSViewModel(application),
    NSGenericViewModelCallback {
    var categoryList: MutableList<NSCategoryData> = arrayListOf()
    var tempVoucherList: MutableList<NSCategoryData> = arrayListOf()
    var isCategoryDataAvailable = MutableLiveData<Boolean>()

    /**
     * Get voucher list data
     *
     */
    fun getProductCategory(isShowProgress: Boolean) {
		categoryList.clear()
        if (isShowProgress) {
            isProgressShowing.value = true
        }
		NSProductRepository.getCategoryOfProducts(this)
    }

    override fun <T> onSuccess(data: T) {
        isProgressShowing.value = false
        val voucherMainListData = data as NSCategoryListResponse
        if (voucherMainListData.data != null) {
            if (voucherMainListData.data.isValidList()) {
                categoryList.addAll(voucherMainListData.data)
            }
        }
		isCategoryDataAvailable.value = true
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
