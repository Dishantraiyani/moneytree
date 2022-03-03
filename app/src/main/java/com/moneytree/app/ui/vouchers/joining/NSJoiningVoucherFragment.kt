package com.moneytree.app.ui.vouchers.joining

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
import com.moneytree.app.databinding.NsFragmentJoiningVouchersBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class NSJoiningVoucherFragment : NSFragment() {
    private val voucherListModel: NSJoiningVouchersViewModel by lazy {
        ViewModelProvider(this).get(NSJoiningVouchersViewModel::class.java)
    }
    private var _binding: NsFragmentJoiningVouchersBinding? = null

    private val voucherBinding get() = _binding!!
    companion object {
        fun newInstance() = NSJoiningVoucherFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentJoiningVouchersBinding.inflate(inflater, container, false)
        return voucherBinding.root
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTabSelectEvent(event: NSJoiningVoucherEventTab) {
        Log.d(TAG, "onTabSelectEvent: $event")
        viewCreated()
        setListener()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun mainSearchCallEvent(event: MainSearchStringEvent) {
        with(voucherListModel) {
            EventBus.getDefault().post(SearchStringEvent(event.search, mainTabPosition))
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun mainSearchCloseEvent(event: MainSearchCloseEvent) {
        with(voucherListModel) {
            EventBus.getDefault().post(SearchCloseEvent(mainTabPosition))
        }
    }

    /**
     * View created
     */
    private fun viewCreated() {
        with(voucherBinding) {
            with(voucherListModel) {
                setFragmentData(activity)
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
                    adapter.setFragment(mFragmentList)
                    viewPager.adapter = adapter
                    TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                        tab.text = mFragmentTitleList[position]
                    }.attach()
                    viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                        override fun onPageSelected(position: Int) {
                            super.onPageSelected(position)
                            mainTabPosition = position
                            when (position) {
                                0 -> {
                                    if (!isPendingAdded) {
                                        EventBus.getDefault().post(
                                            NSPendingEventTab(isPendingAdded)
                                        )
                                        isPendingAdded = true
                                    }
                                    EventBus.getDefault().post(NSSearchClearEvent())
                                }
                                1 -> {
                                    if (!isReceiveAdded) {
                                        EventBus.getDefault().post(
                                            NSReceiveEventTab(isReceiveAdded)
                                        )
                                        isReceiveAdded = true
                                    }
                                    EventBus.getDefault().post(NSSearchClearEvent())
                                }
                                2 -> {
                                    if (!isTransferAdded) {
                                        EventBus.getDefault().post(NSTransferEventTab(isTransferAdded))
                                        isTransferAdded = true
                                    }
                                    EventBus.getDefault().post(NSSearchClearEvent())
                                }
                            }
                        }
                    })
                    viewPager.offscreenPageLimit = 3
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}