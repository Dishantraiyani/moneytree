package com.moneytree.app.ui.offers

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.moneytree.app.R
import com.moneytree.app.common.*
import com.moneytree.app.databinding.NsFragmentOffersBinding
import com.moneytree.app.ui.downlineReOffer.NSDownlineReOfferFragment
import com.moneytree.app.ui.repurchase.NSRePurchaseListFragment
import com.moneytree.app.ui.retail.NSRetailListFragment
import com.moneytree.app.ui.royalty.NSRoyaltyListFragment
import com.moneytree.app.ui.vouchers.NSVouchersViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class NSOfferFragment : NSFragment() {
    private val offerModel: NSOffersViewModel by lazy {
		ViewModelProvider(this)[NSOffersViewModel::class.java]
    }
    private var _binding: NsFragmentOffersBinding? = null

    private val offerBinding get() = _binding!!
    companion object {
        fun newInstance() = NSOfferFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NsFragmentOffersBinding.inflate(inflater, container, false)
        viewCreated()
        setListener()
        return offerBinding.root
    }

    /**
     * View created
     */
    private fun viewCreated() {
        with(offerBinding) {
            with(offerModel) {
                with(layoutHeader) {
                    clBack.visibility = View.VISIBLE
                    tvHeaderBack.text = activity.resources.getString(R.string.offers)
                    ivBack.visibility = View.VISIBLE
                    ivSearch.visibility = View.VISIBLE
                }

                setFragmentData(activity)
                setupViewPager(offerBinding.offerFrameContainer)
            }
        }
    }

    // Add Fragments to Tabs
    private fun setupViewPager(viewPager: ViewPager2) {
        with(offerBinding) {
            with(offerModel) {
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
                            layoutHeader.etSearch.setText("")
							layoutHeader.ivSearch.visibility = View.VISIBLE
                            when (position) {
                                0 -> {
                                    if (!isRepurchaseAdded) {
                                        EventBus.getDefault().post(NSRepurchaseEventTab())
                                        isRepurchaseAdded = true
                                    }
                                }
                                1 -> {
                                    if (!isRetailAdded) {
                                        EventBus.getDefault().post(NSRetailInfoEventTab())
                                        isRetailAdded = true
                                    }
                                }
                                2 -> {
                                    if (!isRoyaltyAdded) {
                                        EventBus.getDefault().post(NSRoyaltyEventTab())
                                        isRoyaltyAdded = true
                                    }
                                }
                                3 -> {
                                    if (!isDownlineAdded) {
										layoutHeader.ivSearch.visibility = View.GONE
                                        EventBus.getDefault().post(NSDownlineEventTab())
                                        isDownlineAdded = true
                                    }
                                }
                            }
                        }
                    })
                    viewPager.offscreenPageLimit = 4
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(offerBinding) {
            with(offerModel) {
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
}
