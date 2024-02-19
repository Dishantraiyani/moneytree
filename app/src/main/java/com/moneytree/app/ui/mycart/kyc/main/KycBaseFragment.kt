package com.moneytree.app.ui.mycart.kyc.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.moneytree.app.R
import com.moneytree.app.base.fragment.BaseViewModelFragment
import com.moneytree.app.common.HeaderUtils
import com.moneytree.app.common.ViewPagerMDAdapter
import com.moneytree.app.databinding.FragmentKycBaseBinding

class KycBaseFragment : BaseViewModelFragment<KycBaseViewModel, FragmentKycBaseBinding>() {

    override val viewModel: KycBaseViewModel by lazy {
        ViewModelProvider(this)[KycBaseViewModel::class.java]
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentKycBaseBinding {
        return FragmentKycBaseBinding.inflate(inflater, container, false)
    }

    override fun setupViews() {
        super.setupViews()
        HeaderUtils(binding.layoutHeader, requireActivity(), clBackView = true, headerTitle = activity.resources.getString(
            R.string.kyc_detail))
        baseObserveViewModel(viewModel)
        viewModel.setFragments(requireActivity())
        setupViewPager(binding.pagerContainer)
    }

    companion object {
        fun newInstance() = KycBaseFragment()
    }

    private fun setupViewPager(viewPager: ViewPager2) {
        with(binding) {
            with(viewModel) {
                try {
                    val adapter = ViewPagerMDAdapter(requireActivity())
                    adapter.setFragment(fragmentList)
                    viewPager.adapter = adapter
                    TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                        tab.text = titleList[position]
                    }.attach()
                    viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    })
                    viewPager.offscreenPageLimit = 5
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
