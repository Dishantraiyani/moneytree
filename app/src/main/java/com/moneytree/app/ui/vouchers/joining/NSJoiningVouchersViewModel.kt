package com.moneytree.app.ui.vouchers.joining

import android.app.Activity
import android.app.Application
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.moneytree.app.R
import com.moneytree.app.common.NSViewModel
import com.moneytree.app.common.callbacks.NSJoiningVoucherCallback
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.repository.NSVoucherRepository
import com.moneytree.app.repository.network.callbacks.NSGenericViewModelCallback
import com.moneytree.app.repository.network.responses.NSVoucherListData
import com.moneytree.app.repository.network.responses.NSVoucherListResponse
import com.moneytree.app.ui.vouchers.product.NSProductVoucherFragment


/**
 * The view model class for joining voucher. It handles the business logic to communicate with the model for the joining voucher and provides the data to the observing UI component.
 */
class NSJoiningVouchersViewModel(application: Application) : NSViewModel(application),
    NSGenericViewModelCallback {
    var voucherList: MutableList<NSVoucherListData> = arrayListOf()
    var tempVoucherList: MutableList<NSVoucherListData> = arrayListOf()
    var isVoucherDataAvailable = MutableLiveData<Boolean>()
    var pageIndex: String = "1"
    var voucherResponse: NSVoucherListResponse? = null
    var tabPosition = 0
    private var isBottomProgressShow: Boolean = false
    private var searchData: String = ""
    val mFragmentTitleList: MutableList<String> = ArrayList()
    val mFragmentList: MutableList<Fragment> = ArrayList()
    private var nsJoiningVoucherCallBack: NSJoiningVoucherCallback? = null
    var isPendingAdded = false
    var isReceiveAdded = false
    var isTransferAdded = false
    var mainTabPosition: Int = 0

    fun setFragmentData(activity: Activity) {
        with(activity.resources) {
            mFragmentTitleList.clear()
            mFragmentTitleList.add(getString(R.string.pending))
            mFragmentTitleList.add(getString(R.string.receive))
            mFragmentTitleList.add(getString(R.string.transfer))
        }
        mFragmentList.clear()
        mFragmentList.add(NSPendingVoucherFragment())
        mFragmentList.add(NSReceiveVoucherFragment())
        mFragmentList.add(NSTransferVoucherFragment())
    }

    /**
     * Get voucher list data
     *
     */
    fun getVoucherListData(pageIndex: String, search: String, isShowProgress: Boolean, isBottomProgress: Boolean, joiningVoucherCallBack: NSJoiningVoucherCallback) {
        nsJoiningVoucherCallBack = joiningVoucherCallBack
        if (pageIndex == "1") {
            voucherList.clear()
        }
        if (isShowProgress) {
            isProgressShowing.value = true
        }
        if (isBottomProgress) {
            isBottomProgressShowing.value = true
        }
        isBottomProgressShow = isBottomProgress
        searchData = search
        when (tabPosition) {
            0 -> {
                NSVoucherRepository.getJoiningVoucherPendingData(pageIndex, search, this)
            }
            1 -> {
                NSVoucherRepository.getJoiningVoucherReceiveData(pageIndex, search, this)
            }
            else -> {
                NSVoucherRepository.getJoiningVoucherTransferData(pageIndex, search, this)
            }
        }
    }

    override fun <T> onSuccess(data: T) {
        isProgressShowing.value = false
        if (isBottomProgressShow) {
            isBottomProgressShowing.value = false
        }
        val voucherMainListData = data as NSVoucherListResponse
        voucherResponse = voucherMainListData
        if (voucherMainListData.data != null) {
            if (voucherMainListData.data.isValidList()) {
                voucherList.addAll(voucherMainListData.data!!)
                nsJoiningVoucherCallBack!!.onResponse(voucherList.isValidList())
                //isVoucherDataAvailable.value = voucherList.isValidList()
            } else if (pageIndex == "1" || searchData.isNotEmpty()){
                nsJoiningVoucherCallBack!!.onResponse(false)
                //isVoucherDataAvailable.value = false
            }
        } else if (pageIndex == "1" || searchData.isNotEmpty()){
            nsJoiningVoucherCallBack!!.onResponse(false)
            //isVoucherDataAvailable.value = false
        }
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
}