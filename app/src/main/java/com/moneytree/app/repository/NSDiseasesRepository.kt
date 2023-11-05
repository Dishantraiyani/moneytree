package com.moneytree.app.repository

import com.moneytree.app.common.NSApplication
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.callbacks.NSRetrofitCallback
import com.moneytree.app.repository.network.error.NSApiErrorHandler
import com.moneytree.app.repository.network.responses.NSBrandResponse
import com.moneytree.app.repository.network.responses.NSDiseasesResponse
import com.moneytree.app.repository.network.responses.NSRegisterListResponse
import retrofit2.Response

/**
 * Repository class to handle data operations related to diseases
 */
object NSDiseasesRepository {
    private val apiManager by lazy { NSApplication.getInstance().getApiManager() }
    private var errorMessageList: MutableList<Any> = mutableListOf()

    fun getDiseasesListData(
        viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.getDiseasesMasterList(object :
            NSRetrofitCallback<NSDiseasesResponse>(viewModelCallback, NSApiErrorHandler.ERROR_DISEASES_LIST_DATA) {
            override fun <T> onResponse(response: Response<T>) {
                val data = response.body() as NSDiseasesResponse
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

    fun getBrandListData(
        viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.getBrandMasterList(object :
            NSRetrofitCallback<NSBrandResponse>(viewModelCallback, NSApiErrorHandler.ERROR_DISEASES_LIST_DATA) {
            override fun <T> onResponse(response: Response<T>) {
                val data = response.body() as NSBrandResponse
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
