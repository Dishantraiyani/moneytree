package com.moneytree.app.ui.memberTree

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.moneytree.app.R
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSFragment
import com.moneytree.app.common.callbacks.NSPageChangeCallback
import com.moneytree.app.databinding.FragmentMemberTreeBinding
import com.moneytree.app.ui.vouchers.NSVoucherListRecycleAdapter

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
        setListener()
        return memberTreeBinding.root
    }

    private fun setListener() {
        with(memberTreeBinding) {
            with(memberTreeViewModel) {
                with(layoutHeader) {
                    clBack.setOnClickListener {
                        onBackPress()
                    }
                }
            }
        }
    }

    private fun viewCreated() {
        with(memberTreeBinding) {
            with(memberTreeViewModel) {
                with(layoutHeader) {
                    clBack.visibility = View.VISIBLE
                    if (isMemberTree == true) {
                        tvHeaderBack.text = requireActivity().resources.getString(R.string.member_tree)
                    } else {
                        tvHeaderBack.text = requireActivity().resources.getString(R.string.level_member_tree)
                    }
                    ivBack.visibility = View.VISIBLE
                    ivSearch.visibility = View.GONE
                    ivAddNew.visibility = View.GONE
                }

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
     * To add data of notification in list
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
     * Set notification data
     *
     * @param isVoucher when data available it's true
     */
    private fun setMemberData(isVoucher: Boolean) {
        with(memberTreeViewModel) {
            voucherDataManage(isVoucher)
            if (isVoucher) {
                memberTreeListAdapter!!.clearData()
                memberTreeListAdapter!!.updateData(memberList)
            }
        }
    }

    /**
     * Notification data manage
     *
     * @param isTripHistoryVisible when notification available it's visible
     */
    private fun voucherDataManage(isTripHistoryVisible: Boolean) {
        with(memberTreeBinding) {
            rvMemberTreeList.visibility = if (isTripHistoryVisible) View.VISIBLE else View.GONE
            clMemberNotFound.visibility = if (isTripHistoryVisible) View.GONE else View.VISIBLE
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