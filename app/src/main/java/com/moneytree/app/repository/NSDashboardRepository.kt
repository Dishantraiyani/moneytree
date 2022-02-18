package com.moneytree.app.repository

import com.moneytree.app.common.NSApplication
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.callbacks.NSRetrofitCallback
import com.moneytree.app.repository.network.error.NSApiErrorHandler
import com.moneytree.app.repository.network.responses.NSDashboardResponse
import retrofit2.Response

/**
 * Repository class to handle data operations related to dashboard
 */
object NSDashboardRepository {
    private val apiManager by lazy { NSApplication.getInstance().getApiManager() }
    private var errorMessageList: MutableList<Any> = mutableListOf()

    /**
     * To get dashboard list API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun dashboardData(
        viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.getDashboard(object :
            NSRetrofitCallback<NSDashboardResponse>(viewModelCallback, NSApiErrorHandler.ERROR_DASHBOARD) {
            override fun <T> onResponse(response: Response<T>) {
                val data = response.body() as NSDashboardResponse
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