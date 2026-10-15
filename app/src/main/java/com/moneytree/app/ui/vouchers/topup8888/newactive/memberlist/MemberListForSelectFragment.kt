package com.moneytree.app.ui.vouchers.topup8888.newactive.memberlist

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
import com.moneytree.app.databinding.FragmentMemberSelectListBinding
import com.moneytree.app.repository.network.responses.TopUp8888MemberListData

class MemberListForSelectFragment(private var downlineList: List<TopUp8888MemberListData>,  private val selectedMember: TopUp8888MemberListData?, private val refreshCallback: (() -> Unit) -> Unit,private val clickCallback: (TopUp8888MemberListData) -> Unit, private val dismissCallback: () -> Unit) : DialogFragment() {

    private lateinit var binding: FragmentMemberSelectListBinding
    
    private var adapter: MemberListForSelectRecycleAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMemberSelectListBinding.inflate(inflater, container, false)
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
            HeaderUtils(layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString(
                R.string.select_member))
            
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
    
    fun setMemberList(downlineList: List<TopUp8888MemberListData>) {
        this.downlineList = downlineList
        adapter?.setData(downlineList)
    }

    private fun initAdapter() {
        if (adapter == null) {
            adapter = MemberListForSelectRecycleAdapter(requireActivity(), selectedMember) {
                clickCallback.invoke(it)
                dismiss()
            }
            binding.rvDownlineList.setupWithAdapter(adapter!!)
        }
        adapter?.setData(downlineList)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        dismissCallback.invoke()
    }
}