package com.moneytree.app.ui.offers

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
import com.moneytree.app.ui.downlineReOffer.NSDownlineReOfferFragment
import com.moneytree.app.ui.repurchase.NSRePurchaseListFragment
import com.moneytree.app.ui.retail.NSRetailListFragment
import com.moneytree.app.ui.royalty.NSRoyaltyListFragment
import com.moneytree.app.ui.vouchers.joining.NSJoiningVoucherFragment
import com.moneytree.app.ui.vouchers.joining.NSPendingVoucherFragment
import com.moneytree.app.ui.vouchers.joining.NSReceiveVoucherFragment
import com.moneytree.app.ui.vouchers.joining.NSTransferVoucherFragment
import com.moneytree.app.ui.vouchers.product.NSProductVoucherFragment


/**
 * The view model class for offers. It handles the business logic to communicate with the model for the offers and provides the data to the observing UI component.
 */
class NSOffersViewModel(application: Application) : NSViewModel(application) {
    val mFragmentTitleList: MutableList<String> = ArrayList()
    val mFragmentList: MutableList<Fragment> = ArrayList()
    var isRepurchaseAdded: Boolean = false
    var isRetailAdded: Boolean = false
    var isRoyaltyAdded: Boolean = false
    var isDownlineAdded: Boolean = false
    var tabPosition: Int = 0

    fun setFragmentData(activity: Activity) {
        with(activity.resources) {
            mFragmentTitleList.clear()
            mFragmentTitleList.add(getString(R.string.repurchase))
            mFragmentTitleList.add(getString(R.string.retail_info))
            mFragmentTitleList.add(getString(R.string.royalty_offer))
            mFragmentTitleList.add(getString(R.string.downline_member))
        }
        mFragmentList.clear()
        mFragmentList.add(NSRePurchaseListFragment())
        mFragmentList.add(NSRetailListFragment())
        mFragmentList.add(NSRoyaltyListFragment())
        mFragmentList.add(NSDownlineReOfferFragment())
    }
}