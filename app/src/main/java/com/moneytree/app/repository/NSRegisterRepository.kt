package com.moneytree.app.repository

import com.moneytree.app.common.NSApplication
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.callbacks.NSRetrofitCallback
import com.moneytree.app.repository.network.error.NSApiErrorHandler
import com.moneytree.app.repository.network.responses.NSRegisterListResponse
import com.moneytree.app.repository.network.responses.NSSendMessageResponse
import com.moneytree.app.repository.network.responses.NSSetDefaultResponse
import com.moneytree.app.repository.network.responses.NSSuccessResponse
import retrofit2.Response

/**
 * Repository class to handle data operations related to register
 */
object NSRegisterRepository {
    private val apiManager by lazy { NSApplication.getInstance().getApiManager() }
    private var errorMessageList: MutableList<Any> = mutableListOf()

    /**
     * To get register data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun getRegisterListData(pageIndex: String, search: String, type: String,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.getRegisterListData(pageIndex, search, type, object :
            NSRetrofitCallback<NSRegisterListResponse>(viewModelCallback, NSApiErrorHandler.ERROR_REGISTER_LIST_DATA) {
            override fun <T> onResponse(response: Response<T>) {
                val data = response.body() as NSRegisterListResponse
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
	 * To save register data API
	 *
	 * @param viewModelCallback The callback to communicate back to the view model
	 */
	fun saveRegisterApi(fullName: String, email: String, mobile: String, password: String,
							viewModelCallback: NSGenericViewModelCallback
	) {
		apiManager.saveRegisterApi(fullName, email, mobile, password, object :
			NSRetrofitCallback<NSSuccessResponse>(viewModelCallback, NSApiErrorHandler.ERROR_REGISTER_SAVE_DATA) {
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


	/**
	 * To save register data API
	 *
	 * @param viewModelCallback The callback to communicate back to the view model
	 */
	fun saveRegisterDirectApi(referCode: String, fullName: String, email: String, mobile: String, password: String,
						viewModelCallback: NSGenericViewModelCallback
	) {
		apiManager.saveDirectRegisterApi(referCode, fullName, email, mobile, password, object :
			NSRetrofitCallback<NSSuccessResponse>(viewModelCallback, NSApiErrorHandler.ERROR_DIRECT_REGISTER_SAVE_DATA) {
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

	/**
	 * To save register data API
	 *
	 * @param viewModelCallback The callback to communicate back to the view model
	 */
	fun setDefault(userId: String,
							  viewModelCallback: NSGenericViewModelCallback
	) {
		apiManager.setDefault(userId, object :
			NSRetrofitCallback<NSSetDefaultResponse>(viewModelCallback, NSApiErrorHandler.ERROR_SET_DEFAULT) {
			override fun <T> onResponse(response: Response<T>) {
				val data = response.body() as NSSetDefaultResponse
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
	 * To save register data API
	 *
	 * @param viewModelCallback The callback to communicate back to the view model
	 */
	fun sendMessage(userId: String,
				   viewModelCallback: NSGenericViewModelCallback
	) {
		apiManager.sendMessage(userId, object :
			NSRetrofitCallback<NSSendMessageResponse>(viewModelCallback, NSApiErrorHandler.ERROR_SEND_MESSAGE) {
			override fun <T> onResponse(response: Response<T>) {
				val data = response.body() as NSSendMessageResponse
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
