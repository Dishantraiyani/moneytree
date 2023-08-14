package com.moneytree.app.ui.vouchers.product

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.moneytree.app.common.*
import com.moneytree.app.common.utils.TAG
import com.moneytree.app.databinding.NsFragmentProductVouchersBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class NSProductVoucherFragment : NSFragment() {
    private val voucherListModel: NSProductVouchersViewModel by lazy {
        ViewModelProvider(this).get(NSProductVouchersViewModel::class.java)
    }
    private var _binding: NsFragmentProductVouchersBinding? = null

    private val voucherBinding get() = _binding!!
    companion object {
        fun newInstance() = NSProductVoucherFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentProductVouchersBinding.inflate(inflater, container, false)
        return voucherBinding.root
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTabSelectEvent(event: NSProductVoucherEventTab) {
        Log.d(TAG, "onTabSelectEvent: $event")
        viewCreated()
        setListener()
    }

    /**
     * View created
     */
    private fun viewCreated() {
        with(voucherBinding) {
            with(voucherListModel) {
                setProductFragmentData(activity)
                setupViewPager(voucherContainer)
            }
        }
    }

    /**
     * Set listener
     */
    private fun setListener() {

    }

    // Add Fragments to Tabs
    private fun setupViewPager(viewPager: ViewPager2) {
        with(voucherBinding) {
            with(voucherListModel) {
                try {
                    val adapter = ViewPagerMDAdapter(requireActivity())
                    adapter.setFragment(mProductFragmentList)
                    viewPager.adapter = adapter
                    TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                        tab.text = mProductFragmentTitleList[position]
                    }.attach()
                    viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    })
                    viewPager.offscreenPageLimit = 3
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}