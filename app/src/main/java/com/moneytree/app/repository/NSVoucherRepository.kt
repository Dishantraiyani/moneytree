package com.moneytree.app.repository

import com.moneytree.app.common.NSApplication
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.callbacks.NSRetrofitCallback
import com.moneytree.app.repository.network.error.NSApiErrorHandler
import com.moneytree.app.repository.network.responses.NSPackageResponse
import com.moneytree.app.repository.network.responses.NSPackageVoucherQntResponse
import com.moneytree.app.repository.network.responses.NSSuccessResponse
import com.moneytree.app.repository.network.responses.NSVoucherListResponse
import retrofit2.Response

/**
 * Repository class to handle data operations related to voucher
 */
object NSVoucherRepository {
    private val apiManager by lazy { NSApplication.getInstance().getApiManager() }
    private var errorMessageList: MutableList<Any> = mutableListOf()

    /**
     * To get joining voucher data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun getJoiningVoucherPendingData(pageIndex: String, search: String,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.getJoiningVoucherPendingData(pageIndex, search, object :
            NSRetrofitCallback<NSVoucherListResponse>(viewModelCallback, NSApiErrorHandler.ERROR_VOUCHER_PENDING_DATA) {
            override fun <T> onResponse(response: Response<T>) {
                val data = response.body() as NSVoucherListResponse
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
    fun getJoiningVoucherReceiveData(pageIndex: String, search: String,
                                     viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.getJoiningVoucherReceiveData(pageIndex, search, object :
            NSRetrofitCallback<NSVoucherListResponse>(viewModelCallback, NSApiErrorHandler.ERROR_VOUCHER_RECEIVE_DATA) {
            override fun <T> onResponse(response: Response<T>) {
                val data = response.body() as NSVoucherListResponse
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
    fun getJoiningVoucherTransferData(pageIndex: String, search: String,
                                     viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.getJoiningVoucherTransferData(pageIndex, search, object :
            NSRetrofitCallback<NSVoucherListResponse>(viewModelCallback, NSApiErrorHandler.ERROR_VOUCHER_TRANSFER_DATA) {
            override fun <T> onResponse(response: Response<T>) {
                val data = response.body() as NSVoucherListResponse
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
	fun packageMasterList(viewModelCallback: NSGenericViewModelCallback
	) {
		apiManager.getPackageMasterList(object :
			NSRetrofitCallback<NSPackageResponse>(viewModelCallback, NSApiErrorHandler.ERROR_PACKAGE_MASTER_LIST) {
			override fun <T> onResponse(response: Response<T>) {
				val data = response.body() as NSPackageResponse
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
	fun getPackageViseVoucherQty(packageId: String, viewModelCallback: NSGenericViewModelCallback
	) {
		apiManager.getPackageViseVoucherQty(packageId, object :
			NSRetrofitCallback<NSPackageVoucherQntResponse>(viewModelCallback, NSApiErrorHandler.ERROR_PACKAGE_VISE_QUANTITY) {
			override fun <T> onResponse(response: Response<T>) {
				val data = response.body() as NSPackageVoucherQntResponse
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
	fun joiningVoucherTransfer(transferId: String, packageId: String, voucherQuantity: String,  viewModelCallback: NSGenericViewModelCallback
	) {
		apiManager.joiningVoucherTransfer(transferId, packageId, voucherQuantity, object :
			NSRetrofitCallback<NSSuccessResponse>(viewModelCallback, NSApiErrorHandler.ERROR_PACKAGE_VISE_TRANSFER) {
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
