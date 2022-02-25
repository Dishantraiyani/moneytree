package com.moneytree.app.ui.recharge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.moneytree.app.R
import com.moneytree.app.common.*
import com.moneytree.app.databinding.NsFragmentRechargeBinding
import com.moneytree.app.ui.downlineReOffer.NSDownlineReOfferFragment
import com.moneytree.app.ui.repurchase.NSRePurchaseListFragment
import com.moneytree.app.ui.retail.NSRetailListFragment
import com.moneytree.app.ui.royalty.NSRoyaltyListFragment
import org.greenrobot.eventbus.EventBus


class NSRechargeFragment : NSFragment() {
    private var _binding: NsFragmentRechargeBinding? = null
    private val rgBinding get() = _binding!!
    private val mFragmentTitleList: MutableList<String> = ArrayList()
    private val mFragmentList: MutableList<Fragment> = ArrayList()
    var fieldName: Array<String> = arrayOf()

    companion object {
        fun newInstance() = NSRechargeFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentRechargeBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
        return rgBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        with(rgBinding) {
            with(layoutHeader) {
                clBack.visibility = View.VISIBLE
                tvHeaderBack.text = activity.resources.getString(R.string.recharge)
                ivBack.visibility = View.VISIBLE
            }
            setFragmentData()
        }
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(rgBinding) {
            with(layoutHeader) {
                clBack.setOnClickListener {
                    onBackPress()
                }
            }
        }
    }

    private fun setFragmentData() {
        fieldName = resources.getStringArray(R.array.recharge_list)
        mFragmentTitleList.clear()
        for (strData in fieldName) {
            mFragmentTitleList.add(strData)
        }
        mFragmentList.clear()
        for (strData in fieldName) {
            mFragmentList.add(NSSubRechargeFragment())
        }
        setupViewPager(rgBinding.rechargeContainer)
    }

    // Add Fragments to Tabs
    private fun setupViewPager(viewPager: ViewPager2) {
        with(rgBinding) {
            try {
                val adapter = ViewPagerMDAdapter(requireActivity())
                adapter.setFragment(mFragmentList)
                viewPager.adapter = adapter
                TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                    tab.text = mFragmentTitleList[position]
                }.attach()
                viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        when (position) {
                            0 -> {
                                EventBus.getDefault().post(
                                    NSRepurchaseEventTab()
                                )
                            }
                            1 -> {
                                EventBus.getDefault().post(
                                    NSRetailInfoEventTab()
                                )
                            }
                            2 -> {
                                EventBus.getDefault().post(NSRoyaltyEventTab())
                            }
                            3 -> {
                                EventBus.getDefault().post(
                                    NSDownlineEventTab()
                                )
                            }
                        }
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}