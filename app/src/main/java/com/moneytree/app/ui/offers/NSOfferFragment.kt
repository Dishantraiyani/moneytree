package com.moneytree.app.ui.offers

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
import org.greenrobot.eventbus.EventBus

class NSOfferFragment : NSFragment() {
    var tabPosition = 0
    private var _binding: NsFragmentOffersBinding? = null
    private val mFragmentTitleList: MutableList<String> = ArrayList()
    val mFragmentList: MutableList<Fragment> = ArrayList()

    private val offerBinding get() = _binding!!
    private val pref = NSApplication.getInstance().getPrefs()
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
            with(layoutHeader) {
                clBack.visibility = View.VISIBLE
                tvHeaderBack.text = activity.resources.getString(R.string.offers)
                ivBack.visibility = View.VISIBLE
                ivSearch.visibility = View.VISIBLE
            }

            setFragmentData()
        }
    }

    private fun setFragmentData() {
        with(activity.resources) {
            mFragmentTitleList.clear()
            mFragmentTitleList.add(getString(R.string.repurchase))
            mFragmentTitleList.add(getString(R.string.retail_info))
            mFragmentTitleList.add(getString(R.string.royalty_offer))
            mFragmentTitleList.add(getString(R.string.downline_member))
        }
        mFragmentList.clear()
        mFragmentList.add(NSRePurchaseListFragment())
        mFragmentList.add(NSRetailListFragment())
        mFragmentList.add(NSRoyaltyListFragment())
        mFragmentList.add(NSDownlineReOfferFragment())
        setupViewPager(offerBinding.offerFrameContainer)
    }

    // Add Fragments to Tabs
    private fun setupViewPager(viewPager: ViewPager2) {
        with(offerBinding) {
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
                                EventBus.getDefault().post(NSRepurchaseEventTab())
                            }
                            1 -> {
                                EventBus.getDefault().post(NSRetailInfoEventTab())
                            }
                            2 -> {
                                EventBus.getDefault().post(NSRoyaltyEventTab())
                            }
                            3 -> {
                                EventBus.getDefault().post(NSDownlineEventTab())
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

    /**
     * Set listener
     */
    private fun setListener() {
        with(offerBinding) {

            with(layoutHeader) {
                clBack.setOnClickListener {
                    EventBus.getDefault().post(BackPressEvent())
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
                /*tabLayout.getTabAt(pref.offerTabPosition!!)!!.select()
                tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        tabPosition = tab!!.position
                        pref.offerTabPosition = tabPosition
                        ivSearch.visibility = View.VISIBLE
                        when (tabPosition) {
                            0 -> {
                                replaceFragment(NSRePurchaseListFragment.newInstance(), false, offerFrameContainer.id)
                            }
                            1 -> {
                                replaceFragment(NSRetailListFragment.newInstance(), false, offerFrameContainer.id)
                            }
                            2 -> {
                                replaceFragment(NSRoyaltyListFragment.newInstance(), false, offerFrameContainer.id)
                            }
                            3 -> {
                                ivSearch.visibility = View.GONE
                                replaceFragment(
                                    NSDownlineReOfferFragment.newInstance(),
                                    false,
                                    offerFrameContainer.id
                                )
                            }
                        }
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {

                    }

                    override fun onTabReselected(tab: TabLayout.Tab?) {

                    }

                })*/

            }
        }
    }

    private fun addTabs() {
        with(offerBinding) {
            val tabList = activity.resources.getStringArray(R.array.offers_tab)
            for (tab in tabList) {
                tabLayout.addTab(tabLayout.newTab().setText(tab))
            }
        }
    }
}