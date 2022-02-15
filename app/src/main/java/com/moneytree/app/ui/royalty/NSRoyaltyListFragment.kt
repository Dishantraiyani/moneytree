package com.moneytree.app.ui.royalty

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.moneytree.app.R
import com.moneytree.app.common.*
import com.moneytree.app.common.callbacks.NSInfoSelectCallback
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.databinding.NsFragmentRoyaltyListBinding
import com.moneytree.app.ui.repurchaseInfo.NSRePurchaseInfoFragment
import com.moneytree.app.ui.royaltyInfo.NSRoyaltyInfoFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class NSRoyaltyListFragment : NSFragment() {
    private val royaltyListModel: NSRoyaltyListViewModel by lazy {
        ViewModelProvider(this).get(NSRoyaltyListViewModel::class.java)
    }
    private var _binding: NsFragmentRoyaltyListBinding? = null

    private val royaltyBinding get() = _binding!!
    private var royaltyListAdapter: NSRoyaltyListRecycleAdapter? = null
    companion object {
        fun newInstance() = NSRoyaltyListFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentRoyaltyListBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
        return royaltyBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        with(royaltyBinding) {
            with(royaltyListModel) {
                setRoyaltyAdapter()
            }
        }
        observeViewModel()
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(royaltyBinding) {
            with(royaltyListModel) {
                srlRefresh.setOnRefreshListener {
                    pageIndex = "1"
                    getRoyaltyListData(pageIndex, "", false, isBottomProgress = false)
                }
            }
        }
    }

    /**
     * To add data of notification in list
     */
    private fun setRoyaltyAdapter() {
        with(royaltyBinding) {
            with(royaltyListModel) {
                rvRoyaltyList.layoutManager = LinearLayoutManager(activity)
                royaltyListAdapter =
                    NSRoyaltyListRecycleAdapter(activity, object : NSPageChangeCallback{
                        override fun onPageChange() {
                            if (royaltyResponse!!.nextPage) {
                                val page: Int = royaltyList.size/NSConstants.PAGINATION + 1
                                pageIndex = page.toString()
                                getRoyaltyListData(pageIndex, "", true, isBottomProgress = true)
                            }
                        }
                    }, object : NSInfoSelectCallback {
                        override fun onClick(position: Int) {
                            val data = royaltyList[position]
                            val bundle = Bundle()
                            bundle.putString(NSConstants.KEY_ROYALTY_INFO, data.royaltyOfferMainId)
                            EventBus.getDefault().post(NSFragmentChange(NSRoyaltyInfoFragment.newInstance(bundle)))
                        }

                    })
                rvRoyaltyList.adapter = royaltyListAdapter
                pageIndex = "1"
                getRoyaltyListData(pageIndex, "", true, isBottomProgress = false)
            }
        }
    }

    private fun bottomProgress(isShowProgress: Boolean) {
        with(royaltyBinding) {
            cvProgress.visibility = if (isShowProgress) View.VISIBLE else View.GONE
        }
    }

    /**
     * Set notification data
     *
     * @param isNotification when data available it's true
     */
    private fun setRoyaltyData(isNotification: Boolean) {
        with(royaltyListModel) {
            notificationDataManage(isNotification)
            if (isNotification) {
                royaltyListAdapter!!.clearData()
                royaltyListAdapter!!.updateData(royaltyList)
            }
        }
    }

    /**
     * Notification data manage
     *
     * @param isTripHistoryVisible when notification available it's visible
     */
    private fun notificationDataManage(isTripHistoryVisible: Boolean) {
        with(royaltyBinding) {
            rvRoyaltyList.visibility = if (isTripHistoryVisible) View.VISIBLE else View.GONE
            clRoyaltyNotFound.visibility = if (isTripHistoryVisible) View.GONE else View.VISIBLE
        }
    }

    /**
     * To observe the view model for data changes
     */
    private fun observeViewModel() {
        with(royaltyListModel) {
            with(royaltyBinding) {
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

                isRoyaltyDataAvailable.observe(
                    viewLifecycleOwner
                ) { isNotification ->
                    srlRefresh.isRefreshing = false
                    setRoyaltyData(isNotification)
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
        with(royaltyListModel) {
            pageIndex = "1"
            if (tempRoyaltyList.isValidList()) {
                royaltyList.clear()
                royaltyList.addAll(tempRoyaltyList)
                tempRoyaltyList.clear()
                setRoyaltyData(royaltyList.isValidList())
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSearchStringEvent(event: SearchStringEvent) {
        with(royaltyListModel) {
            tempRoyaltyList.addAll(royaltyList)
            getRoyaltyListData(pageIndex, event.search, true,
                isBottomProgress = false
            )
        }
    }
}