package com.moneytree.app.ui.vouchers.topup8888.newactive.memberlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
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