package com.moneytree.app.repository

import com.moneytree.app.common.NSApplication
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.callbacks.NSRetrofitCallback
import com.moneytree.app.repository.network.error.NSApiErrorHandler
import com.moneytree.app.repository.network.responses.NSNotificationListResponse
import retrofit2.Response

/**
 * Repository class to handle data operations related to voucher
 */
object NSNotificationRepository {
    private val apiManager by lazy { NSApplication.getInstance().getApiManager() }
    private var errorMessageList: MutableList<Any> = mutableListOf()

    /**
     * To get joining voucher data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun getNotifications(pageIndex: String,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.getNotifications(pageIndex, object :
            NSRetrofitCallback<NSNotificationListResponse>(viewModelCallback, NSApiErrorHandler.ERROR_NOTIFICATION_LIST) {
            override fun <T> onResponse(response: Response<T>) {
                val data = response.body() as NSNotificationListResponse
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
