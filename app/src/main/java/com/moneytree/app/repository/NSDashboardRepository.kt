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
object NSDashboardRepository {
    private val apiManager by lazy { NSApplication.getInstance().getApiManager() }

    /**
     * To get order list API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun dashboardData(
        viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.getDashboard(object :
            NSRetrofitCallback<NSDashboardResponse>(viewModelCallback, NSApiErrorHandler.ERROR_DASHBOARD) {
            override fun <T> onResponse(response: Response<T>) {
                viewModelCallback.onSuccess(response.body())
            }
        })
    }
}