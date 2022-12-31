package com.moneytree.app.ui.notification

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.NsActivityHomeBinding
import com.moneytree.app.databinding.NsActivityNotificationBinding
import com.moneytree.app.ui.home.NSHomeFragment

class NSNotificationActivity : NSActivity() {
    private lateinit var homeBinding: NsActivityNotificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeBinding = NsActivityNotificationBinding.inflate(layoutInflater)
        setContentView(homeBinding.root)
        loadInitialFragment()
    }

    /**
     * To initialize home fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(NSNotificationFragment.newInstance(), false, homeBinding.notificationContainer.id)
    }
}
