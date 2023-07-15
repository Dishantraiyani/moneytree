package com.moneytree.app.ui.mycart.stockDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.moneytree.app.R
import com.moneytree.app.common.HeaderUtils
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSFragment
import com.moneytree.app.databinding.FragmentStockInfoBinding

class StockDetailFragment : NSFragment() {
    private val stockTreeViewModel: StockDetailViewModel by lazy {
        ViewModelProvider(this)[StockDetailViewModel::class.java]
    }
    private var _binding: FragmentStockInfoBinding? = null
    private val stockBinding get() = _binding!!
    private var stockListAdapter: StockDetailRecycleAdapter? = null

    companion object {
        fun newInstance(bundle: Bundle?) = StockDetailFragment().apply {
            arguments = bundle
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            with(stockTreeViewModel) {
                 isStock = it.getBoolean(NSConstants.KEY_STOCK_TYPE)
                 stockId = it.getString(NSConstants.STOCK_DETAIL_ID)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStockInfoBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
        return stockBinding.root
    }

    private fun setListener() {
        with(stockBinding) {
            srlRefresh.setOnRefreshListener {
                stockTreeViewModel.getStockInfo(false)
            }
        }
    }

    private fun viewCreated() {
        with(stockBinding) {
            with(stockTreeViewModel) {
                HeaderUtils(layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString(if (isStock) R.string.stock_transfer else R.string.repurchase_info))
            }
        }

        setStockAdapter()
        observeViewModel()
    }

    /**
     * To add data of member tree in list
     */
    private fun setStockAdapter() {
        with(stockBinding) {
            with(stockTreeViewModel) {
                rvStockList.layoutManager = LinearLayoutManager(activity)
                stockListAdapter = StockDetailRecycleAdapter(activity, isStock)
				rvStockList.adapter = stockListAdapter
                getStockInfo(true)
            }
        }
    }

    /**
     * Set member tree data
     *
     * @param isMemberTree when data available it's true
     */
    private fun setMemberData(isMemberTree: Boolean) {
        with(stockTreeViewModel) {
            memberTreeDataManage(isMemberTree)
            if (isMemberTree) {
                stockListAdapter!!.clearData()
                stockListAdapter!!.updateData(stockList)
            }
        }
    }

    /**
     * MemberTree data manage
     *
     * @param isStockVisible when memberTree available it's visible
     */
    private fun memberTreeDataManage(isStockVisible: Boolean) {
        with(stockBinding) {
            rvStockList.visibility = if (isStockVisible) View.VISIBLE else View.GONE
            clMemberNotFound.visibility = if (isStockVisible) View.GONE else View.VISIBLE
        }
    }

    /**
     * To observe the view model for data changes
     */
    private fun observeViewModel() {
        with(stockTreeViewModel) {
            with(stockBinding) {
                isProgressShowing.observe(
                    viewLifecycleOwner
                ) { shouldShowProgress ->
                    updateProgress(shouldShowProgress)
                }

                isStockDataAvailable.observe(
                    viewLifecycleOwner
                ) { isNotification ->
                    srlRefresh.isRefreshing = false
                    setMemberData(isNotification)
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
