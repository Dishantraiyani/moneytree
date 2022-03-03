package com.moneytree.app.ui.repurchase

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.moneytree.app.R
import com.moneytree.app.common.*
import com.moneytree.app.common.callbacks.NSInfoSelectCallback
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.common.utils.TAG
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.databinding.NsFragmentRepurchaseListBinding
import com.moneytree.app.ui.repurchaseInfo.NSRePurchaseInfoFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class NSRePurchaseListFragment : NSFragment() {
    private val repurchaseListModel: NSRePurchaseListViewModel by lazy {
        ViewModelProvider(this).get(NSRePurchaseListViewModel::class.java)
    }
    private var _binding: NsFragmentRepurchaseListBinding? = null

    private val repurchaseBinding get() = _binding!!
    private var repurchaseListAdapter: NSRePurchaseListRecycleAdapter? = null
    companion object {
        fun newInstance() = NSRePurchaseListFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentRepurchaseListBinding.inflate(inflater, container, false)
        return repurchaseBinding.root
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTabSelectEvent(event: NSRepurchaseEventTab) {
        Log.d(TAG, "onTabSelectEvent: $event")
        viewCreated()
        setListener()
    }

    /**
     * View created
     */
    private fun viewCreated() {
        setRepurchaseAdapter()
        observeViewModel()
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(repurchaseBinding) {
            with(repurchaseListModel) {
                srlRefresh.setOnRefreshListener {
                    pageIndex = "1"
                    getRePurchaseListData(pageIndex, "", false, isBottomProgress = false)
                }
            }
        }
    }

    /**
     * To add data of repurchase in list
     */
    private fun setRepurchaseAdapter() {
        with(repurchaseBinding) {
            with(repurchaseListModel) {
                rvRepurchaseList.layoutManager = LinearLayoutManager(activity)
                repurchaseListAdapter =
                    NSRePurchaseListRecycleAdapter(activity, object : NSPageChangeCallback{
                        override fun onPageChange() {
                            if (rePurchaseResponse!!.nextPage) {
                                val page: Int = rePurchaseList.size/NSConstants.PAGINATION + 1
                                pageIndex = page.toString()
                                getRePurchaseListData(pageIndex, "", true, isBottomProgress = true)
                            }
                        }
                    }, object : NSInfoSelectCallback {
                        override fun onClick(position: Int) {
                            val data = rePurchaseList[position]
                            val bundle = Bundle()
                            bundle.putString(NSConstants.KEY_REPURCHASE_INFO, data.repurchaseId)
                            EventBus.getDefault().post(NSFragmentChange(NSRePurchaseInfoFragment.newInstance(bundle)))
                        }

                    })
                rvRepurchaseList.adapter = repurchaseListAdapter
                pageIndex = "1"
                getRePurchaseListData(pageIndex, "", true, isBottomProgress = false)
            }
        }
    }

    private fun bottomProgress(isShowProgress: Boolean) {
        with(repurchaseBinding) {
            cvProgress.visibility = if (isShowProgress) View.VISIBLE else View.GONE
        }
    }

    /**
     * Set repurchase data
     *
     * @param isRepurchase when data available it's true
     */
    private fun setRePurchaseData(isRepurchase: Boolean) {
        with(repurchaseListModel) {
            repurchaseDataManage(isRepurchase)
            if (isRepurchase) {
                repurchaseListAdapter!!.clearData()
                repurchaseListAdapter!!.updateData(rePurchaseList)
            }
        }
    }

    /**
     * Repurchase data manage
     *
     * @param repurchaseData when notification available it's visible
     */
    private fun repurchaseDataManage(repurchaseData: Boolean) {
        with(repurchaseBinding) {
            rvRepurchaseList.visibility = if (repurchaseData) View.VISIBLE else View.GONE
            clRepurchaseNotFound.visibility = if (repurchaseData) View.GONE else View.VISIBLE
        }
    }

    /**
     * To observe the view model for data changes
     */
    private fun observeViewModel() {
        with(repurchaseListModel) {
            with(repurchaseBinding) {
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

                isRePurchaseDataAvailable.observe(
                    viewLifecycleOwner
                ) { isNotification ->
                    srlRefresh.isRefreshing = false
                    setRePurchaseData(isNotification)
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
        if(event.position == 0) {
            with(repurchaseListModel) {
                pageIndex = "1"
                if (tempRePurchaseList.isValidList()) {
                    rePurchaseList.clear()
                    rePurchaseList.addAll(tempRePurchaseList)
                    tempRePurchaseList.clear()
                    setRePurchaseData(rePurchaseList.isValidList())
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSearchStringEvent(event: SearchStringEvent) {
        if(event.position == 0) {
            with(repurchaseListModel) {
                tempRePurchaseList.addAll(rePurchaseList)
                getRePurchaseListData(
                    pageIndex, event.search, true,
                    isBottomProgress = false
                )
            }
        }
    }
}