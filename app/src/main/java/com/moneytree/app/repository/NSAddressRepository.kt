package com.moneytree.app.repository

import com.moneytree.app.common.NSApplication
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.callbacks.NSRetrofitCallback
import com.moneytree.app.repository.network.error.NSApiErrorHandler
import com.moneytree.app.repository.network.responses.NSAddressCreateResponse
import com.moneytree.app.repository.network.responses.NSAddressListResponse
import com.moneytree.app.repository.network.responses.NSSuccessResponse
import retrofit2.Response

/**
 * Repository class to handle data operations related to diseases
 */
object NSAddressRepository {
    private val apiManager by lazy { NSApplication.getInstance().getApiManager() }
    private var errorMessageList: MutableList<Any> = mutableListOf()

    fun addOrUpdateAddress(request: NSAddressCreateResponse,
                           viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.addOrUpdateAddress(request, object :
            NSRetrofitCallback<NSSuccessResponse>(viewModelCallback, NSApiErrorHandler.ERROR_DOCTOR_LIST_DATA) {
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

    fun getAddressList(viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.getAddressList(object :
            NSRetrofitCallback<NSAddressListResponse>(viewModelCallback, NSApiErrorHandler.ERROR_DOCTOR_LIST_DATA) {
            override fun <T> onResponse(response: Response<T>) {
                val data = response.body() as NSAddressListResponse
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

    fun deleteAddress(addressId: String, viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.addressDelete(addressId, object :
            NSRetrofitCallback<NSSuccessResponse>(viewModelCallback, NSApiErrorHandler.ERROR_DOCTOR_LIST_DATA) {
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
