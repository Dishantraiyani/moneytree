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
    fun getWalletList(pageIndex: String, search: String, startDate: String, endDate: String, type: String,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.getWalletList(pageIndex, search, startDate, endDate, type, object :
            NSRetrofitCallback<NSWalletListResponse>(viewModelCallback, NSApiErrorHandler.ERROR_WALLET_LIST_DATA) {
            override fun <T> onResponse(response: Response<T>) {
                val data = response.body() as NSWalletListResponse
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
	 * To get joining voucher data API
	 *
	 * @param viewModelCallback The callback to communicate back to the view model
	 */
	fun getWalletRedeemList(pageIndex: String, search: String, startDate: String, endDate: String,
									 viewModelCallback: NSGenericViewModelCallback
	) {
		apiManager.walletRedeemList(pageIndex, search, startDate, endDate, object :
			NSRetrofitCallback<NSRedeemListResponse>(viewModelCallback, NSApiErrorHandler.ERROR_REDEEM_LIST_DATA) {
			override fun <T> onResponse(response: Response<T>) {
				val data = response.body() as NSRedeemListResponse
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
					errorMessageList.clear()
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
	fun transferWalletMoney(transactionId: String, amount: String, remark: String, password: String,
							  viewModelCallback: NSGenericViewModelCallback
	) {
		apiManager.transferWalletAmount(transactionId, amount, remark, password, object :
			NSRetrofitCallback<NSSuccessResponse>(viewModelCallback, NSApiErrorHandler.ERROR_TRANSFER_WALLET_AMOUNT) {
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

	fun getCoinWalletList(pageIndex: String, search: String, startDate: String, endDate: String, type: String,
					  viewModelCallback: NSGenericViewModelCallback
	) {
		apiManager.getCoinWalletList(pageIndex, search, startDate, endDate, type, object :
			NSRetrofitCallback<NSWalletListResponse>(viewModelCallback, NSApiErrorHandler.ERROR_COMMON) {
			override fun <T> onResponse(response: Response<T>) {
				val data = response.body() as NSWalletListResponse
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

	fun getPendingCoinWalletList(pageIndex: String, search: String,
						  viewModelCallback: NSGenericViewModelCallback
	) {
		apiManager.getPendingCoinWalletList(pageIndex, search, object :
			NSRetrofitCallback<NSPendingCoinWalletListResponse>(viewModelCallback, NSApiErrorHandler.ERROR_COMMON) {
			override fun <T> onResponse(response: Response<T>) {
				val data = response.body() as NSPendingCoinWalletListResponse
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
