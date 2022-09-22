package com.moneytree.app.ui.levelMemberDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.moneytree.app.R
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSFragment
import com.moneytree.app.databinding.FragmentLevelMemberTreeDetailBinding

class LevelMemberTreeDetailFragment : NSFragment() {
    private val levelMemberTreeViewModel: LevelMemberTreeDetailViewModel by lazy {
        ViewModelProvider(this)[LevelMemberTreeDetailViewModel::class.java]
    }
    private var _binding: FragmentLevelMemberTreeDetailBinding? = null
    private val levelMemberTreeBinding get() = _binding!!
    private var levelMemberTreeListAdapter: LevelMemberTreeDetailRecycleAdapter? = null

    companion object {
        fun newInstance(bundle: Bundle?) = LevelMemberTreeDetailFragment().apply {
            arguments = bundle
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            with(levelMemberTreeViewModel) {
                levelNumber = it.getString(NSConstants.KEY_MEMBER_LEVEL_NUMBER)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLevelMemberTreeDetailBinding.inflate(inflater, container, false)
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
                levelMemberTreeListAdapter = LevelMemberTreeDetailRecycleAdapter(activity)
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
