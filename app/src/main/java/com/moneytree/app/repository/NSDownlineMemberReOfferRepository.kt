package com.moneytree.app.repository

import com.moneytree.app.common.NSApplication
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.callbacks.NSRetrofitCallback
import com.moneytree.app.repository.network.error.NSApiErrorHandler
import com.moneytree.app.repository.network.responses.NSDownlineMemberDirectReOfferResponse
import retrofit2.Response

/**
 * Repository class to handle data operations related to downline member
 */
object NSDownlineMemberReOfferRepository {
    private val apiManager by lazy { NSApplication.getInstance().getApiManager() }
    private var errorMessageList: MutableList<Any> = mutableListOf()

    /**
     * To get downline member data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun getDownlineMemberReOfferListData(viewModelCallback: NSGenericViewModelCallback) {
        apiManager.getDownlineMemberDirectReOffer(object :
            NSRetrofitCallback<NSDownlineMemberDirectReOfferResponse>(viewModelCallback, NSApiErrorHandler.ERROR_DOWNLINE_MEMBER_LIST_DATA) {
            override fun <T> onResponse(response: Response<T>) {
                val data = response.body() as NSDownlineMemberDirectReOfferResponse
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
