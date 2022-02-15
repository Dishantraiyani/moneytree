package com.moneytree.app.repository

import com.moneytree.app.common.NSApplication
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.callbacks.NSRetrofitCallback
import com.moneytree.app.repository.network.error.NSApiErrorHandler
import com.moneytree.app.repository.network.responses.*
import retrofit2.Response

/**
 * Repository class to handle data operations related to orders
 */
object NSRePurchaseRepository {
    private val apiManager by lazy { NSApplication.getInstance().getApiManager() }

    /**
     * To get single order data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun getRePurchaseListData(pageIndex: String, search: String,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.getRePurchaseListData(pageIndex, search, object :
            NSRetrofitCallback<NSRePurchaseListResponse>(viewModelCallback, NSApiErrorHandler.ERROR_REPURCHASE_LIST_DATA) {
            override fun <T> onResponse(response: Response<T>) {
                viewModelCallback.onSuccess(response.body())
            }
        })
    }

    /**
     * To get single order data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun getRePurchaseInfoData(pageIndex: String, repurchaseId: String,
                              viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.getRePurchaseInfoData(pageIndex, repurchaseId, object :
            NSRetrofitCallback<NSRePurchaseInfoResponse>(viewModelCallback, NSApiErrorHandler.ERROR_REPURCHASE_INFO_DATA) {
            override fun <T> onResponse(response: Response<T>) {
                viewModelCallback.onSuccess(response.body())
            }
        })
    }
}