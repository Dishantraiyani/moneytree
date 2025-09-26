package com.moneytree.app.ui.vouchers.topupact.list.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.moneytree.app.R
import com.moneytree.app.common.*
import com.moneytree.app.common.utils.setSafeOnClickListener
import com.moneytree.app.common.utils.switchActivity
import com.moneytree.app.databinding.FragmentTopUpVoucherDetailBinding
import com.moneytree.app.repository.network.responses.TopUpDashboardData
import com.moneytree.app.ui.vouchers.topupact.pending.PendingVoucherListActivity
import com.moneytree.app.ui.vouchers.topupact.sponsorList.SponsorListActivity

class TopupDetailFragment : NSFragment() {
    
    private var _binding: FragmentTopUpVoucherDetailBinding? = null
    private val voucherBinding get() = _binding!!
    
    companion object {
        fun newInstance(bundle: Bundle?) = TopupDetailFragment().apply {
            arguments = bundle
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTopUpVoucherDetailBinding.inflate(inflater, container, false)
        viewCreated()
        return voucherBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        HeaderUtils(voucherBinding.layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString(R.string.top_voucher_detail), isAddNew = false)
        
        arguments?.let {
            val topUpDetail = it.getString(NSConstants.KEY_IS_TOP_UP_DETAIL)
            val model: TopUpDashboardData = Gson().fromJson(topUpDetail, TopUpDashboardData::class.java)
            setDetail(model)
        }
    }
    
    private fun setDetail(data: TopUpDashboardData) {
        voucherBinding.apply {
            txtTopupStatus.text = data.topupStatus ?: "-"
            txtTotalSponsor.text = data.totalSpomsor?.toString() ?: "0"
            txtTotalReceivable.text = data.totalRecevalble?.toString() ?: "0"
            txtTotalPayout.text = data.totalPayout?.toString() ?: "0"
            txtPendingReceivable.text = data.pendingRecevalble?.toString() ?: "0"
            txtSponsorIncome.text = data.sponsorIncome?.toString() ?: "0"
            txtTotalShare.text = data.totalShare ?: "0"
            txtShareValue.text = data.shareValue?.toString() ?: "0"
            txtTotalVoucher.text = data.totalVoucher ?: "0"
            txtUsedVoucher.text = data.usedVoucher?.toString() ?: "0"
            txtPendingVoucher.text = data.pendingVoucher ?: "0"
            txtLapseVoucher.text = data.lapseVoucher?.toString() ?: "0"
            txtUniversalIdIncome.text = data.universalIdIncome?.toString() ?: "0"
            txtTopupIncome.text = data.topupIncome?.toString() ?: "0"
            
            layoutPendingVoucher.setSafeOnClickListener {
                switchActivity(PendingVoucherListActivity::class.java)
            }
            
            layoutTotalSponsor.setSafeOnClickListener {
                switchActivity(SponsorListActivity::class.java)
            }
        }
    }
}