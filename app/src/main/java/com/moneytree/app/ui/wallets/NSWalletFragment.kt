package com.moneytree.app.ui.wallets

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.moneytree.app.R
import com.moneytree.app.common.*
import com.moneytree.app.common.NSRequestCodes.REQUEST_WALLET_UPDATE
import com.moneytree.app.common.NSRequestCodes.REQUEST_WALLET_UPDATE_TRANSFER
import com.moneytree.app.common.utils.addText
import com.moneytree.app.common.utils.switchActivity
import com.moneytree.app.common.utils.switchResultActivity
import com.moneytree.app.databinding.FragmentMainBinding
import com.moneytree.app.databinding.NsFragmentWalletBinding
import com.moneytree.app.ui.vouchers.NSVouchersViewModel
import com.moneytree.app.ui.wallets.redeemForm.NSAddRedeemActivity
import com.moneytree.app.ui.wallets.transfer.NSTransferActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class NSWalletFragment : NSFragment() {
    private val walletModel: NSWalletsViewModel by lazy {
        ViewModelProvider(this).get(NSWalletsViewModel::class.java)
    }
    private var _binding: NsFragmentWalletBinding? = null
    private val mainBinding get() = _binding!!
	private var amountAvailable = "0"

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
                    ivSearch.visibility = View.VISIBLE
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
                        switchActivity(NSTransferActivity::class.java, bundleOf(NSConstants.KEY_IS_VOUCHER_FROM_TRANSFER to false))
                    }

                    ivSearch.setOnClickListener {
                        cardSearch.visibility = View.VISIBLE
                    }

                    ivClose.setOnClickListener {
                        cardSearch.visibility = View.GONE
                        etSearch.setText("")
                        hideKeyboard(cardSearch)
                        EventBus.getDefault().post(SearchCloseEvent(tabPosition))
                    }

                    etSearch.setOnKeyListener(object: View.OnKeyListener{
                        override fun onKey(p0: View?, keyCode: Int, event: KeyEvent): Boolean {
                            if (event.action == KeyEvent.ACTION_DOWN) {
                                when (keyCode) {
                                    KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                                        val strSearch = etSearch.text.toString()
                                        if (strSearch.isNotEmpty()) {
                                            hideKeyboard(cardSearch)
                                            EventBus.getDefault().post(SearchStringEvent(strSearch, tabPosition))
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
        fun newInstance() = NSWalletFragment()
    }
}
