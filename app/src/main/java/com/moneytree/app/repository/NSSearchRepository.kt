package com.moneytree.app.repository

import com.moneytree.app.common.NSApplication
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.callbacks.NSRetrofitCallback
import com.moneytree.app.repository.network.error.NSApiErrorHandler
import com.moneytree.app.repository.network.responses.NSDiseasesResponse
import com.moneytree.app.repository.network.responses.NSProductListResponse
import com.moneytree.app.repository.network.responses.NSRegisterListResponse
import com.moneytree.app.repository.network.responses.NSSearchListResponse
import retrofit2.Response

/**
 * Repository class to handle data operations related to diseases
 */
object NSSearchRepository {
    private val apiManager by lazy { NSApplication.getInstance().getApiManager() }
    private var errorMessageList: MutableList<Any> = mutableListOf()

    fun searchList(search: String,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.searchList(search, object :
            NSRetrofitCallback<NSSearchListResponse>(viewModelCallback, NSApiErrorHandler.ERROR_SEARCH_LIST_DATA) {
            override fun <T> onResponse(response: Response<T>) {
                val data = response.body() as NSSearchListResponse
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

    fun searchDirectOrderList(search: String,
                   viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.searchDirectOrderList(search, object :
            NSRetrofitCallback<NSSearchListResponse>(viewModelCallback, NSApiErrorHandler.ERROR_SEARCH_LIST_DATA) {
            override fun <T> onResponse(response: Response<T>) {
                val data = response.body() as NSSearchListResponse
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
