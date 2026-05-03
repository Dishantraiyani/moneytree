package com.moneytree.app.ui.vouchers.topup8888.downlines

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.moneytree.app.R
import com.moneytree.app.common.HeaderUtils
import com.moneytree.app.common.utils.setSafeOnClickListener
import com.moneytree.app.common.utils.setupWithAdapter
import com.moneytree.app.databinding.FragmentDownlinesListBinding
import com.moneytree.app.repository.network.responses.DownlineListItem

class DownlineFragment(private var downlineList: List<DownlineListItem>, private val refreshCallback: (() -> Unit) -> Unit, private val dismissCallback: () -> Unit) : DialogFragment() {

    private lateinit var binding: FragmentDownlinesListBinding
    private val viewModel: DownlineViewModel by lazy {
        ViewModelProvider(this)[DownlineViewModel::class.java]
    }
    
    private var adapter: DownlineRecycleAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDownlinesListBinding.inflate(inflater, container, false)
        setupView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    private fun setupView() {
        binding.apply {
            viewModel.apply {
                HeaderUtils(layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString(
                    R.string.downline_members_active_status))

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
    }
    
    fun setDownlineList(downlineList: List<DownlineListItem>) {
        this.downlineList = downlineList
        adapter?.setData(downlineList)
    }

    private fun initAdapter() {
        if (adapter == null) {
            adapter = DownlineRecycleAdapter(requireActivity())
            binding.rvDownlineList.setupWithAdapter(adapter!!)
        }
        adapter?.setData(downlineList)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        dismissCallback.invoke()
    }
}