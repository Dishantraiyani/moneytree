package com.moneytree.app.repository

import com.moneytree.app.common.NSApplication
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.callbacks.NSRetrofitCallback
import com.moneytree.app.repository.network.error.NSApiErrorHandler
import com.moneytree.app.repository.network.responses.DoctorResponse
import com.moneytree.app.repository.network.responses.NSSearchListResponse
import retrofit2.Response

/**
 * Repository class to handle data operations related to diseases
 */
object NSDoctorRepository {
    private val apiManager by lazy { NSApplication.getInstance().getApiManager() }
    private var errorMessageList: MutableList<Any> = mutableListOf()

    fun doctorList(pageIndex: String, search: String,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.getDoctorList(pageIndex, search, object :
            NSRetrofitCallback<DoctorResponse>(viewModelCallback, NSApiErrorHandler.ERROR_DOCTOR_LIST_DATA) {
            override fun <T> onResponse(response: Response<T>) {
                val data = response.body() as DoctorResponse
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
