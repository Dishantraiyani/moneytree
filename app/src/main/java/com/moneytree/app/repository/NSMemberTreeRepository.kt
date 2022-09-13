package com.moneytree.app.repository

import com.moneytree.app.common.NSApplication
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.callbacks.NSRetrofitCallback
import com.moneytree.app.repository.network.error.NSApiErrorHandler
import com.moneytree.app.repository.network.responses.NSLevelMemberTreeResponse
import com.moneytree.app.repository.network.responses.NSMemberTreeResponse
import com.moneytree.app.repository.network.responses.NSUpLineListResponse
import retrofit2.Response

/**
 * Repository class to handle data operations related to member tree
 */
object NSMemberTreeRepository {
    private val apiManager by lazy { NSApplication.getInstance().getApiManager() }
    private var errorMessageList: MutableList<Any> = mutableListOf()

    /**
     * To get member tree data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun getMemberTree(viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.getMemberTreeData(object :
            NSRetrofitCallback<NSMemberTreeResponse>(viewModelCallback, NSApiErrorHandler.ERROR_MEMBER_TREE) {
            override fun <T> onResponse(response: Response<T>) {
                val data = response.body() as NSMemberTreeResponse
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

    /**
     * To get level wise data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun getLevelWiseTree(viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.getLevelWiseTree(object :
            NSRetrofitCallback<NSLevelMemberTreeResponse>(viewModelCallback, NSApiErrorHandler.ERROR_LEVEL_WISE_MEMBER_TREE) {
            override fun <T> onResponse(response: Response<T>) {
                viewModelCallback.onSuccess(response.body())
            }
        })
    }

	/**
	 * To get member tree data API
	 *
	 * @param viewModelCallback The callback to communicate back to the view model
	 */
	fun getUpLineMemberList(viewModelCallback: NSGenericViewModelCallback
	) {
		apiManager.getUpLineMembers(object :
			NSRetrofitCallback<NSUpLineListResponse>(viewModelCallback, NSApiErrorHandler.ERROR_UP_LINE_MEMBER_TREE) {
			override fun <T> onResponse(response: Response<T>) {
				val data = response.body() as NSUpLineListResponse
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
