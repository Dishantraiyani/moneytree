package com.moneytree.app.ui.main

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.NsActivityMainBinding

class NSMainActivity : NSActivity() {
    private lateinit var mainBinding: NsActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = NsActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        loadInitialFragment()
    }

    /**
     * To initialize home fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(MainFragment.newInstance(), false, mainBinding.mainContainer.id)
    }
}