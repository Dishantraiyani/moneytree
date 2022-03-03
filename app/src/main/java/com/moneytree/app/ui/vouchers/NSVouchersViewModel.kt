package com.moneytree.app.ui.vouchers

import android.app.Activity
import android.app.Application
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.R
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSVoucherRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.NSVoucherListData
import com.moneytree.app.repository.network.responses.NSVoucherListResponse
import com.moneytree.app.ui.vouchers.joining.NSJoiningVoucherFragment
import com.moneytree.app.ui.vouchers.joining.NSPendingVoucherFragment
import com.moneytree.app.ui.vouchers.joining.NSReceiveVoucherFragment
import com.moneytree.app.ui.vouchers.joining.NSTransferVoucherFragment
import com.moneytree.app.ui.vouchers.product.NSProductVoucherFragment


/**
 * The view model class for voucher. It handles the business logic to communicate with the model for the voucher and provides the data to the observing UI component.
 */
class NSVouchersViewModel(application: Application) : NSViewModel(application) {
    val mMainFragmentTitleList: MutableList<String> = ArrayList()
    val mMainFragmentList: MutableList<Fragment> = ArrayList()
    var isProductAdded: Boolean = false
    var isJoiningAdded: Boolean = false

    fun setMainFragmentData(activity: Activity) {
        with(activity.resources) {
            mMainFragmentTitleList.clear()
            mMainFragmentTitleList.add(getString(R.string.joining_voucher))
            mMainFragmentTitleList.add(getString(R.string.product_voucher))
        }
        mMainFragmentList.clear()
        mMainFragmentList.add(NSJoiningVoucherFragment())
        mMainFragmentList.add(NSProductVoucherFragment())
    }
}