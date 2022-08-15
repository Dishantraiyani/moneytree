package com.moneytree.app.repository

import com.moneytree.app.common.NSApplication
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.callbacks.NSRetrofitCallback
import com.moneytree.app.repository.network.error.NSApiErrorHandler
import com.moneytree.app.repository.network.responses.*
import retrofit2.Response

/**
 * Repository class to handle data operations related to wallet
 */
object NSWalletRepository {
    private val apiManager by lazy { NSApplication.getInstance().getApiManager() }
    private var errorMessageList: MutableList<Any> = mutableListOf()

    /**
     * To get wallet list data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun getWalletList(pageIndex: String, search: String,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.getWalletList(pageIndex, search, object :
            NSRetrofitCallback<NSWalletListResponse>(viewModelCallback, NSApiErrorHandler.ERROR_WALLET_LIST_DATA) {
            override fun <T> onResponse(response: Response<T>) {
                val data = response.body() as NSWalletListResponse
                if (data.status) {
                    viewModelCallback.onSuccess(response.body())
                } else {
                    errorMessageList.add(data.message!!)
                    viewModelCallback.onError(errorMessageList)
                }
            }
        })
    }

	/**
	 * To get joining voucher data API
	 *
	 * @param viewModelCallback The callback to communicate back to the view model
	 */
	fun getWalletRedeemList(pageIndex: String, search: String,
									 viewModelCallback: NSGenericViewModelCallback
	) {
		apiManager.walletRedeemList(pageIndex, search, object :
			NSRetrofitCallback<NSRedeemListResponse>(viewModelCallback, NSApiErrorHandler.ERROR_REDEEM_LIST_DATA) {
			override fun <T> onResponse(response: Response<T>) {
				val data = response.body() as NSRedeemListResponse
				if (data.status) {
					viewModelCallback.onSuccess(response.body())
				} else {
					errorMessageList.add(data.message!!)
					viewModelCallback.onError(errorMessageList)
				}
			}
		})
	}

	/**
	 * To get joining voucher data API
	 *
	 * @param viewModelCallback The callback to communicate back to the view model
	 */
	fun redeemWalletSaveMoney(amount: String, password: String,
							viewModelCallback: NSGenericViewModelCallback
	) {
		apiManager.redeemWalletSave(amount, password, object :
			NSRetrofitCallback<NSSuccessResponse>(viewModelCallback, NSApiErrorHandler.ERROR_REDEEM_SAVE_DATA) {
			override fun <T> onResponse(response: Response<T>) {
				val data = response.body() as NSSuccessResponse
				if (data.status) {
					viewModelCallback.onSuccess(response.body())
				} else {
					errorMessageList.add(data.message!!)
					viewModelCallback.onError(errorMessageList)
				}
			}
		})
	}
}
