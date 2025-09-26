package com.moneytree.app.ui.vouchers.topupact.sponsorList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.moneytree.app.R
import com.moneytree.app.common.*
import com.moneytree.app.common.callbacks.NSJoiningVoucherCallback
import com.moneytree.app.databinding.FragmentSponsorListBinding

class SponsorListFragment : NSFragment(), NSJoiningVoucherCallback {
    private val viewModel: SponsorListViewModel by lazy {
	    ViewModelProvider(this)[SponsorListViewModel::class.java]
    }
    private var _binding: FragmentSponsorListBinding? = null

    private val voucherBinding get() = _binding!!
    private var voucherListAdapter: SponsorListRecycleAdapter? = null
    
    companion object {
        fun newInstance() = SponsorListFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSponsorListBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
        return voucherBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        HeaderUtils(voucherBinding.layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString(R.string.sponsor_title))
        
        setSponsorAdapter()
        observeViewModel()
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(viewModel) {
            with(voucherBinding) {
                srlRefresh.setOnRefreshListener {
                    getSponsorListData(false, this@SponsorListFragment)
                }
            }
        }
    }

    /**
     * To add data of vouchers in list
     */
    private fun setSponsorAdapter() {
        with(voucherBinding) {
            with(viewModel) {
                rvVoucherList.layoutManager = LinearLayoutManager(activity)
                voucherListAdapter = SponsorListRecycleAdapter(activity) {
                
                }
                rvVoucherList.adapter = voucherListAdapter
                
                getSponsorListData(true, this@SponsorListFragment)
            }
        }
    }

    private fun bottomProgress(isShowProgress: Boolean) {
        with(voucherBinding) {
            cvProgress.visibility = if (isShowProgress) View.VISIBLE else View.GONE
        }
    }

    /**
     * Set voucher data
     *
     * @param isVoucher when data available it's true
     */
    private fun setVoucherData(isVoucher: Boolean) {
        with(viewModel) {
            voucherDataManage(isVoucher)
            if (isVoucher) {
                voucherListAdapter?.clearData()
                voucherListAdapter?.updateDataValue(pendingVoucherList)
            }
        }
    }

    /**
     * Voucher data manage
     *
     * @param isVoucherVisible when voucher available it's visible
     */
    private fun voucherDataManage(isVoucherVisible: Boolean) {
        with(voucherBinding) {
            rvVoucherList.visibility = if (isVoucherVisible) View.VISIBLE else View.GONE
            clVoucherNotFound.visibility = if (isVoucherVisible) View.GONE else View.VISIBLE
        }
    }

    /**
     * To observe the view model for data changes
     */
    private fun observeViewModel() {
        with(viewModel) {
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

    override fun onResponse(isAvailable: Boolean) {
       with(voucherBinding) {
           srlRefresh.isRefreshing = false
           setVoucherData(isAvailable)
       }
    }
}