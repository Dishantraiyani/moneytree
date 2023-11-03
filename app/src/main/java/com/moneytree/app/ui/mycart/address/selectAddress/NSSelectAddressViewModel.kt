package com.moneytree.app.ui.mycart.address.selectAddress

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSApplication
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSAddressRepository
import com.moneytree.app.repository.network.responses.NSAddressCreateResponse
import com.moneytree.app.repository.network.responses.NSAddressListResponse
import com.moneytree.app.repository.network.responses.NSSuccessResponse
import com.moneytree.app.repository.network.responses.ProductDataDTO


/**
 * The view model class for joining voucher. It handles the business logic to communicate with the model for the joining voucher and provides the data to the observing UI component.
 */
class NSSelectAddressViewModel(application: Application) : NSViewModel(application) {
    var isFromOrder: Boolean = false

    /**
     * Get voucher list data
     *
     */
    fun getAddressList(isShowProgress: Boolean, callback: (MutableList<NSAddressCreateResponse>) -> Unit) {
        if (isShowProgress) showProgress()
        callCommonApi({ obj ->
            NSAddressRepository.getAddressList(obj)
        }, { data, isSuccess ->
            hideProgress()
            if (isSuccess && data is NSAddressListResponse) {
                callback.invoke(data.data)
            } else {
                callback.invoke(arrayListOf())
            }
        })
    }

    fun deleteAddress(addressId: String, callback: (NSSuccessResponse) -> Unit) {
        showProgress()
        callCommonApi({ obj ->
            NSAddressRepository.deleteAddress(addressId, obj)
        }, { data, isSuccess ->
            hideProgress()
            if (data is NSSuccessResponse) {
                callback.invoke(data)
            }
        })
    }
}
