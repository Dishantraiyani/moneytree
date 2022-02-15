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
object NSVoucherRepository {
    private val apiManager by lazy { NSApplication.getInstance().getApiManager() }

    /**
     * To get single order data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun getJoiningVoucherPendingData(pageIndex: String, search: String,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.getJoiningVoucherPendingData(pageIndex, search, object :
            NSRetrofitCallback<NSVoucherListResponse>(viewModelCallback, NSApiErrorHandler.ERROR_VOUCHER_PENDING_DATA) {
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
    fun getJoiningVoucherReceiveData(pageIndex: String, search: String,
                                     viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.getJoiningVoucherReceiveData(pageIndex, search, object :
            NSRetrofitCallback<NSVoucherListResponse>(viewModelCallback, NSApiErrorHandler.ERROR_VOUCHER_RECEIVE_DATA) {
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
    fun getJoiningVoucherTransferData(pageIndex: String, search: String,
                                     viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.getJoiningVoucherTransferData(pageIndex, search, object :
            NSRetrofitCallback<NSVoucherListResponse>(viewModelCallback, NSApiErrorHandler.ERROR_VOUCHER_TRANSFER_DATA) {
            override fun <T> onResponse(response: Response<T>) {
                viewModelCallback.onSuccess(response.body())
            }
        })
    }
}