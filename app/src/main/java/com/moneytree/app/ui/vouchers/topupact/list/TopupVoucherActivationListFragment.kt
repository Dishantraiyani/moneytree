package com.moneytree.app.ui.vouchers.topupact.list

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.moneytree.app.R
import com.moneytree.app.common.*
import com.moneytree.app.common.utils.setSafeOnClickListener
import com.moneytree.app.common.utils.setVisibility
import com.moneytree.app.common.utils.switchActivity
import com.moneytree.app.common.utils.switchResultActivity
import com.moneytree.app.databinding.FragmentTopUpVoucherActivationListBinding
import com.moneytree.app.repository.network.responses.TopUpDashboardData
import com.moneytree.app.ui.vouchers.topupact.TopUpVoucherActivationActivity
import com.moneytree.app.ui.vouchers.topupact.list.detail.TopUpDetailActivity

class TopupVoucherActivationListFragment : NSFragment() {
    private val voucherListModel: TopUpVoucherActivationListViewModel by lazy {
	    ViewModelProvider(this)[TopUpVoucherActivationListViewModel::class.java]
    }
    private var _binding: FragmentTopUpVoucherActivationListBinding? = null

    private val voucherBinding get() = _binding!!
    
    private val topUpDataResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            if (resultCode == RESULT_OK) {
                voucherListModel.getVoucherListData(true)
            }
        }
    
    companion object {
        fun newInstance() = TopupVoucherActivationListFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTopUpVoucherActivationListBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
        return voucherBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        HeaderUtils(voucherBinding.layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString(R.string.top_voucher_activation), isAddNew = false)
        observeViewModel()
        voucherListModel.getVoucherListData(true)
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(voucherListModel) {
            with(voucherBinding) {
                
                layoutHeader.ivAddNew.setSafeOnClickListener {
                    switchResultActivity(topUpDataResult, TopUpVoucherActivationActivity::class.java, bundleOf(NSConstants.KEY_IS_VOUCHER_FROM_TRANSFER to true))
                }
            }
        }
    }
    
    private fun setVoucherData(dashboardData: TopUpDashboardData?) {
        voucherBinding.apply {
            tvActivationDate.text = dashboardData?.activationDate
            tvDspId.text = dashboardData?.dspId
            tvSponsorId.text = dashboardData?.sponsorId
            tvOptionId.text = dashboardData?.option
            nsvScroll.setVisibility(dashboardData?.dspId != null)
            clVoucherNotFound.setVisibility(dashboardData?.dspId == null)
            HeaderUtils(voucherBinding.layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString(R.string.top_voucher_activation), isAddNew = !dashboardData?.topupStatus.equals("Active"))
            
            if (dashboardData != null) {
                btnViewDetail.setSafeOnClickListener {
                    switchActivity(TopUpDetailActivity::class.java, bundleOf(NSConstants.KEY_IS_TOP_UP_DETAIL to Gson().toJson(dashboardData)))
                }
            }
        }
    }

    /**
     * To observe the view model for data changes
     */
    private fun observeViewModel() {
        with(voucherListModel) {
            with(voucherBinding) {
                isProgressShowing.observe(
                    viewLifecycleOwner
                ) { shouldShowProgress ->
                    updateProgress(shouldShowProgress)
                }

                isVoucherDataAvailable.observe(
                    viewLifecycleOwner
                ) { voucherData ->
                    setVoucherData(voucherData)
                }

                failureErrorMessage.observe(viewLifecycleOwner) { errorMessage ->
                    showAlertDialog(errorMessage)
                }

                apiErrors.observe(viewLifecycleOwner) { apiErrors ->
                    parseAndShowApiError(apiErrors)
                }

                noNetworkAlert.observe(viewLifecycleOwner) {
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