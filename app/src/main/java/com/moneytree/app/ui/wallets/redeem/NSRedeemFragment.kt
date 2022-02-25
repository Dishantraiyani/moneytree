package com.moneytree.app.ui.wallets.redeem

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.moneytree.app.R
import com.moneytree.app.common.*
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.utils.TAG
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.databinding.NsFragmentRedeemBinding
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class NSRedeemFragment : NSFragment() {
    private val redeemListModel: NSRedeemViewModel by lazy {
        ViewModelProvider(this).get(NSRedeemViewModel::class.java)
    }
    private var _binding: NsFragmentRedeemBinding? = null

    private val redeemBinding get() = _binding!!
    private var redeemListAdapter: NSRedeemRecycleAdapter? = null
    companion object {
        fun newInstance() = NSRedeemFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentRedeemBinding.inflate(inflater, container, false)
        return redeemBinding.root
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTabSelectEvent(event: NSRedemptionEventTab) {
        Log.d(TAG, "onTabSelectEvent: $event")
        viewCreated()
        setListener()
    }

    /**
     * View created
     */
    private fun viewCreated() {
        setRedeemAdapter()
        observeViewModel()
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(redeemBinding) {
            with(redeemListModel) {
                srlRefresh.setOnRefreshListener {
                    pageIndex = "1"
                    getRedeemListData(pageIndex, "", false, isBottomProgress = false)
                }
            }
        }
    }

    /**
     * To add data of redeem in list
     */
    private fun setRedeemAdapter() {
        with(redeemBinding) {
            with(redeemListModel) {
                rvRedeemList.layoutManager = LinearLayoutManager(activity)
                redeemListAdapter =
                    NSRedeemRecycleAdapter(activity, object : NSPageChangeCallback{
                        override fun onPageChange() {
                            if (redeemResponse!!.nextPage) {
                                val page: Int = redeemList.size/NSConstants.PAGINATION + 1
                                pageIndex = page.toString()
                                getRedeemListData(pageIndex, "", true, isBottomProgress = true)
                            }
                        }
                    })
                rvRedeemList.adapter = redeemListAdapter
                pageIndex = "1"
                getRedeemListData(pageIndex, "", true, isBottomProgress = false)
            }
        }
    }

    private fun bottomProgress(isShowProgress: Boolean) {
        with(redeemBinding) {
            cvProgress.visibility = if (isShowProgress) View.VISIBLE else View.GONE
        }
    }

    /**
     * Set redeem data
     *
     * @param isRedeem when data available it's true
     */
    private fun setRedeemData(isRedeem: Boolean) {
        with(redeemListModel) {
            redeemDataManage(isRedeem)
            if (isRedeem) {
                redeemListAdapter!!.clearData()
                redeemListAdapter!!.updateData(redeemList)
            }
        }
    }

    /**
     * Redeem data manage
     *
     * @param isRedeemVisible when redeem available it's visible
     */
    private fun redeemDataManage(isRedeemVisible: Boolean) {
        with(redeemBinding) {
            rvRedeemList.visibility = if (isRedeemVisible) View.VISIBLE else View.GONE
            clRedeemNotFound.visibility = if (isRedeemVisible) View.GONE else View.VISIBLE
        }
    }

    /**
     * To observe the view model for data changes
     */
    private fun observeViewModel() {
        with(redeemListModel) {
            with(redeemBinding) {
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

                isRedeemDataAvailable.observe(
                    viewLifecycleOwner
                ) { isRedeem ->
                    srlRefresh.isRefreshing = false
                    setRedeemData(isRedeem)
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSearchClose(event: SearchCloseEvent) {
        NSLog.d(tags, "onSearchClose: $event")
        with(redeemListModel) {
            pageIndex = "1"
            if (tempRedeemList.isValidList()) {
                redeemList.clear()
                redeemList.addAll(tempRedeemList)
                tempRedeemList.clear()
                setRedeemData(redeemList.isValidList())
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSearchStringEvent(event: SearchStringEvent) {
        with(redeemListModel) {
            tempRedeemList.addAll(redeemList)
            getRedeemListData(pageIndex, event.search, true, isBottomProgress = false)
        }
    }
}