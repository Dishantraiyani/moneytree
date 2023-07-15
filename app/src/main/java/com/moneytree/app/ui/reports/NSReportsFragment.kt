package com.moneytree.app.ui.reports

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.google.android.material.tabs.TabLayout
import com.moneytree.app.R
import com.moneytree.app.common.*
import com.moneytree.app.databinding.NsFragmentReportsBinding
import com.moneytree.app.ui.levelMember.LevelMemberTreeFragment
import com.moneytree.app.ui.memberTree.MemberTreeFragment

class NSReportsFragment : NSFragment() {
    private var _binding: NsFragmentReportsBinding? = null

    private val reportsBinding get() = _binding!!
    companion object {
        fun newInstance() = NSReportsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentReportsBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
        return reportsBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        with(reportsBinding) {
            HeaderUtils(layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString(R.string.reports))
            replaceFragment(
                MemberTreeFragment.newInstance(
                    bundleOf(
                        NSConstants.MEMBER_TREE_ENABLE to true
                    )
                ), false, reportsFrameContainer.id)
        }
        addTabs()
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(reportsBinding) {

            with(layoutHeader) {
                tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        when (tab!!.position) {
                            0 -> {
                                replaceFragment(
                                    MemberTreeFragment.newInstance(
                                        bundleOf(
                                            NSConstants.MEMBER_TREE_ENABLE to true
                                        )
                                    ), false, reportsFrameContainer.id)
                            }
                            1 -> {
                                replaceFragment(
                                    LevelMemberTreeFragment.newInstance(
                                        bundleOf(
                                            NSConstants.MEMBER_TREE_ENABLE to false
                                        )
                                    ), false, reportsFrameContainer.id)
                            }
                        }
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {

                    }

                    override fun onTabReselected(tab: TabLayout.Tab?) {

                    }

                })

            }
        }
    }

    private fun addTabs() {
        with(reportsBinding) {
            val tabList = activity.resources.getStringArray(R.array.reports)
            for (tab in tabList) {
                tabLayout.addTab(tabLayout.newTab().setText(tab))
            }
        }
    }
}