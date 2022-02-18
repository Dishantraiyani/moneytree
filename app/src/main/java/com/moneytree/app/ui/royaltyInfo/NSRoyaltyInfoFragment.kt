package com.moneytree.app.ui.royaltyInfo

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

class NSRoyaltyInfoFragment : NSFragment() {
    private val royaltyListModel: NSRoyaltyInfoViewModel by lazy {
        ViewModelProvider(this).get(NSRoyaltyInfoViewModel::class.java)
    }
    private var _binding: NsFragmentRetailInfoBinding? = null

    private val royaltyBinding get() = _binding!!
    private var royaltyListAdapter: NSRoyaltyInfoRecycleAdapter? = null

    companion object {
        fun newInstance(bundle: Bundle?) = NSRoyaltyInfoFragment().apply {
            arguments = bundle
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            with(royaltyListModel) {
                royaltyId = it.getString(NSConstants.KEY_ROYALTY_INFO)
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
        return royaltyBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        with(royaltyBinding) {
            with(layoutHeader) {
                clBack.visibility = View.VISIBLE
                tvHeaderBack.text = activity.resources.getString(R.string.royalty_info)
                ivBack.visibility = View.VISIBLE
                ivSearch.visibility = View.GONE
                ivAddNew.visibility = View.GONE
            }
            setRoyaltyAdapter()
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
                    getRoyaltyListData(pageIndex, royaltyId!!, false, isBottomProgress = false)
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
     * To add data of royalty in list
     */
    private fun setRoyaltyAdapter() {
        with(royaltyBinding) {
            with(royaltyListModel) {
                rvRetailList.layoutManager = LinearLayoutManager(activity)
                royaltyListAdapter =
                    NSRoyaltyInfoRecycleAdapter(activity, object : NSPageChangeCallback{
                        override fun onPageChange() {
                            if (royaltyResponse!!.nextPage) {
                                val page: Int = royaltyInfoList.size/NSConstants.PAGINATION + 1
                                pageIndex = page.toString()
                                getRoyaltyListData(pageIndex, royaltyId!!, true, isBottomProgress = true)
                            }
                        }
                    })
                rvRetailList.adapter = royaltyListAdapter
                pageIndex = "1"
                getRoyaltyListData(pageIndex, royaltyId!!, true, isBottomProgress = false)
            }
        }
    }

    private fun bottomProgress(isShowProgress: Boolean) {
        with(royaltyBinding) {
            cvProgress.visibility = if (isShowProgress) View.VISIBLE else View.GONE
        }
    }

    /**
     * Set royalty data
     *
     * @param isRoyalty when data available it's true
     */
    private fun setRegisterData(isRoyalty: Boolean) {
        with(royaltyListModel) {
            royaltyDataManage(isRoyalty)
            if (isRoyalty) {
                royaltyListAdapter!!.clearData()
                royaltyListAdapter!!.updateData(royaltyInfoList)
            }
        }
    }

    /**
     * Royalty data manage
     *
     * @param isRoyaltyInfo when royalty available it's visible
     */
    private fun royaltyDataManage(isRoyaltyInfo: Boolean) {
        with(royaltyBinding) {
            rvRetailList.visibility = if (isRoyaltyInfo) View.VISIBLE else View.GONE
            clRetailNotFound.visibility = if (isRoyaltyInfo) View.GONE else View.VISIBLE
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
                ) { isRoyalty ->
                    srlRefresh.isRefreshing = false
                    setRegisterData(isRoyalty)
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