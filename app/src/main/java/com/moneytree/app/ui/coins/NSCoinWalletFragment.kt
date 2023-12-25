package com.moneytree.app.ui.coins

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.moneytree.app.R
import com.moneytree.app.common.BackPressEvent
import com.moneytree.app.common.HeaderUtils
import com.moneytree.app.common.NSActivityEvent
import com.moneytree.app.common.NSConstants
import com.moneytree.app.common.NSFragment
import com.moneytree.app.common.NSRedeemWalletUpdateEvent
import com.moneytree.app.common.NSRedeemWalletUpdateTransferEvent
import com.moneytree.app.common.NSRedemptionEventTab
import com.moneytree.app.common.NSRequestCodes.REQUEST_WALLET_UPDATE
import com.moneytree.app.common.NSRequestCodes.REQUEST_WALLET_UPDATE_TRANSFER
import com.moneytree.app.common.NSTransactionsEventTab
import com.moneytree.app.common.NSWalletAmount
import com.moneytree.app.common.ViewPagerMDAdapter
import com.moneytree.app.common.callbacks.NSHeaderMainSearchCallback
import com.moneytree.app.common.callbacks.NSHeaderSearchCallback
import com.moneytree.app.common.callbacks.NSSearchCallback
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.switchActivity
import com.moneytree.app.common.utils.switchResultActivity
import com.moneytree.app.databinding.NsFragmentCoinWalletBinding
import com.moneytree.app.ui.coins.redeemForm.NSAddRedeemActivity
import com.moneytree.app.ui.coins.redeemHistory.NSPendingFragment
import com.moneytree.app.ui.coins.transaction.NSCoinTransactionFragment
import com.moneytree.app.ui.coins.transfer.NSTransferActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class NSCoinWalletFragment : NSFragment(), NSHeaderMainSearchCallback, NSSearchCallback {
    private val walletModel: NSCoinWalletsViewModel by lazy {
        ViewModelProvider(this)[NSCoinWalletsViewModel::class.java]
    }
    private var _binding: NsFragmentCoinWalletBinding? = null
    private val mainBinding get() = _binding!!
	private var amountAvailable = "0"
    private var searchCallback: NSHeaderSearchCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentCoinWalletBinding.inflate(inflater, container, false)
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
                NSConstants.tabName = this@NSCoinWalletFragment.javaClass
                HeaderUtils(layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString(R.string.coins), isSearch = true, searchCallback = this@NSCoinWalletFragment)
                tvTransfer.visibility = View.VISIBLE
                tvRedeem.visibility = View.GONE
                setFragmentData(requireActivity())
                setupViewPager(walletContainer)
            }
        }
    }

    private fun setFragmentData(activity: FragmentActivity) {
        walletModel.apply {
            with(activity.resources) {
                mFragmentTitleList.clear()
                mFragmentTitleList.add(getString(R.string.credit_debit_title))
                mFragmentTitleList.add(getString(R.string.pending))
            }
            mFragmentList.clear()
            mFragmentList.add(NSCoinTransactionFragment.newInstance())
            mFragmentList.add(NSPendingFragment.newInstance())
        }
    }

    /**
     * Set listener
     *
     */
    private fun setListener() {
        with(mainBinding) {
            with(walletModel) {
                with(layoutHeader) {
                    clBack.setOnClickListener {
                        EventBus.getDefault().post(BackPressEvent())
                    }

                    tvRedeem.setOnClickListener {
						switchResultActivity(
							dataResult, NSAddRedeemActivity::class.java,
							bundleOf(
								NSConstants.KEY_AVAILABLE_BALANCE to amountAvailable
							)
						)
                    }

                    tvTransfer.setOnClickListener {
                        switchActivity(NSTransferActivity::class.java, bundleOf(NSConstants.KEY_IS_VOUCHER_FROM_TRANSFER to false, NSConstants.KEY_AVAILABLE_BALANCE to amountAvailable))
                    }

                    ivClose.setOnClickListener {
                        cardSearch.visibility = View.GONE
                        etSearch.setText("")
                        hideKeyboard(cardSearch)
                        searchCallback?.onHeader("", tabPosition, true)
                    }
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
					viewPager.isUserInputEnabled = false
                    TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                        tab.text = mFragmentTitleList[position]
                    }.attach()
                    viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                        override fun onPageSelected(position: Int) {
                            super.onPageSelected(position)
                            tabPosition = position
                            when (position) {
                                0 -> {
                                    tvTransfer.visibility = View.VISIBLE
                                    tvRedeem.visibility = View.GONE
                                    if (!isTransactionAdded) {
                                        EventBus.getDefault().post(
                                            NSTransactionsEventTab()
                                        )
                                        isTransactionAdded = true
                                    }
                                    (mFragmentList[position] as NSCoinTransactionFragment).loadFragment(this@NSCoinWalletFragment)

                                }
                                1 -> {
                                    tvTransfer.visibility = View.GONE
                                    tvRedeem.visibility = View.VISIBLE
                                    if (!isRedemptionAdded) {
                                        EventBus.getDefault().post(
                                            NSRedemptionEventTab()
                                        )
                                        isRedemptionAdded = true
                                    }
                                    (mFragmentList[position] as NSPendingFragment).loadFragment(this@NSCoinWalletFragment)
                                }
                            }
                        }
                    })
                    viewPager.offscreenPageLimit = 2
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun walletAmount(event: NSWalletAmount) {
        with(mainBinding) {
			amountAvailable = event.amount
            tvTotalBalance.text = addText(requireActivity(), R.string.balance, event.amount)
        }
    }

	@Subscribe(threadMode = ThreadMode.MAIN)
	fun onResultEvent(event: NSActivityEvent) {
		if (event.resultCode == REQUEST_WALLET_UPDATE) {
			EventBus.getDefault().post(NSRedeemWalletUpdateEvent())
		} else if (event.resultCode == REQUEST_WALLET_UPDATE_TRANSFER) {
            EventBus.getDefault().post(NSRedeemWalletUpdateTransferEvent())
        }
        with(mainBinding.layoutHeader) {
            cardSearch.visibility = View.GONE
            etSearch.setText("")
            hideKeyboard(cardSearch)
        }
	}

    companion object {
        fun newInstance() = NSCoinWalletFragment()
    }

    override fun onHeader(callback: NSHeaderSearchCallback) {
        searchCallback = callback
    }

    override fun onSearch(search: String) {
        with(walletModel) {
            searchCallback?.onHeader(search, tabPosition, false)
        }
    }
}
