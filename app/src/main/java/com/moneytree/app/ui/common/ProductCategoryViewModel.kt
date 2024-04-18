package com.moneytree.app.ui.common

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.ProductCategory
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSDiseasesRepository
import com.moneytree.app.repository.NSProductRepository
import com.moneytree.app.repository.NSVoucherRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.NSCategoryData
import com.moneytree.app.repository.network.responses.NSCategoryListResponse
import com.moneytree.app.repository.network.responses.NSDiseasesData
import com.moneytree.app.repository.network.responses.NSDiseasesResponse
import com.moneytree.app.repository.network.responses.NSJointCategoryDiseasesResponse
import com.moneytree.app.repository.network.responses.NSVoucherListData
import com.moneytree.app.repository.network.responses.NSVoucherListResponse


/**
 * The view model class for product category. It handles the business logic to communicate with the model for the joining voucher and provides the data to the observing UI component.
 */
class ProductCategoryViewModel(application: Application) : NSViewModel(application) {
    var isCategoryDataAvailable = MutableLiveData<NSJointCategoryDiseasesResponse>()
    var jointResponse: NSJointCategoryDiseasesResponse = NSJointCategoryDiseasesResponse()
    var obj: NSGenericViewModelCallback? = null

    /**
     * Get product category list data
     *
     */
    fun getProductCategory(isShowProgress: Boolean, isDiseases: Boolean = false, isFromHome: Boolean = false) {
        if (isFromHome) {
            val gson: NSCategoryListResponse = Gson().fromJson(ProductCategory.categories, NSCategoryListResponse::class.java)
            jointResponse.categoryList = gson.data
            isCategoryDataAvailable.value = jointResponse
        } else {
            if (isShowProgress) {
                isProgressShowing.value = true
            }

            obj = object : NSGenericViewModelCallback {
                override fun <T> onSuccess(data: T) {
                    if (data is NSCategoryListResponse) {
                        jointResponse.categoryList = data.data
                        if (!isDiseases) {
                            isCategoryDataAvailable.value = jointResponse
                            isProgressShowing.value = false
                        } else {
                            NSDiseasesRepository.getDiseasesListData(obj!!)
                        }
                    } else if (data is NSDiseasesResponse) {
                        isProgressShowing.value = false
                        jointResponse.diseasesList = data.data
                        isCategoryDataAvailable.value = jointResponse
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
            NSProductRepository.getCategoryOfProducts(obj!!)
        }
    }
}
