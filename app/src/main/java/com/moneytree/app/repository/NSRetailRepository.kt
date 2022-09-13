package com.moneytree.app.repository

import com.moneytree.app.common.NSApplication
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.callbacks.NSRetrofitCallback
import com.moneytree.app.repository.network.error.NSApiErrorHandler
import com.moneytree.app.repository.network.responses.NSRetailInfoResponse
import com.moneytree.app.repository.network.responses.NSRetailListResponse
import retrofit2.Response

/**
 * Repository class to handle data operations related to retail
 */
object NSRetailRepository {
    private val apiManager by lazy { NSApplication.getInstance().getApiManager() }
    private var errorMessageList: MutableList<Any> = mutableListOf()

    /**
     * To get retail list data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun getRetailListData(pageIndex: String, search: String,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.getRetailListData(pageIndex, search, object :
            NSRetrofitCallback<NSRetailListResponse>(viewModelCallback, NSApiErrorHandler.ERROR_REPURCHASE_LIST_DATA) {
            override fun <T> onResponse(response: Response<T>) {
                val data = response.body() as NSRetailListResponse
                if (data.status) {
                    viewModelCallback.onSuccess(response.body())
                } else {
                    errorMessageList.add(data.message!!)
                    viewModelCallback.onError(errorMessageList)
                }
            }
        })
    }

    /**
     * To get retail info data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun getRetailInfoData(pageIndex: String, repurchaseId: String,
                              viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.getRetailInfoData(pageIndex, repurchaseId, object :
            NSRetrofitCallback<NSRetailInfoResponse>(viewModelCallback, NSApiErrorHandler.ERROR_RETAIL_INFO_DATA) {
            override fun <T> onResponse(response: Response<T>) {
                val data = response.body() as NSRetailInfoResponse
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
