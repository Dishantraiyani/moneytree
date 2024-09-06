package com.moneytree.app.ui.mycart.orders

import android.app.Activity
import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.BuildConfig
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.SliderDoctorAdapter
import com.moneytree.app.common.callbacks.NSSearchResponseCallback
import com.moneytree.app.common.utils.NSUtilities
import com.moneytree.app.config.ApiConfig
import com.moneytree.app.repository.NSDiseasesRepository
import com.moneytree.app.repository.NSProductRepository
import com.moneytree.app.repository.NSSearchRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.NSBrandResponse
import com.moneytree.app.repository.network.responses.NSCategoryListResponse
import com.moneytree.app.repository.network.responses.NSDiseasesResponse
import com.moneytree.app.repository.network.responses.NSJointCategoryDiseasesResponse
import com.moneytree.app.repository.network.responses.NSProductListResponse
import com.moneytree.app.repository.network.responses.NSSearchListResponse
import com.moneytree.app.repository.network.responses.ProductDataDTO
import com.moneytree.app.slider.IndicatorView.animation.type.IndicatorAnimationType
import com.moneytree.app.slider.SliderView


/**
 * The view model class for joining voucher. It handles the business logic to communicate with the model for the joining voucher and provides the data to the observing UI component.
 */
class NSOrderViewModel(application: Application) : NSViewModel(application),
    NSGenericViewModelCallback {
    var productList: MutableList<ProductDataDTO> = arrayListOf()
    var isProductsDataAvailable = MutableLiveData<MutableList<ProductDataDTO>>()
    var pageIndex: String = "1"
    var pageList: MutableList<String> = arrayListOf()
    private var isBottomProgressShow: Boolean = false
    private var searchData: String = ""
	var categoryId: String? = ""
    var brandId: String? = ""
    var selectedStock: String = "All"
    val mFragmentList: MutableList<String> = ArrayList()

    var isCategoryDataAvailable = MutableLiveData<NSJointCategoryDiseasesResponse>()
    var jointResponse: NSJointCategoryDiseasesResponse = NSJointCategoryDiseasesResponse()

    fun searchAll(search: String, callback: NSSearchResponseCallback) {
        if (search.length > 2) {
            NSSearchRepository.searchDirectOrderList(search, object : NSGenericViewModelCallback {
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
		categoryId?.let { NSProductRepository.getOnlineOrderList(pageIndex, search, it, brandId?:"", selectedStock, this) }
    }

    override fun <T> onSuccess(data: T) {
        isProgressShowing.value = false
        if (isBottomProgressShow) {
            isBottomProgressShowing.value = false
        }
        val productListData = data as NSProductListResponse
       // productList.clear()
        productList.addAll(productListData.data)
        isProductsDataAvailable.value = productList
    }

    fun setupViewPager(activity: Activity, viewPager: SliderView, response: ProductDataDTO) {
        val list = response.multiImageList
        val imageList = list?.split(",")
        mFragmentList.clear()
        for (image in imageList?: arrayListOf()) {
            val base = ApiConfig.baseUrlImage
            mFragmentList.add(base + image)
        }

        val pagerAdapter = SliderDoctorAdapter(activity, mFragmentList)
        viewPager.setSliderAdapter(pagerAdapter)
        pagerAdapter.notifyDataSetChanged()
        viewPager.setIndicatorAnimation(IndicatorAnimationType.NONE)
        viewPager.setSliderTransformAnimation()
        // viewPager.startAutoCycle()
    }

    fun getOnlineOrderCategory(isShowProgress: Boolean, isDiseases: Boolean = false) {
        if (isShowProgress) showProgress()
        callCommonApi({ obj ->
            NSProductRepository.getOnlineOrderCategory(obj)
        }, { data, isSuccess ->
            hideProgress()
            if (isSuccess) {
                if (data is NSCategoryListResponse) {
                    jointResponse.categoryList = data.data
                    if (!isDiseases) {
                        isCategoryDataAvailable.value = jointResponse
                        isProgressShowing.value = false
                    } else {
                        callCommonApi({ obj ->
                            NSDiseasesRepository.getBrandListData(obj)
                        }, { dataDiseases, _ ->
                            hideProgress()
                            if (dataDiseases is NSBrandResponse) {
                                isProgressShowing.value = false
                                jointResponse.brandList = dataDiseases.data
                                isCategoryDataAvailable.value = jointResponse
                            }
                        })

                    }
                }
            }
        })
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
