package com.moneytree.app.ui.vouchers

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.moneytree.app.R
import com.moneytree.app.common.*
import com.moneytree.app.databinding.NsFragmentVouchersBinding
import org.greenrobot.eventbus.EventBus

class NSVoucherFragment : NSFragment() {
    private val voucherListModel: NSVouchersViewModel by lazy {
        ViewModelProvider(this).get(NSVouchersViewModel::class.java)
    }
    private var _binding: NsFragmentVouchersBinding? = null

    private val voucherBinding get() = _binding!!
    companion object {
        fun newInstance() = NSVoucherFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentVouchersBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
        return voucherBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        with(voucherBinding) {
            with(voucherListModel) {
                with(layoutHeader) {
                    clBack.visibility = View.VISIBLE
                    tvHeaderBack.text = activity.resources.getString(R.string.vouchers)
                    ivBack.visibility = View.VISIBLE
                    ivSearch.visibility = View.VISIBLE
                }
                setFragmentData(activity)
                setupViewPager(voucherContainer)
            }
        }
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(voucherBinding) {
            with(voucherListModel) {

                with(layoutHeader) {
                    clBack.setOnClickListener {
                        onBackPress()
                    }

                    ivSearch.setOnClickListener {
                        cardSearch.visibility = View.VISIBLE
                    }

                    ivClose.setOnClickListener {
                        cardSearch.visibility = View.GONE
                        etSearch.setText("")
                        hideKeyboard(cardSearch)
                        EventBus.getDefault().post(SearchCloseEvent())
                    }

                    etSearch.setOnKeyListener(object: View.OnKeyListener{
                        override fun onKey(p0: View?, keyCode: Int, event: KeyEvent): Boolean {
                            if (event.action == KeyEvent.ACTION_DOWN) {
                                when (keyCode) {
                                    KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                                        val strSearch = etSearch.text.toString()
                                        if (strSearch.isNotEmpty()) {
                                            hideKeyboard(cardSearch)
                                            EventBus.getDefault().post(SearchStringEvent(strSearch))
                                        }
                                        return true
                                    }
                                }
                            }
                            return false
                        }
                    })
                }
            }
        }
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
                            when (position) {
                                0 -> {
                                    EventBus.getDefault().post(
                                        NSPendingEventTab()
                                    )
                                }
                                1 -> {
                                    EventBus.getDefault().post(
                                        NSReceiveEventTab()
                                    )
                                }
                                2 -> {
                                    EventBus.getDefault().post(NSTransferEventTab())
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