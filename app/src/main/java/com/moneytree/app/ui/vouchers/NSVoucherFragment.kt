package com.moneytree.app.ui.vouchers

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.moneytree.app.R
import com.moneytree.app.common.BackPressEvent
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSFragment
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.databinding.NsFragmentVouchersBinding
import org.greenrobot.eventbus.EventBus

class NSVoucherFragment : NSFragment() {
    private val voucherListModel: NSVouchersViewModel by lazy {
        ViewModelProvider(this).get(NSVouchersViewModel::class.java)
    }
    private var _binding: NsFragmentVouchersBinding? = null

    private val voucherBinding get() = _binding!!
    private var voucherListAdapter: NSVoucherListRecycleAdapter? = null
    companion object {
        fun newInstance() = NSVoucherFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentVouchersBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
        return voucherBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        with(voucherBinding) {
            with(voucherListModel) {
                with(layoutHeader) {
                    clBack.visibility = View.VISIBLE
                    tvHeaderBack.text = activity.resources.getString(R.string.vouchers)
                    ivBack.visibility = View.VISIBLE
                    ivSearch.visibility = View.VISIBLE
                }
                setVoucherAdapter()
            }
        }
        addTabs()
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
                    getVoucherListData(pageIndex, "", false, isBottomProgress = false)
                }

                with(layoutHeader) {
                    clBack.setOnClickListener {
                        EventBus.getDefault().post(BackPressEvent())
                    }

                    ivSearch.setOnClickListener {
                        cardSearch.visibility = View.VISIBLE
                    }

                    ivClose.setOnClickListener {
                        cardSearch.visibility = View.GONE
                        etSearch.setText("")
                        hideKeyboard(cardSearch)
                        pageIndex = "1"
                        if (tempVoucherList.isValidList()) {
                            voucherList.clear()
                            voucherList.addAll(tempVoucherList)
                            tempVoucherList.clear()
                            setVoucherData(voucherList.isValidList())
                        }
                    }

                    etSearch.setOnKeyListener(object: View.OnKeyListener{
                        override fun onKey(p0: View?, keyCode: Int, event: KeyEvent): Boolean {
                            if (event.action == KeyEvent.ACTION_DOWN) {
                                when (keyCode) {
                                    KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                                        val strSearch = etSearch.text.toString()
                                        if (strSearch.isNotEmpty()) {
                                            hideKeyboard(cardSearch)
                                            tempVoucherList.addAll(voucherList)
                                            getVoucherListData(pageIndex, strSearch, true,
                                                isBottomProgress = false
                                            )
                                        }
                                        return true
                                    }
                                }
                            }
                            return false
                        }
                    })

                    tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
                        override fun onTabSelected(tab: TabLayout.Tab?) {
                            tabPosition = tab!!.position
                            pageIndex = "1"
                            if (voucherListAdapter != null) {
                                voucherListAdapter!!.clearData()
                            }
                            getVoucherListData(pageIndex, "", true, isBottomProgress = false)
                        }

                        override fun onTabUnselected(tab: TabLayout.Tab?) {

                        }

                        override fun onTabReselected(tab: TabLayout.Tab?) {

                        }

                    })

                }
            }
        }
    }

    private fun addTabs() {
        with(voucherBinding) {
            val tabList = activity.resources.getStringArray(R.array.voucher_tab)
            for (tab in tabList) {
                tabLayout.addTab(tabLayout.newTab().setText(tab))
            }
        }
    }

    /**
     * To add data of notification in list
     */
    private fun setVoucherAdapter() {
        with(voucherBinding) {
            with(voucherListModel) {
                rvVoucherList.layoutManager = LinearLayoutManager(activity)
                voucherListAdapter =
                    NSVoucherListRecycleAdapter(activity, object : NSPageChangeCallback{
                        override fun onPageChange() {
                            if (voucherResponse!!.nextPage) {
                                val page: Int = voucherList.size/NSConstants.PAGINATION + 1
                                pageIndex = page.toString()
                                getVoucherListData(pageIndex, "", true, isBottomProgress = true)
                            }
                        }
                    })
                rvVoucherList.adapter = voucherListAdapter
                pageIndex = "1"
                getVoucherListData(pageIndex, "", true, isBottomProgress = false)
            }
        }
    }

    private fun bottomProgress(isShowProgress: Boolean) {
        with(voucherBinding) {
            cvProgress.visibility = if (isShowProgress) View.VISIBLE else View.GONE
        }
    }

    /**
     * Set notification data
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
     * Notification data manage
     *
     * @param isTripHistoryVisible when notification available it's visible
     */
    private fun voucherDataManage(isTripHistoryVisible: Boolean) {
        with(voucherBinding) {
            rvVoucherList.visibility = if (isTripHistoryVisible) View.VISIBLE else View.GONE
            clVoucherNotFound.visibility = if (isTripHistoryVisible) View.GONE else View.VISIBLE
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

                isVoucherDataAvailable.observe(
                    viewLifecycleOwner
                ) { isNotification ->
                    srlRefresh.isRefreshing = false
                    setVoucherData(isNotification)
                }

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

}