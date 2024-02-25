package com.moneytree.app.ui.mycart.kyc.main

import android.app.Activity
import android.app.Application
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.moneytree.app.R
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.ui.mycart.kyc.NSKycFragment
import com.moneytree.app.ui.mycart.kyc.bank.BankDetailFragment
import com.moneytree.app.ui.mycart.kyc.nominee.NomineeDetailFragment
import com.moneytree.app.ui.mycart.kyc.personal.PersonalDetailFragment
import com.moneytree.app.ui.vouchers.joining.NSPendingVoucherFragment
import com.moneytree.app.ui.vouchers.joining.NSReceiveVoucherFragment
import com.moneytree.app.ui.vouchers.joining.NSTransferVoucherFragment


/**
 * The view model class for product voucher. It handles the business logic to communicate with the model for the product voucher and provides the data to the observing UI component.
 */
class KycBaseViewModel(application: Application) : NSViewModel(application) {
    val titleList: MutableList<String> = ArrayList()
    val fragmentList: MutableList<Fragment> = ArrayList()


    fun setFragments(activity: Activity) {
        with(activity.resources) {
            titleList.clear()
            titleList.add(getString(R.string.personal_detail))
            titleList.add(getString(R.string.bank_detail))
            titleList.add(getString(R.string.nominee_detail))
            titleList.add(getString(R.string.pan_card))
            titleList.add(getString(R.string.aadhar_detail))
        }
        fragmentList.clear()
        fragmentList.add(PersonalDetailFragment())
        fragmentList.add(BankDetailFragment())
        fragmentList.add(NomineeDetailFragment())
        fragmentList.add(NSKycFragment.newInstance(bundleOf(NSConstants.KEY_KYC_TYPE to "pancard")))
        fragmentList.add(NSKycFragment.newInstance(bundleOf(NSConstants.KEY_KYC_TYPE to "adharcard")))
    }
}