package com.moneytree.app.ui.retailInfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.moneytree.app.R
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSFragment
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.databinding.NsFragmentRetailInfoBinding

class NSRetailInfoFragment : NSFragment() {
    private val retailListModel: NSRetailInfoViewModel by lazy {
        ViewModelProvider(this).get(NSRetailInfoViewModel::class.java)
    }
    private var _binding: NsFragmentRetailInfoBinding? = null

    private val retailBinding get() = _binding!!
    private var retailListAdapter: NSRetailInfoRecycleAdapter? = null

    companion object {
        fun newInstance(bundle: Bundle?) = NSRetailInfoFragment().apply {
            arguments = bundle
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            with(retailListModel) {
                retailId = it.getString(NSConstants.KEY_RETAIL_INFO)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentRetailInfoBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
        return retailBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        with(retailBinding) {
            with(layoutHeader) {
                clBack.visibility = View.VISIBLE
                tvHeaderBack.text = activity.resources.getString(R.string.retail_info)
                ivBack.visibility = View.VISIBLE
                ivSearch.visibility = View.GONE
                ivAddNew.visibility = View.GONE
            }
            setRetailAdapter()
        }
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
                    getRetailListData(pageIndex, retailId!!, false, isBottomProgress = false)
                }

                with(layoutHeader) {
                    clBack.setOnClickListener {
                       onBackPress()
                    }
                }
            }
        }
    }

    /**
     * To add data of notification in list
     */
    private fun setRetailAdapter() {
        with(retailBinding) {
            with(retailListModel) {
                rvRetailList.layoutManager = LinearLayoutManager(activity)
                retailListAdapter =
                    NSRetailInfoRecycleAdapter(activity, object : NSPageChangeCallback{
                        override fun onPageChange() {
                            if (retailResponse!!.nextPage) {
                                val page: Int = retailInfoList.size/NSConstants.PAGINATION + 1
                                pageIndex = page.toString()
                                getRetailListData(pageIndex, retailId!!, true, isBottomProgress = true)
                            }
                        }
                    })
                rvRetailList.adapter = retailListAdapter
                pageIndex = "1"
                getRetailListData(pageIndex, retailId!!, true, isBottomProgress = false)
            }
        }
    }

    private fun bottomProgress(isShowProgress: Boolean) {
        with(retailBinding) {
            cvProgress.visibility = if (isShowProgress) View.VISIBLE else View.GONE
        }
    }

    /**
     * Set notification data
     *
     * @param isRetail when data available it's true
     */
    private fun setRetailData(isRetail: Boolean) {
        with(retailListModel) {
            retailDataManage(isRetail)
            if (isRetail) {
                retailListAdapter!!.clearData()
                retailListAdapter!!.updateData(retailInfoList)
            }
        }
    }

    /**
     * Notification data manage
     *
     * @param isRetailVisible when notification available it's visible
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
        with(retailListModel) {
            with(retailBinding) {
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
}