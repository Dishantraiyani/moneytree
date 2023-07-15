package com.moneytree.app.ui.memberTree

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
import com.moneytree.app.databinding.FragmentMemberTreeBinding

class MemberTreeFragment : NSFragment() {
    private val memberTreeViewModel: MemberTreeViewModel by lazy {
        ViewModelProvider(this)[MemberTreeViewModel::class.java]
    }
    private var _binding: FragmentMemberTreeBinding? = null
    private val memberTreeBinding get() = _binding!!
    private var memberTreeListAdapter: MemberTreeRecycleAdapter? = null

    companion object {
        fun newInstance(bundle: Bundle?) = MemberTreeFragment().apply {
            arguments = bundle
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            with(memberTreeViewModel) {
                isMemberTree = it.getBoolean(NSConstants.MEMBER_TREE_ENABLE)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMemberTreeBinding.inflate(inflater, container, false)
        viewCreated()
        return memberTreeBinding.root
    }

    private fun viewCreated() {
        with(memberTreeBinding) {
            with(memberTreeViewModel) {
                HeaderUtils(layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString(if (isMemberTree == true) R.string.member_tree else R.string.level_member_tree))
                if (isMemberTree == true) {
                    tvMemberNotFoundSub.text = requireActivity().resources.getString(R.string.no_member_tree_available)
                } else {
                    tvMemberNotFoundSub.text = requireActivity().resources.getString(R.string.no_level_tree_available)
                }
            }
        }

        setMemberTreeAdapter()
        observeViewModel()
    }

    /**
     * To add data of member tree in list
     */
    private fun setMemberTreeAdapter() {
        with(memberTreeBinding) {
            with(memberTreeViewModel) {
                rvMemberTreeList.layoutManager = LinearLayoutManager(activity)
                memberTreeListAdapter = MemberTreeRecycleAdapter(activity)
                rvMemberTreeList.adapter = memberTreeListAdapter
                getMemberTreeData(true)
            }
        }
    }

    /**
     * Set member tree data
     *
     * @param isMemberTree when data available it's true
     */
    private fun setMemberData(isMemberTree: Boolean) {
        with(memberTreeViewModel) {
            memberTreeDataManage(isMemberTree)
            if (isMemberTree) {
                memberTreeListAdapter!!.clearData()
                memberTreeListAdapter!!.updateData(memberList)
            }
        }
    }

    /**
     * MemberTree data manage
     *
     * @param isMemberTreeVisible when memberTree available it's visible
     */
    private fun memberTreeDataManage(isMemberTreeVisible: Boolean) {
        with(memberTreeBinding) {
            rvMemberTreeList.visibility = if (isMemberTreeVisible) View.VISIBLE else View.GONE
            clMemberNotFound.visibility = if (isMemberTreeVisible) View.GONE else View.VISIBLE
        }
    }

    /**
     * To observe the view model for data changes
     */
    private fun observeViewModel() {
        with(memberTreeViewModel) {
            with(memberTreeBinding) {
                isProgressShowing.observe(
                    viewLifecycleOwner
                ) { shouldShowProgress ->
                    updateProgress(shouldShowProgress)
                }

                isMemberDataAvailable.observe(
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