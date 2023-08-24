package com.moneytree.app.repository

import com.moneytree.app.common.NSApplication
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.callbacks.NSRetrofitCallback
import com.moneytree.app.repository.network.error.NSApiErrorHandler
import com.moneytree.app.repository.network.requests.NSRechargeSaveRequest
import com.moneytree.app.repository.network.responses.*
import retrofit2.Response

/**
 * Repository class to handle data operations related to wallet
 */
object NSRechargeRepository {
    private val apiManager by lazy { NSApplication.getInstance().getApiManager() }
    private var errorMessageList: MutableList<Any> = mutableListOf()

    /**
     * To get wallet list data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun getServiceProvider(rechargeType: String, rechargeMemberId: String = "",
        viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.getServiceProvider(rechargeType, rechargeMemberId, object :
            NSRetrofitCallback<NSServiceProviderResponse>(viewModelCallback, NSApiErrorHandler.ERROR_SERVICE_PROVIDER_DATA) {
            override fun <T> onResponse(response: Response<T>) {
                val data = response.body() as NSServiceProviderResponse
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
	 * To get wallet list data API
	 *
	 * @param viewModelCallback The callback to communicate back to the view model
	 */
	fun saveRecharge(rechargeSave: NSRechargeSaveRequest,
					 viewModelCallback: NSGenericViewModelCallback
	) {
		apiManager.rechargeSave(rechargeSave, object :
			NSRetrofitCallback<NSSuccessResponse>(viewModelCallback, NSApiErrorHandler.ERROR_RECHARGE_SAVE_DATA) {
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
	 * To get wallet list data API
	 *
	 * @param viewModelCallback The callback to communicate back to the view model
	 */
	fun getRechargeFetchData(rechargeSave: NSRechargeSaveRequest,
					 viewModelCallback: NSGenericViewModelCallback
	) {
		apiManager.cancelAllRequests()
		apiManager.rechargeFetchData(rechargeSave, object :
			NSRetrofitCallback<NSRechargeFetchListResponse>(viewModelCallback, NSApiErrorHandler.ERROR_RECHARGE_FETCH_DATA) {
			override fun <T> onResponse(response: Response<T>) {
				//Do not enter conditions
				viewModelCallback.onSuccess(response.body())
			}
		})
	}


	/**
	 * To get register data API
	 *
	 * @param viewModelCallback The callback to communicate back to the view model
	 */
	fun getRechargeListData(pageIndex: String, search: String, rechargeType: String = "", statusType: String = "",
							viewModelCallback: NSGenericViewModelCallback
	) {
		apiManager.getRechargeListData(pageIndex, search, rechargeType, statusType, object :
			NSRetrofitCallback<NSRechargeListResponse>(viewModelCallback, NSApiErrorHandler.ERROR_RECHARGE_LIST_DATA) {
			override fun <T> onResponse(response: Response<T>) {
				val data = response.body() as NSRechargeListResponse
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
	 * To get register data API
	 *
	 * @param viewModelCallback The callback to communicate back to the view model
	 */
	fun qrCodeRecharge(qrUserId: String, amount: String, note: String = "", name: String, upiId: String = "",
							viewModelCallback: NSGenericViewModelCallback
	) {
		apiManager.qrScan(qrUserId, amount, note, name, upiId, object :
			NSRetrofitCallback<NSSuccessResponse>(viewModelCallback, NSApiErrorHandler.ERROR_QR_SCAN) {
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
	 * To set recharge using payment gateWay API
	 *
	 * @param viewModelCallback The callback to communicate back to the view model
	 */
	fun rechargePaymentMode(orderId: String,paymentData: String, amount: String, note: String = "",
					   viewModelCallback: NSGenericViewModelCallback
	) {
		apiManager.rechargePaymentMode(orderId, paymentData, amount, note, object :
			NSRetrofitCallback<NSSuccessResponse>(viewModelCallback, NSApiErrorHandler.ERROR_PAYMENT_MODE) {
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
