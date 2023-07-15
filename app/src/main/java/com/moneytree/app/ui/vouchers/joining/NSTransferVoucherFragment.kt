package com.moneytree.app.ui.vouchers.joining

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.moneytree.app.R
import com.moneytree.app.common.*
import com.moneytree.app.common.callbacks.NSJoiningVoucherCallback
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.utils.TAG
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.databinding.NsFragmentTransferVouchersBinding
import com.moneytree.app.ui.vouchers.NSVoucherListRecycleAdapter
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class NSTransferVoucherFragment : NSFragment(), NSJoiningVoucherCallback {
    private val voucherListModel: NSJoiningVouchersViewModel by lazy {
        ViewModelProvider(this).get(NSJoiningVouchersViewModel::class.java)
    }
    private var _binding: NsFragmentTransferVouchersBinding? = null

    private val voucherBinding get() = _binding!!
    private var voucherListAdapter: NSVoucherListRecycleAdapter? = null
    companion object {
        fun newInstance() = NSTransferVoucherFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentTransferVouchersBinding.inflate(inflater, container, false)
        return voucherBinding.root
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTabSelectEvent(event: NSTransferEventTab) {
        Log.d(TAG, "onTabSelectEvent: $event")
        if (!event.isAdded) {
            with(voucherListModel) {
                isTransferAdded = true
                viewCreated()
                setListener()
            }
        }
    }


    /**
     * View created
     */
    private fun viewCreated() {
        with(voucherListModel) {
            tabPosition = 2
            setVoucherAdapter()
        }
        observeViewModel()
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(voucherBinding) {
            with(voucherListModel) {
                srlRefresh.setOnRefreshListener {
                    pageIndex = "1"
                    getVoucherListData(pageIndex, "", false, isBottomProgress = false, this@NSTransferVoucherFragment)
                }
            }
        }
    }

    /**
     * To add data of vouchers in list
     */
    private fun setVoucherAdapter() {
        with(voucherBinding) {
            with(voucherListModel) {
                rvVoucherList.layoutManager = LinearLayoutManager(activity)
                voucherListAdapter =
                    NSVoucherListRecycleAdapter(activity, object : NSPageChangeCallback{
                        override fun onPageChange(pageNo: Int) {
                            if (voucherResponse!!.nextPage) {
                                val page: Int = voucherList.size/NSConstants.PAGINATION + 1
                                pageIndex = page.toString()
                                getVoucherListData(pageIndex, "", true, isBottomProgress = true, this@NSTransferVoucherFragment)
                            }
                        }
                    })
                rvVoucherList.adapter = voucherListAdapter
                pageIndex = "1"
                getVoucherListData(pageIndex, "", true, isBottomProgress = false, this@NSTransferVoucherFragment)
            }
        }
    }

    private fun bottomProgress(isShowProgress: Boolean) {
        with(voucherBinding) {
            cvProgress.visibility = if (isShowProgress) View.VISIBLE else View.GONE
        }
    }

    /**
     * Set voucher data
     *
     * @param isVoucher when data available it's true
     */
    private fun setVoucherData(isVoucher: Boolean) {
        with(voucherListModel) {
            voucherDataManage(isVoucher)
            if (isVoucher) {
                voucherListAdapter!!.clearData()
                voucherListAdapter!!.updateData(voucherList, tabPosition)
            }
        }
    }

    /**
     * Voucher data manage
     *
     * @param isVoucherVisible when voucher available it's visible
     */
    private fun voucherDataManage(isVoucherVisible: Boolean) {
        with(voucherBinding) {
            rvVoucherList.visibility = if (isVoucherVisible) View.VISIBLE else View.GONE
            clVoucherNotFound.visibility = if (isVoucherVisible) View.GONE else View.VISIBLE
        }
    }

    /**
     * To observe the view model for data changes
     */
    private fun observeViewModel() {
        with(voucherListModel) {
            with(voucherBinding) {
                isProgressShowing.observe(
                    viewLifecycleOwner
                ) { shouldShowProgress ->
                    updateProgress(shouldShowProgress)
                }

                isBottomProgressShowing.observe(
                    viewLifecycleOwner
                ) { isBottomProgressShowing ->
                    bottomProgress(isBottomProgressShowing)
                }

                /*isVoucherDataAvailable.observe(
                    viewLifecycleOwner
                ) { isVoucher ->
                    srlRefresh.isRefreshing = false
                    setVoucherData(isVoucher)
                }*/

                failureErrorMessage.observe(viewLifecycleOwner) { errorMessage ->
                    srlRefresh.isRefreshing = false
                    showAlertDialog(errorMessage)
                }

                apiErrors.observe(viewLifecycleOwner) { apiErrors ->
                    srlRefresh.isRefreshing = false
                    parseAndShowApiError(apiErrors)
                }

                noNetworkAlert.observe(viewLifecycleOwner) {
                    srlRefresh.isRefreshing = false
                    showNoNetworkAlertDialog(
                        getString(R.string.no_network_available),
                        getString(R.string.network_unreachable)
                    )
                }

                validationErrorId.observe(viewLifecycleOwner) { errorId ->
                    showAlertDialog(getString(errorId))
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSearchClose(event: SearchCloseEvent) {
        NSLog.d(tags, "onSearchClose: $event")
        if (event.position == 2) {
            with(voucherListModel) {
                pageIndex = "1"
                if (tempVoucherList.isValidList()) {
                    voucherList.clear()
                    voucherList.addAll(tempVoucherList)
                    tempVoucherList.clear()
                    setVoucherData(voucherList.isValidList())
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSearchStringEvent(event: SearchStringEvent) {
        if (event.position == 2) {
            with(voucherListModel) {
                tempVoucherList.addAll(voucherList)
                getVoucherListData(
                    pageIndex,
                    event.search,
                    true,
                    isBottomProgress = false,
                    this@NSTransferVoucherFragment
                )
            }
        }
    }

    override fun onResponse(isAvailable: Boolean) {
        with(voucherBinding) {
            srlRefresh.isRefreshing = false
            setVoucherData(isAvailable)
        }
    }
}