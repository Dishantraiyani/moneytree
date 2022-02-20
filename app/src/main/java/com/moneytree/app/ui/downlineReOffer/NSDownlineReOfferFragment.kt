package com.moneytree.app.ui.downlineReOffer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.moneytree.app.R
import com.moneytree.app.common.NSDownlineEventTab
import com.moneytree.app.common.NSFragment
import com.moneytree.app.common.NSRoyaltyEventTab
import com.moneytree.app.databinding.NsFragmentDownlineReOfferBinding
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class NSDownlineReOfferFragment : NSFragment() {
    private val downlineListModel: NSDownlineReOfferViewModel by lazy {
        ViewModelProvider(this).get(NSDownlineReOfferViewModel::class.java)
    }
    private var _binding: NsFragmentDownlineReOfferBinding? = null

    private val downlineBinding get() = _binding!!
    private var downlineListAdapter: NSDownlineReOfferListRecycleAdapter? = null
    companion object {
        fun newInstance() = NSDownlineReOfferFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentDownlineReOfferBinding.inflate(inflater, container, false)
        return downlineBinding.root
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTabSelectEvent(event: NSDownlineEventTab) {
        viewCreated()
        setListener()
    }

    /**
     * View created
     */
    private fun viewCreated() {
        setDownlineAdapter()
        observeViewModel()
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(downlineBinding) {
            with(downlineListModel) {
                srlRefresh.setOnRefreshListener {
                    getDownlineListData(false)
                }
            }
        }
    }

    /**
     * To add data of downline adapter in list
     */
    private fun setDownlineAdapter() {
        with(downlineBinding) {
            with(downlineListModel) {
                rvDownlineReOfferList.layoutManager = LinearLayoutManager(activity)
                downlineListAdapter =
                    NSDownlineReOfferListRecycleAdapter()
                rvDownlineReOfferList.adapter = downlineListAdapter
                getDownlineListData(true)
            }
        }
    }

    private fun bottomProgress(isShowProgress: Boolean) {
        with(downlineBinding) {
            cvProgress.visibility = if (isShowProgress) View.VISIBLE else View.GONE
        }
    }

    /**
     * Set downline data
     *
     * @param isDownline when data available it's true
     */
    private fun setDownlineData(isDownline: Boolean) {
        with(downlineListModel) {
            downlineDataManage(isDownline)
            if (isDownline) {
                downlineListAdapter!!.clearData()
                downlineListAdapter!!.updateData(downlineList)
            }
        }
    }

    /**
     * Downline data manage
     *
     * @param isDownlineVisible when downline data available it's visible
     */
    private fun downlineDataManage(isDownlineVisible: Boolean) {
        with(downlineBinding) {
            rvDownlineReOfferList.visibility = if (isDownlineVisible) View.VISIBLE else View.GONE
            clDownlineReOfferNotFound.visibility = if (isDownlineVisible) View.GONE else View.VISIBLE
        }
    }

    /**
     * To observe the view model for data changes
     */
    private fun observeViewModel() {
        with(downlineListModel) {
            with(downlineBinding) {
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

                isDownlineDataAvailable.observe(
                    viewLifecycleOwner
                ) { isDownlineDataAvailable ->
                    srlRefresh.isRefreshing = false
                    setDownlineData(isDownlineDataAvailable)
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