package com.moneytree.app.ui.wallets

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.moneytree.app.R
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.ui.wallets.redeem.NSRedeemFragment
import com.moneytree.app.ui.wallets.transaction.NSTransactionFragment


/**
 * The view model class for wallets. It handles the business logic to communicate with the model for the wallet and provides the data to the observing UI component.
 */
class NSWalletsViewModel(application: Application) : NSViewModel(application) {
    val mFragmentTitleList: MutableList<String> = ArrayList()
    val mFragmentList: MutableList<Fragment> = ArrayList()
    var tabPosition: Int = 0
    var isTransactionAdded = false
    var isRedemptionAdded = false

    fun setFragmentData(activity: FragmentActivity) {
        with(activity.resources) {
            mFragmentTitleList.clear()
            mFragmentTitleList.add(getString(R.string.transactions_title))
            mFragmentTitleList.add(getString(R.string.redeem_title))
        }
        mFragmentList.clear()
        mFragmentList.add(NSTransactionFragment())
        mFragmentList.add(NSRedeemFragment())
    }
}