package com.moneytree.app.ui.wallets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.moneytree.app.R
import com.moneytree.app.common.*
import com.moneytree.app.databinding.FragmentMainBinding
import com.moneytree.app.databinding.NsFragmentWalletBinding
import com.moneytree.app.ui.vouchers.NSVouchersViewModel
import org.greenrobot.eventbus.EventBus

class NSWalletFragment : Fragment() {
    private val walletModel: NSWalletsViewModel by lazy {
        ViewModelProvider(this).get(NSWalletsViewModel::class.java)
    }
    private var _binding: NsFragmentWalletBinding? = null
    private val mainBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentWalletBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
        return mainBinding.root
    }

    /**
     * View created
     *
     */
    private fun viewCreated() {
        with(mainBinding) {
            with(walletModel) {
                with(layoutHeader) {
                    tvHeaderBack.text = resources.getString(R.string.wallet)
                    clBack.visibility = View.VISIBLE
                }
                tvTransfer.visibility = View.VISIBLE
                tvRedeem.visibility = View.GONE
                setFragmentData(requireActivity())
                setupViewPager(walletContainer)
            }
        }
    }

    /**
     * Set listener
     *
     */
    private fun setListener() {
        with(mainBinding) {
            with(layoutHeader) {
                clBack.setOnClickListener {
                    EventBus.getDefault().post(BackPressEvent())
                }
            }
        }
    }

    // Add Fragments to Tabs
    private fun setupViewPager(viewPager: ViewPager2) {
        with(mainBinding) {
            with(walletModel) {
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
                                    tvTransfer.visibility = View.VISIBLE
                                    tvRedeem.visibility = View.GONE
                                    EventBus.getDefault().post(
                                        NSPendingEventTab()
                                    )
                                }
                                1 -> {
                                    tvTransfer.visibility = View.GONE
                                    tvRedeem.visibility = View.VISIBLE
                                    EventBus.getDefault().post(
                                        NSReceiveEventTab()
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

    companion object {
        fun newInstance() = NSWalletFragment()
    }
}