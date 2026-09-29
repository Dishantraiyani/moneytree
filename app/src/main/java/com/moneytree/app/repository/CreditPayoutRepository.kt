package com.moneytree.app.repository

import com.moneytree.app.common.NSApplication
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.callbacks.NSRetrofitCallback
import com.moneytree.app.repository.network.error.NSApiErrorHandler
import com.moneytree.app.repository.network.responses.CreditPayoutListResponse
import com.moneytree.app.repository.network.responses.NSRePurchaseInfoResponse
import com.moneytree.app.repository.network.responses.NSRePurchaseListResponse
import com.moneytree.app.repository.network.responses.OnlineOrderInfoResponse
import com.moneytree.app.repository.network.responses.OrderInfoResponse
import retrofit2.Response

/**
 * Repository class to handle data operations related to repurchase
 */
object CreditPayoutRepository {
    private val apiManager by lazy { NSApplication.getInstance().getApiManager() }
    private var errorMessageList: MutableList<Any> = mutableListOf()

    /**
     * To get repurchase list data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun getCreditPayoutList(pageIndex: String, search: String, paymentStatus: String,
                            viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.getCreditPayoutList(pageIndex, search, paymentStatus, object :
            NSRetrofitCallback<CreditPayoutListResponse>(viewModelCallback, NSApiErrorHandler.ERROR_REPURCHASE_LIST_DATA) {
            override fun <T> onResponse(response: Response<T>) {
                val data = response.body() as CreditPayoutListResponse
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
