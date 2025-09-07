package com.moneytree.app.repository

import com.moneytree.app.common.NSApplication
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.callbacks.NSRetrofitCallback
import com.moneytree.app.repository.network.error.NSApiErrorHandler
import com.moneytree.app.repository.network.requests.VoucherActiveSaveModel
import com.moneytree.app.repository.network.requests.VoucherTransferModel
import com.moneytree.app.repository.network.responses.DspAndSponsorModel
import com.moneytree.app.repository.network.responses.NSPackageResponse
import com.moneytree.app.repository.network.responses.NSPackageVoucherQntResponse
import com.moneytree.app.repository.network.responses.NSSuccessResponse
import com.moneytree.app.repository.network.responses.NSVoucherListResponse
import com.moneytree.app.repository.network.responses.TopUpDashboardResponse
import com.moneytree.app.repository.network.responses.TopUpVoucherAvailableListResponse
import com.moneytree.app.repository.network.responses.TopUpVoucherListResponse
import com.moneytree.app.repository.network.responses.TopUpVoucherQntResponse
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
	
	fun getTopUpVoucherTransferList(viewModelCallback: NSGenericViewModelCallback) {
		apiManager.getTopUpVoucherTransferList(object :
			NSRetrofitCallback<TopUpVoucherListResponse>(viewModelCallback, NSApiErrorHandler.ERROR_VOUCHER_TOPUP_VOUCHER_TRANSFER) {
			override fun <T> onResponse(response: Response<T>) {
				val data = response.body() as TopUpVoucherListResponse
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
	
	fun getTopUpVoucherQuantity(viewModelCallback: NSGenericViewModelCallback) {
		apiManager.getTopUpVoucherQuantity(object :
			NSRetrofitCallback<TopUpVoucherQntResponse>(viewModelCallback, NSApiErrorHandler.ERROR_VOUCHER_TOPUP_VOUCHER_TRANSFER) {
			override fun <T> onResponse(response: Response<T>) {
				val data = response.body() as TopUpVoucherQntResponse
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
	
	fun saveTopUpVoucher(model: VoucherTransferModel, viewModelCallback: NSGenericViewModelCallback) {
		apiManager.saveVoucherTransfer(model, object :
			NSRetrofitCallback<NSSuccessResponse>(viewModelCallback, NSApiErrorHandler.ERROR_VOUCHER_TOPUP_VOUCHER_TRANSFER) {
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
	
	fun getTopUpVoucherAvailableList(viewModelCallback: NSGenericViewModelCallback) {
		apiManager.getTopUpVoucherAvailableList(object :
			NSRetrofitCallback<TopUpVoucherAvailableListResponse>(viewModelCallback, NSApiErrorHandler.ERROR_VOUCHER_TOPUP_VOUCHER_TRANSFER) {
			override fun <T> onResponse(response: Response<T>) {
				val data = response.body() as TopUpVoucherAvailableListResponse
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
	
	fun checkDspAndSponsor(memberType: String, memberCode: String, viewModelCallback: NSGenericViewModelCallback) {
		apiManager.checkDspAndSponsor(memberType, memberCode, object :
			NSRetrofitCallback<DspAndSponsorModel>(viewModelCallback, NSApiErrorHandler.ERROR_VOUCHER_TOPUP_VOUCHER_TRANSFER) {
			override fun <T> onResponse(response: Response<T>) {
				val data = response.body() as DspAndSponsorModel?
				if (data?.status == true) {
					viewModelCallback.onSuccess(response.body())
				} else {
					errorMessageList.clear()
					errorMessageList.add(data?.message?:"")
					viewModelCallback.onError(errorMessageList)
				}
			}
		})
	}
	
	fun saveTopUpActiveVoucher(model: VoucherActiveSaveModel, viewModelCallback: NSGenericViewModelCallback) {
		apiManager.saveTopUpActiveVoucher(model, object :
			NSRetrofitCallback<NSSuccessResponse>(viewModelCallback, NSApiErrorHandler.ERROR_VOUCHER_TOPUP_VOUCHER_TRANSFER) {
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
	
	fun getTopUpDashboardList(viewModelCallback: NSGenericViewModelCallback) {
		apiManager.getTopUpDashboardList(object :
			NSRetrofitCallback<TopUpDashboardResponse>(viewModelCallback, NSApiErrorHandler.ERROR_VOUCHER_TOPUP_VOUCHER_TRANSFER) {
			override fun <T> onResponse(response: Response<T>) {
				val data = response.body() as TopUpDashboardResponse?
				if (data?.status == true) {
					viewModelCallback.onSuccess(response.body())
				} else {
					errorMessageList.clear()
					errorMessageList.add(data?.message?:"")
					viewModelCallback.onError(errorMessageList)
				}
			}
		})
	}
}
