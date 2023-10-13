package com.moneytree.app.repository

import com.moneytree.app.common.NSApplication
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.callbacks.NSRetrofitCallback
import com.moneytree.app.repository.network.error.NSApiErrorHandler
import com.moneytree.app.repository.network.responses.NSRePurchaseInfoResponse
import com.moneytree.app.repository.network.responses.NSRePurchaseListResponse
import com.moneytree.app.repository.network.responses.OrderInfoResponse
import retrofit2.Response

/**
 * Repository class to handle data operations related to repurchase
 */
object NSRePurchaseRepository {
    private val apiManager by lazy { NSApplication.getInstance().getApiManager() }
    private var errorMessageList: MutableList<Any> = mutableListOf()

    /**
     * To get repurchase list data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun getRePurchaseListData(pageIndex: String, search: String,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.getRePurchaseListData(pageIndex, search, object :
            NSRetrofitCallback<NSRePurchaseListResponse>(viewModelCallback, NSApiErrorHandler.ERROR_REPURCHASE_LIST_DATA) {
            override fun <T> onResponse(response: Response<T>) {
                val data = response.body() as NSRePurchaseListResponse
                if (data.status) {
                    viewModelCallback.onSuccess(response.body())
                } else {
					errorMessageList.clear()
                    errorMessageList.add(data.message!!)
                    viewModelCallback.onError(errorMessageList)
                }
            }
        })
    }

    /**
     * To get repurchase info data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun getRePurchaseInfoData(pageIndex: String, repurchaseId: String,
                              viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.getRePurchaseInfoData(pageIndex, repurchaseId, object :
            NSRetrofitCallback<NSRePurchaseInfoResponse>(viewModelCallback, NSApiErrorHandler.ERROR_REPURCHASE_INFO_DATA) {
            override fun <T> onResponse(response: Response<T>) {
                val data = response.body() as NSRePurchaseInfoResponse
                if (data.status) {
                    viewModelCallback.onSuccess(response.body())
                } else {
					errorMessageList.clear()
                    errorMessageList.add(data.message!!)
                    viewModelCallback.onError(errorMessageList)
                }
            }
        })
    }

    fun getPlaceOrderInfoData(pageIndex: String, repurchaseId: String, search: String,
                              viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.getPlaceOrderInfoData(pageIndex, repurchaseId, search, object :
            NSRetrofitCallback<OrderInfoResponse>(viewModelCallback, NSApiErrorHandler.ERROR_REPURCHASE_INFO_DATA) {
            override fun <T> onResponse(response: Response<T>) {
                val data = response.body() as OrderInfoResponse
                if (data.status) {
                    viewModelCallback.onSuccess(response.body())
                } else {
                    errorMessageList.clear()
                    errorMessageList.add(data.message!!)
                    viewModelCallback.onError(errorMessageList)
                }
            }
        })
    }
}
