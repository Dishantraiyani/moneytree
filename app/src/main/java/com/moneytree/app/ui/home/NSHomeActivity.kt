package com.moneytree.app.ui.home

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.NsActivityHomeBinding

class NSHomeActivity : NSActivity() {
    private lateinit var homeBinding: NsActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeBinding = NsActivityHomeBinding.inflate(layoutInflater)
        setContentView(homeBinding.root)
        loadInitialFragment()
    }

    /**
     * To initialize home fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(NSHomeFragment.newInstance(), false, homeBinding.homeContainer.id)
    }
}