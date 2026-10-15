package com.moneytree.app.ui.repurchaseAct.voucherlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.DialogFragment
import com.moneytree.app.R
import com.moneytree.app.common.HeaderUtils
import com.moneytree.app.common.utils.gone
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.common.utils.setSafeOnClickListener
import com.moneytree.app.common.utils.setVisibility
import com.moneytree.app.common.utils.setupWithAdapter
import com.moneytree.app.common.utils.visible
import com.moneytree.app.databinding.FragmentVoucherListBinding
import com.moneytree.app.repository.network.responses.VoucherList30000DataItem
import com.moneytree.app.ui.repurchaseAct.Repurchase3000ActivationModel

class VoucherListFragment(private var viewModel: Repurchase3000ActivationModel, private var voucherId: String?, private val refreshCallback: (() -> Unit) -> Unit, private val dismissCallback: () -> Unit) : DialogFragment() {

    private lateinit var binding: FragmentVoucherListBinding
    
    private var adapter: VoucherListRecycleAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVoucherListBinding.inflate(inflater, container, false)
        setupView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.let { window ->
            window.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            window.setBackgroundDrawableResource(android.R.color.transparent)
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowInsetsControllerCompat(window, view).isAppearanceLightStatusBars = true
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.root.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }
    }

    private fun setupView() {
        binding.apply {
            HeaderUtils(layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString(R.string.voucher_list))
            callVoucherList(false, voucherId)

            srlRefresh.setOnRefreshListener {
                callVoucherList(true, voucherId)
            }
            
            layoutHeader.ivBack.setSafeOnClickListener {
                dismiss()
            }
        }
    }

    private fun callVoucherList(isRefresh: Boolean, voucherId: String?) {
        binding.progress.visible()
        viewModel.getTopUpVoucher30000VoucherList(!isRefresh, voucherId) { voucherData ->
            binding.srlRefresh.isRefreshing = false
            binding.progress.gone()
            initAdapter(voucherData)
        }
    }

    private fun initAdapter(list: List<VoucherList30000DataItem>) {
        if (adapter == null) {
            adapter = VoucherListRecycleAdapter()
            binding.rvDownlineList.setupWithAdapter(adapter!!)
        }
        adapter?.setData(list)
        binding.clVoucherNotFound.setVisibility(!list.isValidList())
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissCallback.invoke()
    }
}