package com.moneytree.app.ui.vouchers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.moneytree.app.R
import com.moneytree.app.common.*
import com.moneytree.app.common.callbacks.NSSearchCallback
import com.moneytree.app.common.utils.switchActivity
import com.moneytree.app.databinding.NsFragmentMainVouchersBinding
import com.moneytree.app.ui.wallets.transfer.NSTransferActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class NSMainVoucherFragment : NSFragment(), NSSearchCallback {
    private val voucherListModel: NSVouchersViewModel by lazy {
        ViewModelProvider(this).get(NSVouchersViewModel::class.java)
    }
    private var _binding: NsFragmentMainVouchersBinding? = null

    private val voucherBinding get() = _binding!!
    companion object {
        fun newInstance() = NSMainVoucherFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentMainVouchersBinding.inflate(inflater, container, false)
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
                HeaderUtils(layoutHeader, requireActivity(), clBackView = true, headerTitle = resources.getString(R.string.vouchers), isSearch = true, searchCallback = this@NSMainVoucherFragment)
                setMainFragmentData(activity)
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
                    ivClose.setOnClickListener {
                        cardSearch.visibility = View.GONE
                        etSearch.setText("")
                        hideKeyboard(cardSearch)
                        EventBus.getDefault().post(MainSearchCloseEvent())
                    }

					voucherBinding.btnSubmit.setOnClickListener ( object : OnSingleClickListener() {
						override fun onSingleClick(v: View?) {
							switchActivity(NSTransferActivity::class.java, bundleOf(NSConstants.KEY_IS_VOUCHER_FROM_TRANSFER to true))
						}
					})

						//switchActivity(NSPackageListActivity::class.java)
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
                    adapter.setFragment(mMainFragmentList)
                    viewPager.adapter = adapter
                    TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                        tab.text = mMainFragmentTitleList[position]
                    }.attach()
                    viewPager.isUserInputEnabled = false
                    viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                        override fun onPageSelected(position: Int) {
                            super.onPageSelected(position)
                            when (position) {
                                0 -> {
                                    if (!isJoiningAdded) {
                                        EventBus.getDefault().post(
                                            NSJoiningVoucherEventTab()
                                        )
                                        isJoiningAdded = true
                                    }
                                }
                                1 -> {
                                    if (!isProductAdded) {
                                        EventBus.getDefault().post(
                                            NSProductVoucherEventTab()
                                        )
                                        isProductAdded = true
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
    fun clearSearchEvent(event: NSSearchClearEvent) {
        with(voucherBinding) {
            layoutHeader.etSearch.setText("")
        }
    }

    override fun onSearch(search: String) {
        EventBus.getDefault().post(MainSearchStringEvent(search))
    }
}
