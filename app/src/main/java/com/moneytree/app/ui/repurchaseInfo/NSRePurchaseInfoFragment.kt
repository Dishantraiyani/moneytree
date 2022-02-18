package com.moneytree.app.ui.repurchaseInfo

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
import com.moneytree.app.databinding.NsFragmentRepurchaseInfoBinding

class NSRePurchaseInfoFragment : NSFragment() {
    private val repurchaseListModel: NSRePurchaseInfoViewModel by lazy {
        ViewModelProvider(this).get(NSRePurchaseInfoViewModel::class.java)
    }
    private var _binding: NsFragmentRepurchaseInfoBinding? = null

    private val repurchaseBinding get() = _binding!!
    private var repurchaseListAdapter: NSRePurchaseInfoRecycleAdapter? = null

    companion object {
        fun newInstance(bundle: Bundle?) = NSRePurchaseInfoFragment().apply {
            arguments = bundle
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            with(repurchaseListModel) {
                repurchaseId = it.getString(NSConstants.KEY_REPURCHASE_INFO)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentRepurchaseInfoBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
        return repurchaseBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        with(repurchaseBinding) {
            with(layoutHeader) {
                clBack.visibility = View.VISIBLE
                tvHeaderBack.text = activity.resources.getString(R.string.repurchase_info)
                ivBack.visibility = View.VISIBLE
                ivSearch.visibility = View.GONE
                ivAddNew.visibility = View.GONE
            }
            setRepurchaseAdapter()
        }
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
                    getRePurchaseListData(pageIndex, repurchaseId!!, false, isBottomProgress = false)
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
     * To add data of repurchase in list
     */
    private fun setRepurchaseAdapter() {
        with(repurchaseBinding) {
            with(repurchaseListModel) {
                rvRepurchaseList.layoutManager = LinearLayoutManager(activity)
                repurchaseListAdapter =
                    NSRePurchaseInfoRecycleAdapter(activity, object : NSPageChangeCallback{
                        override fun onPageChange() {
                            if (rePurchaseResponse!!.nextPage) {
                                val page: Int = rePurchaseInfoList.size/NSConstants.PAGINATION + 1
                                pageIndex = page.toString()
                                getRePurchaseListData(pageIndex, repurchaseId!!, true, isBottomProgress = true)
                            }
                        }
                    })
                rvRepurchaseList.adapter = repurchaseListAdapter
                pageIndex = "1"
                getRePurchaseListData(pageIndex, repurchaseId!!, true, isBottomProgress = false)
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
                repurchaseListAdapter!!.updateData(rePurchaseInfoList)
            }
        }
    }

    /**
     * Repurchase data manage
     *
     * @param isRepurchaseDataAvailable when repurchase available it's visible
     */
    private fun repurchaseDataManage(isRepurchaseDataAvailable: Boolean) {
        with(repurchaseBinding) {
            rvRepurchaseList.visibility = if (isRepurchaseDataAvailable) View.VISIBLE else View.GONE
            clRepurchaseNotFound.visibility = if (isRepurchaseDataAvailable) View.GONE else View.VISIBLE
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
                ) { isRePurchaseDataAvailable ->
                    srlRefresh.isRefreshing = false
                    setRePurchaseData(isRePurchaseDataAvailable)
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