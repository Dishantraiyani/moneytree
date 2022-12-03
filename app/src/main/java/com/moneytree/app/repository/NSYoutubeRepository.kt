package com.moneytree.app.repository

import com.moneytree.app.R
import com.moneytree.app.common.NSApplication
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.callbacks.NSRetrofitCallback
import com.moneytree.app.repository.network.error.NSApiErrorHandler
import com.moneytree.app.repository.network.responses.*
import retrofit2.Response

/**
 * Repository class to handle data operations related to voucher
 */
object NSYoutubeRepository {
    private val apiManager by lazy { NSApplication.getInstance().getApiManager() }
    private var errorMessageList: MutableList<Any> = mutableListOf()

    /**
     * To get joining voucher data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun getYoutubeVideos(youtubeRequestMap: Map<String, String>,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.getYoutubeVideos(youtubeRequestMap, object :
            NSRetrofitCallback<YoutubeResponse>(viewModelCallback, NSApiErrorHandler.ERROR_YOUTUBE_DATA) {
            override fun <T> onResponse(response: Response<T>) {
                val data = response.body() as YoutubeResponse
                if (data.kind != null) {
                    viewModelCallback.onSuccess(response.body())
                } else {
					errorMessageList.clear()
                    errorMessageList.add(NSApplication.getInstance().resources.getString(R.string.something_went_wrong))
                    viewModelCallback.onError(errorMessageList)
                }
            }
        })
    }
}
