package com.moneytree.app.ui.repurchaseAct.activation

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
import com.moneytree.app.common.utils.isValidList
import com.moneytree.app.common.utils.setSafeOnClickListener
import com.moneytree.app.common.utils.setVisibility
import com.moneytree.app.common.utils.setupWithAdapter
import com.moneytree.app.databinding.FragmentActivationListBinding
import com.moneytree.app.repository.network.responses.ActivationListItem
import com.moneytree.app.ui.repurchaseAct.Repurchase3000ActivationModel
import com.moneytree.app.ui.repurchaseAct.voucherlist.VoucherListFragment

class ActivationListFragment(private var viewModel: Repurchase3000ActivationModel, private var activationList: List<ActivationListItem>, private val refreshCallback: (() -> Unit) -> Unit, private val dismissCallback: () -> Unit) : DialogFragment() {

    private lateinit var binding: FragmentActivationListBinding
    
    private var adapter: ActivationListRecycleAdapter? = null
    private var voucherListFragment: VoucherListFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentActivationListBinding.inflate(inflater, container, false)
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
            HeaderUtils(layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString(R.string.my_status))
            
            srlRefresh.setOnRefreshListener {
                refreshCallback.invoke {
                    srlRefresh.isRefreshing = false
                }
            }
            
            layoutHeader.ivBack.setSafeOnClickListener {
                dismiss()
            }
            
            initAdapter()
        }
    }

    private fun initAdapter() {
        if (adapter == null) {
            adapter = ActivationListRecycleAdapter(requireActivity()) {
                voucherListFragment = VoucherListFragment(viewModel, it.voucherId, {
                    //callTopupVoucher8888(true, it)
                }) {
                    voucherListFragment = null
                }
                voucherListFragment?.setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
                voucherListFragment?.show(childFragmentManager, "show_activation")
            }
            binding.rvDownlineList.setupWithAdapter(adapter!!)
        }
        adapter?.setData(activationList)
        binding.clVoucherNotFound.setVisibility(!activationList.isValidList())
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissCallback.invoke()
    }
}