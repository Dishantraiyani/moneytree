package com.moneytree.app.ui.mycart.address

import android.app.Application
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.repository.NSAddressRepository
import com.moneytree.app.repository.network.responses.NSAddressCreateResponse
import com.moneytree.app.repository.network.responses.NSSuccessResponse


/**
 * The view model class for joining voucher. It handles the business logic to communicate with the model for the joining voucher and provides the data to the observing UI component.
 */
class NSAddressViewModel(application: Application) : NSViewModel(application) {

    var selectedAddressModel: NSAddressCreateResponse? = null
    fun addOrUpdateAddress(request: NSAddressCreateResponse, callback: ((NSSuccessResponse) -> Unit)) {
        showProgress()
        callCommonApi({ obj ->
            NSAddressRepository.addOrUpdateAddress(request, obj)
        }, { data, isSuccess ->
            hideProgress()
            if (isSuccess && data is NSSuccessResponse) {
                callback.invoke(data)
            }
        })
    }
}
