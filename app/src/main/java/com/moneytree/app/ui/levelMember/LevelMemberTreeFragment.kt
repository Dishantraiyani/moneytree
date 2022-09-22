package com.moneytree.app.ui.levelMember

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.moneytree.app.R
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSFragment
import com.moneytree.app.databinding.FragmentLevelMemberTreeBinding

class LevelMemberTreeFragment : NSFragment() {
    private val levelMemberTreeViewModel: LevelMemberTreeViewModel by lazy {
        ViewModelProvider(this)[LevelMemberTreeViewModel::class.java]
    }
    private var _binding: FragmentLevelMemberTreeBinding? = null
    private val levelMemberTreeBinding get() = _binding!!
    private var levelMemberTreeListAdapter: LevelMemberTreeRecycleAdapter? = null

    companion object {
        fun newInstance(bundle: Bundle?) = LevelMemberTreeFragment().apply {
            arguments = bundle
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            with(levelMemberTreeViewModel) {
                isMemberTree = it.getBoolean(NSConstants.MEMBER_TREE_ENABLE)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLevelMemberTreeBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
        return levelMemberTreeBinding.root
    }

    private fun setListener() {
        with(levelMemberTreeBinding) {
            with(layoutHeader) {
                clBack.setOnClickListener {
                    onBackPress()
                }

				srlRefresh.setOnRefreshListener {
					levelMemberTreeViewModel.getMemberTreeData(false)
				}
            }
        }
    }

    private fun viewCreated() {
        with(levelMemberTreeBinding) {
            with(layoutHeader) {
                clBack.visibility = View.VISIBLE
                tvHeaderBack.text = requireActivity().resources.getString(R.string.level_member_tree)
                ivBack.visibility = View.VISIBLE
                ivSearch.visibility = View.GONE
                ivAddNew.visibility = View.GONE
            }
        }

        setMemberTreeAdapter()
        observeViewModel()
    }

    /**
     * To add data of member tree in list
     */
    private fun setMemberTreeAdapter() {
        with(levelMemberTreeBinding) {
            with(levelMemberTreeViewModel) {
                rvLevelMemberTreeList.layoutManager = LinearLayoutManager(activity)
                levelMemberTreeListAdapter = LevelMemberTreeRecycleAdapter(activity)
                rvLevelMemberTreeList.adapter = levelMemberTreeListAdapter
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
        with(levelMemberTreeViewModel) {
            memberTreeDataManage(isMemberTree)
            if (isMemberTree) {
                levelMemberTreeListAdapter!!.clearData()
                levelMemberTreeListAdapter!!.updateData(memberList)
            }
        }
    }

    /**
     * MemberTree data manage
     *
     * @param isMemberTreeVisible when memberTree available it's visible
     */
    private fun memberTreeDataManage(isMemberTreeVisible: Boolean) {
        with(levelMemberTreeBinding) {
            rvLevelMemberTreeList.visibility = if (isMemberTreeVisible) View.VISIBLE else View.GONE
            clLevelMemberNotFound.visibility = if (isMemberTreeVisible) View.GONE else View.VISIBLE
        }
    }

    /**
     * To observe the view model for data changes
     */
    private fun observeViewModel() {
        with(levelMemberTreeViewModel) {
            with(levelMemberTreeBinding) {
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
