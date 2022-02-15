package com.moneytree.app.ui.main

import android.os.Bundle
import com.moneytree.app.R
import com.moneytree.app.common.*
import com.moneytree.app.databinding.NsActivityMainBinding
import com.moneytree.app.ui.home.NSHomeFragment
import com.moneytree.app.ui.offers.NSOfferFragment
import com.moneytree.app.ui.profile.NSProfileFragment
import com.moneytree.app.ui.vouchers.NSVoucherFragment
import io.github.g00fy2.quickie.QRResult
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class NSMainActivity : NSActivity() {
    private lateinit var mainBinding: NsActivityMainBinding
   private val pref = NSApplication.getInstance().getPrefs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = NsActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        viewCreated()
        setListener()
    }

    /**
     * View created
     */
    private fun viewCreated() {
        with(mainBinding) {
            replaceCurrentFragment(NSHomeFragment.newInstance(), false, mainContainer.id)
        }
    }

    /**
     * Set listener
     */
    private fun setListener() {
        with(mainBinding) {
            navigationMenu.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.tb_home -> {
                        replaceCurrentFragment(NSHomeFragment.newInstance(), false, mainContainer.id)
                    }
                    R.id.tb_register -> {
                        //replaceCurrentFragment(NSRegisterListFragment.newInstance(), false, mainContainer.id)
                    }
                    R.id.tb_vouchers -> {
                        replaceCurrentFragment(NSVoucherFragment.newInstance(), false, mainContainer.id)
                    }
                    R.id.tb_offers -> {
                        pref.offerTabPosition = 0
                        replaceCurrentFragment(NSOfferFragment.newInstance(), false, mainContainer.id)
                    }
                    R.id.tb_profile -> {
                        replaceCurrentFragment(NSProfileFragment.newInstance(), false, mainContainer.id)
                    }
                }
                true
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFragmentEvent(event: NSFragmentChange) {
        with(mainBinding) {
            replaceCurrentFragment(event.fragment, true, mainContainer.id)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTabChange(event: NSTabChange) {
        with(mainBinding) {
            navigationMenu.selectedItemId = event.tab
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBackPress(event: BackPressEvent) {
       with(mainBinding) {
           navigationMenu.selectedItemId = R.id.tb_home
       }
    }
}