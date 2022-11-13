package com.moneytree.app.repository

import com.moneytree.app.common.NSApplication
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.callbacks.NSRetrofitCallback
import com.moneytree.app.repository.network.error.NSApiErrorHandler
import com.moneytree.app.repository.network.responses.*
import retrofit2.Response

/**
 * Repository class to handle data operations related to voucher
 */
object NSProductRepository {
    private val apiManager by lazy { NSApplication.getInstance().getApiManager() }
    private var errorMessageList: MutableList<Any> = mutableListOf()

    /**
     * To get joining voucher data API
     *
     * @param viewModelCallback The callback to communicate back to the view model
     */
    fun getProductList(pageIndex: String, search: String, categoryId: String,
        viewModelCallback: NSGenericViewModelCallback
    ) {
        apiManager.getProductList(pageIndex, search, categoryId, object :
            NSRetrofitCallback<NSProductListResponse>(viewModelCallback, NSApiErrorHandler.ERROR_PRODUCT_LIST) {
            override fun <T> onResponse(response: Response<T>) {
                val data = response.body() as NSProductListResponse
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
	fun getProductStockList(pageIndex: String, search: String, categoryId: String = "",
					   viewModelCallback: NSGenericViewModelCallback
	) {
		apiManager.getProductStockList(pageIndex, search, categoryId, object :
			NSRetrofitCallback<NSProductListResponse>(viewModelCallback, NSApiErrorHandler.ERROR_PRODUCT_LIST) {
			override fun <T> onResponse(response: Response<T>) {
				val data = response.body() as NSProductListResponse
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
    fun getCategoryOfProducts(viewModelCallback: NSGenericViewModelCallback) {
        apiManager.getProductCategory(object :
            NSRetrofitCallback<NSCategoryListResponse>(viewModelCallback, NSApiErrorHandler.ERROR_CATEGORY_PRODUCT) {
            override fun <T> onResponse(response: Response<T>) {
                val data = response.body() as NSCategoryListResponse
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
	fun getActivationList(pageIndex: String, search: String,
					   viewModelCallback: NSGenericViewModelCallback
	) {
		apiManager.getActivationList(pageIndex, search, object :
			NSRetrofitCallback<NSActivationListResponse>(viewModelCallback, NSApiErrorHandler.ERROR_ACTIVATION_LIST) {
			override fun <T> onResponse(response: Response<T>) {
				val data = response.body() as NSActivationListResponse
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
	fun getActivatePackage(viewModelCallback: NSGenericViewModelCallback
	) {
		apiManager.getActivationPackageList(object :
			NSRetrofitCallback<NSActivationPackageResponse>(viewModelCallback, NSApiErrorHandler.ERROR_ACTIVATION_PACKAGE_LIST) {
			override fun <T> onResponse(response: Response<T>) {
				val data = response.body() as NSActivationPackageResponse
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
	fun getMemberActivatePackage(memberId: String, viewModelCallback: NSGenericViewModelCallback
	) {
		apiManager.getMemberActivationPackageList(memberId, object :
			NSRetrofitCallback<NSActivationPackageResponse>(viewModelCallback, NSApiErrorHandler.ERROR_ACTIVATION_PACKAGE_LIST) {
			override fun <T> onResponse(response: Response<T>) {
				val data = response.body() as NSActivationPackageResponse
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
	fun getActivateSave(registrationType: String, packageId: String,
						viewModelCallback: NSGenericViewModelCallback
	) {
		apiManager.activationSave(registrationType, packageId, object :
			NSRetrofitCallback<NSSuccessResponse>(viewModelCallback, NSApiErrorHandler.ERROR_ACTIVATION_PACKAGE_SAVE) {
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
	fun getActivateDirectSave(memberId: String, registrationType: String, packageId: String,
						viewModelCallback: NSGenericViewModelCallback
	) {
		apiManager.activationDirectSave(memberId, registrationType, packageId, object :
			NSRetrofitCallback<NSSuccessResponse>(viewModelCallback, NSApiErrorHandler.ERROR_ACTIVATION_PACKAGE_SAVE) {
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
	fun saveMyCart(memberId: String, walletType: String, remark: String, productList: String,
				   viewModelCallback: NSGenericViewModelCallback
	) {
		apiManager.saveMyCart(memberId, walletType, remark, productList, object :
			NSRetrofitCallback<NSSuccessResponse>(viewModelCallback, NSApiErrorHandler.ERROR_PRODUCT_SEND_DATA) {
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
	 * To make logout
	 *
	 * @param viewModelCallback The callback to communicate back to view model
	 */
	fun checkStockList(stockType: String, stockId: String, viewModelCallback: NSGenericViewModelCallback) {
		apiManager.checkStockList(stockType, stockId, object : NSRetrofitCallback<NSMemberDetailResponse>(
			viewModelCallback, NSApiErrorHandler.ERROR_CHECK_STOKE_TYPE
		) {
			override fun <T> onResponse(response: Response<T>) {
				val data = response.body() as NSMemberDetailResponse
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
