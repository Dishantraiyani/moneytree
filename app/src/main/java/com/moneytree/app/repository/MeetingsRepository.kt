package com.moneytree.app.repository

import com.moneytree.app.common.NSApplication
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.callbacks.NSRetrofitCallback
import com.moneytree.app.repository.network.error.NSApiErrorHandler
import com.moneytree.app.repository.network.responses.MeetingsListResponse
import com.moneytree.app.repository.network.responses.NSSuccessResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

/**
 * Repository class to handle data operations related to diseases
 */
object MeetingsRepository {
    private val apiManager by lazy { NSApplication.getInstance().getApiManager() }
    private var errorMessageList: MutableList<Any> = mutableListOf()

    fun saveMeetings(map: HashMap<String, RequestBody>, image: MultipartBody.Part?, viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.saveMeetings(map, image, object :
            NSRetrofitCallback<NSSuccessResponse>(viewModelCallback, NSApiErrorHandler.ERROR_SAVE_MEETING) {
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

    fun getMeetingList(viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.getMeetingsList(object :
            NSRetrofitCallback<MeetingsListResponse>(viewModelCallback, NSApiErrorHandler.ERROR_MEETING_LIST) {
            override fun <T> onResponse(response: Response<T>) {
                val data = response.body() as MeetingsListResponse
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

    fun deleteMeeting(eventId: String, viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.deleteMeetings(eventId, object :
            NSRetrofitCallback<NSSuccessResponse>(viewModelCallback, NSApiErrorHandler.ERROR_DELETE_MEETING) {
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
