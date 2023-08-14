package com.moneytree.app.repository

import com.moneytree.app.common.NSApplication
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.callbacks.NSRetrofitCallback
import com.moneytree.app.repository.network.error.NSApiErrorHandler
import com.moneytree.app.repository.network.requests.NSDoctorSendRequest
import com.moneytree.app.repository.network.responses.DoctorHistoryResponse
import com.moneytree.app.repository.network.responses.DoctorResponse
import com.moneytree.app.repository.network.responses.NSSearchListResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
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

    fun sendDoctorRequest(request: NSDoctorSendRequest,image: List<MultipartBody.Part>,
                          viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.doctorRequestSend(request, image, object :
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

    fun doctorHistoryList(pageIndex: String, search: String,
                   viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.getDoctorHistoryList(pageIndex, search, object :
            NSRetrofitCallback<DoctorHistoryResponse>(viewModelCallback, NSApiErrorHandler.ERROR_DOCTOR_LIST_DATA) {
            override fun <T> onResponse(response: Response<T>) {
                val data = response.body() as DoctorHistoryResponse
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
