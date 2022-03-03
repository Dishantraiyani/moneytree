package com.moneytree.app.ui.retail

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
import com.moneytree.app.databinding.NsFragmentDirectRetailListBinding
import com.moneytree.app.ui.retailInfo.NSRetailInfoFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class NSRetailListFragment : NSFragment() {
    private val retailListModel: NSRetailListViewModel by lazy {
        ViewModelProvider(this).get(NSRetailListViewModel::class.java)
    }
    private var _binding: NsFragmentDirectRetailListBinding? = null

    private val retailBinding get() = _binding!!
    private var retailListAdapter: NSRetailListRecycleAdapter? = null
    companion object {
        fun newInstance() = NSRetailListFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentDirectRetailListBinding.inflate(inflater, container, false)
        return retailBinding.root
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTabSelectEvent(event: NSRetailInfoEventTab) {
        Log.d(TAG, "onTabSelectEvent: $event")
        viewCreated()
        setListener()
    }

    /**
     * View created
     */
    private fun viewCreated() {
        setRetailAdapter()
        observeViewModel()
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(retailBinding) {
            with(retailListModel) {
                srlRefresh.setOnRefreshListener {
                    pageIndex = "1"
                    getRetailListData(pageIndex, "", false, isBottomProgress = false)
                }
            }
        }
    }

    /**
     * To add data of retail in list
     */
    private fun setRetailAdapter() {
        with(retailBinding) {
            with(retailListModel) {
                rvRetailList.layoutManager = LinearLayoutManager(activity)
                retailListAdapter =
                    NSRetailListRecycleAdapter(activity, object : NSPageChangeCallback{
                        override fun onPageChange() {
                            if (retailResponse!!.nextPage) {
                                val page: Int = retailList.size/NSConstants.PAGINATION + 1
                                pageIndex = page.toString()
                                getRetailListData(pageIndex, "", true, isBottomProgress = true)
                            }
                        }
                    }, object : NSInfoSelectCallback {
                        override fun onClick(position: Int) {
                            val data = retailList[position]
                            val bundle = Bundle()
                            bundle.putString(NSConstants.KEY_RETAIL_INFO, data.directRetailOfferMainId)
                            EventBus.getDefault().post(NSFragmentChange(NSRetailInfoFragment.newInstance(bundle)))
                        }

                    })
                rvRetailList.adapter = retailListAdapter
                pageIndex = "1"
                getRetailListData(pageIndex, "", true, isBottomProgress = false)
            }
        }
    }

    private fun bottomProgress(isShowProgress: Boolean) {
        with(retailBinding) {
            cvProgress.visibility = if (isShowProgress) View.VISIBLE else View.GONE
        }
    }

    /**
     * Set retail data
     *
     * @param isRetail when data available it's true
     */
    private fun setRetailData(isRetail: Boolean) {
        with(retailListModel) {
            retailDataManage(isRetail)
            if (isRetail) {
                retailListAdapter!!.clearData()
                retailListAdapter!!.updateData(retailList)
            }
        }
    }

    /**
     * Retail data manage
     *
     * @param isRetailVisible when retail data available it's visible
     */
    private fun retailDataManage(isRetailVisible: Boolean) {
        with(retailBinding) {
            rvRetailList.visibility = if (isRetailVisible) View.VISIBLE else View.GONE
            clRetailNotFound.visibility = if (isRetailVisible) View.GONE else View.VISIBLE
        }
    }

    /**
     * To observe the view model for data changes
     */
    private fun observeViewModel() {
        with(retailBinding) {
            with(retailListModel) {
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

                isRetailDataAvailable.observe(
                    viewLifecycleOwner
                ) { isRetail ->
                    srlRefresh.isRefreshing = false
                    setRetailData(isRetail)
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
        if(event.position == 1) {
            with(retailListModel) {
                pageIndex = "1"
                if (tempRetailList.isValidList()) {
                    retailList.clear()
                    retailList.addAll(tempRetailList)
                    tempRetailList.clear()
                    setRetailData(retailList.isValidList())
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSearchStringEvent(event: SearchStringEvent) {
        if(event.position == 1) {
            with(retailListModel) {
                tempRetailList.addAll(retailList)
                getRetailListData(
                    pageIndex, event.search, true,
                    isBottomProgress = false
                )
            }
        }
    }
}