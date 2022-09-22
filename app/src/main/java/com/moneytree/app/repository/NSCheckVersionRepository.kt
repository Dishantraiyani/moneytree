package com.moneytree.app.repository

import com.moneytree.app.common.NSApplication
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.callbacks.NSRetrofitCallback
import com.moneytree.app.repository.network.error.NSApiErrorHandler
import com.moneytree.app.repository.network.responses.NSCheckVersionResponse
import retrofit2.Response

/**
 * Repository class to handle data operations related to check version
 */
object NSCheckVersionRepository {
    private val apiManager by lazy { NSApplication.getInstance().getApiManager() }
    private var errorMessageList: MutableList<Any> = mutableListOf()

    /**
     * To get dashboard list API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun checkVersionData(
        viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.checkVersion(object :
            NSRetrofitCallback<NSCheckVersionResponse>(viewModelCallback, NSApiErrorHandler.ERROR_CHECK_VERSION) {
            override fun <T> onResponse(response: Response<T>) {
                val data = response.body() as NSCheckVersionResponse
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
