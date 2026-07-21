package com.moneytree.app.ui.onlineorder.placeorder

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSApplication
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.repository.NSAddressRepository
import com.moneytree.app.repository.NSProductRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.NSAddressCreateResponse
import com.moneytree.app.repository.network.responses.NSSuccessResponse
import com.moneytree.app.repository.network.responses.PlaceOrderAddressCreateResponse
import com.moneytree.app.ui.onlineorder.OnlineOrderHelper


/**
 * The view model class for joining voucher. It handles the business logic to communicate with the model for the joining voucher and provides the data to the observing UI component.
 */
class PlaceOrderAddressViewModel(application: Application) : NSViewModel(application) {

    var selectedAddressModel: NSAddressCreateResponse? = null
    var finalPayoutAmount: Int = 0
    var isProductSendDataAvailable = MutableLiveData<Boolean>()
    var successResponse: NSSuccessResponse? = null
    var paymentAmountWithCharge: Double = 0.0
    
    fun saveOnlineOrderCart(map: HashMap<String, Any>, isShowProgress: Boolean) {
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        NSProductRepository.saveOnlineOrderCart(map, object: NSGenericViewModelCallback {
            override fun <T> onSuccess(data: T) {
                isProgressShowing.value = false
                successResponse = data as NSSuccessResponse
                if (successResponse?.status == true) {
                    OnlineOrderHelper.clearOrderList()
                }
                isProductSendDataAvailable.value = true
            }
            
            override fun onError(errors: List<Any>) {
                handleError(errors)
            }
            
            override fun onFailure(failureMessage: String?) {
                handleFailure(failureMessage)
            }
            
            override fun <T> onNoNetwork(localData: T) {
                handleNoNetwork()
            }
        } )
    }
}
