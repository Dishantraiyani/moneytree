package com.moneytree.app.repository

import com.moneytree.app.common.NSApplication
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.callbacks.NSRetrofitCallback
import com.moneytree.app.repository.network.error.NSApiErrorHandler
import com.moneytree.app.repository.network.requests.NSKycSendRequest
import com.moneytree.app.repository.network.responses.KycResponse
import retrofit2.Response

/**
 * Repository class to handle data operations related to wallet
 */
object NSKycRepository {
    private val apiManager by lazy { NSApplication.getInstance().getApiManager() }
    private var errorMessageList: MutableList<Any> = mutableListOf()

    fun kycVerification(kycRequest: NSKycSendRequest,
						viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.kycVerification(kycRequest, object :
            NSRetrofitCallback<KycResponse>(viewModelCallback, NSApiErrorHandler.ERROR_SERVICE_PROVIDER_DATA) {
            override fun <T> onResponse(response: Response<T>) {
                val data = response.body() as KycResponse
                if (data.success) {
                    viewModelCallback.onSuccess(response.body())
                } else {
					errorMessageList.clear()
                    errorMessageList.add(data.message?:"")
                    viewModelCallback.onError(errorMessageList)
                }
            }
        })
    }
}
