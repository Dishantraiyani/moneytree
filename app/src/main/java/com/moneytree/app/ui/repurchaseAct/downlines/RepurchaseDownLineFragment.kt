package com.moneytree.app.ui.repurchaseAct.downlines

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
import com.moneytree.app.common.utils.setSafeOnClickListener
import com.moneytree.app.common.utils.setupWithAdapter
import com.moneytree.app.databinding.FragmentDownlinesListBinding
import com.moneytree.app.repository.network.responses.Repurchase3000DownLineListItem

class RepurchaseDownLineFragment(private var downLineList: List<Repurchase3000DownLineListItem>, private val refreshCallback: (() -> Unit) -> Unit, private val dismissCallback: () -> Unit) : DialogFragment() {

    private lateinit var binding: FragmentDownlinesListBinding
    
    private var adapter: RepurchaseDownLineRecycleAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDownlinesListBinding.inflate(inflater, container, false)
        setupView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.let { window ->
            window.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
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
            HeaderUtils(layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString(R.string.my_down_lines))

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
    
    fun setDownLineList(downLineList: List<Repurchase3000DownLineListItem>) {
        this.downLineList = downLineList
        adapter?.setData(downLineList)
    }

    private fun initAdapter() {
        if (adapter == null) {
            adapter = RepurchaseDownLineRecycleAdapter(requireActivity())
            binding.rvDownlineList.setupWithAdapter(adapter!!)
        }
        adapter?.setData(downLineList)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        dismissCallback.invoke()
    }
}