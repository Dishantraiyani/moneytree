package com.moneytree.app.repository

import com.moneytree.app.common.NSApplication
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.callbacks.NSRetrofitCallback
import com.moneytree.app.repository.network.error.NSApiErrorHandler
import com.moneytree.app.repository.network.responses.NSRoyaltyInfoResponse
import com.moneytree.app.repository.network.responses.NSRoyaltyListResponse
import retrofit2.Response

/**
 * Repository class to handle data operations related to orders
 */
object NSRoyaltyRepository {
    private val apiManager by lazy { NSApplication.getInstance().getApiManager() }
    private var errorMessageList: MutableList<Any> = mutableListOf()

    /**
     * To get single order data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun getRoyaltyListData(pageIndex: String, search: String,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.getRoyaltyListData(pageIndex, search, object :
            NSRetrofitCallback<NSRoyaltyListResponse>(viewModelCallback, NSApiErrorHandler.ERROR_ROYALTY_LIST_DATA) {
            override fun <T> onResponse(response: Response<T>) {
                val data = response.body() as NSRoyaltyListResponse
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
     * To get single order data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun getRoyaltyInfoData(pageIndex: String, repurchaseId: String,
                              viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.getRoyaltyInfoData(pageIndex, repurchaseId, object :
            NSRetrofitCallback<NSRoyaltyInfoResponse>(viewModelCallback, NSApiErrorHandler.ERROR_ROYAL_INFO_DATA) {
            override fun <T> onResponse(response: Response<T>) {
                val data = response.body() as NSRoyaltyInfoResponse
                if (data.status) {
                    viewModelCallback.onSuccess(response.body())
                } else {
                    errorMessageList.add(data.message!!)
                    viewModelCallback.onError(errorMessageList)
                }
            }
        })
    }
}