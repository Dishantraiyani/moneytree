package com.moneytree.app.repository

import com.moneytree.app.common.NSApplication
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.callbacks.NSRetrofitCallback
import com.moneytree.app.repository.network.error.NSApiErrorHandler
import com.moneytree.app.repository.network.requests.NSDoctorSendRequest
import com.moneytree.app.repository.network.requests.NSKycSendRequest
import com.moneytree.app.repository.network.responses.DoctorResponse
import com.moneytree.app.repository.network.responses.KycResponse
import com.moneytree.app.repository.network.responses.NSSuccessResponse
import okhttp3.MultipartBody
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

    fun sendKycRequest(kycDetail: String, image: MultipartBody.Part,
                          viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.kycRequestSend(kycDetail, image, object :
            NSRetrofitCallback<NSSuccessResponse>(viewModelCallback, NSApiErrorHandler.ERROR_KYC_DATA_SEND) {
            override fun <T> onResponse(response: Response<T>) {
                val data = response.body() as NSSuccessResponse
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

    fun checkKycVerification(viewModelCallback: NSGenericViewModelCallback) {
        apiManager.checkKycVerification(object :
            NSRetrofitCallback<NSSuccessResponse>(viewModelCallback, NSApiErrorHandler.ERROR_KYC_STATUS) {
            override fun <T> onResponse(response: Response<T>) {
                val data = response.body() as NSSuccessResponse
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