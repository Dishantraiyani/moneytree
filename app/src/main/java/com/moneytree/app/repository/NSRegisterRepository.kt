package com.moneytree.app.repository

import com.moneytree.app.common.NSApplication
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.callbacks.NSRetrofitCallback
import com.moneytree.app.repository.network.error.NSApiErrorHandler
import com.moneytree.app.repository.network.responses.NSRegisterListResponse
import retrofit2.Response

/**
 * Repository class to handle data operations related to register
 */
object NSRegisterRepository {
    private val apiManager by lazy { NSApplication.getInstance().getApiManager() }
    private var errorMessageList: MutableList<Any> = mutableListOf()

    /**
     * To get register data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun getRegisterListData(pageIndex: String, search: String,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.getRegisterListData(pageIndex, search, object :
            NSRetrofitCallback<NSRegisterListResponse>(viewModelCallback, NSApiErrorHandler.ERROR_REGISTER_LIST_DATA) {
            override fun <T> onResponse(response: Response<T>) {
                val data = response.body() as NSRegisterListResponse
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