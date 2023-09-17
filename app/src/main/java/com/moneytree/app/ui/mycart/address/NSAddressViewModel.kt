package com.moneytree.app.ui.mycart.address

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.common.NSApplication
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.repository.NSProductRepository
import com.moneytree.app.repository.NSUserRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.MemberDetailModel
import com.moneytree.app.repository.network.responses.NSMemberDetailResponse
import com.moneytree.app.repository.network.responses.NSSuccessResponse
import com.moneytree.app.repository.network.responses.ProductDataDTO


/**
 * The view model class for joining voucher. It handles the business logic to communicate with the model for the joining voucher and provides the data to the observing UI component.
 */
class NSAddressViewModel(application: Application) : NSViewModel(application) {

}
