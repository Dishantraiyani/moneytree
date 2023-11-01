package com.moneytree.app.ui.mycart.address

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityCommonBinding

class NSAddressActivity : NSActivity() {
    private lateinit var productsBinding: ActivityCommonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productsBinding = ActivityCommonBinding.inflate(layoutInflater)
        setContentView(productsBinding.root)
        loadInitialFragment(intent.extras)
    }

    /**
     * To initialize product fragment
     *
     */
    private fun loadInitialFragment(bundle: Bundle?) {
        replaceCurrentFragment(NSAddressFragment.newInstance(bundle), false, productsBinding.commonContainer.id)
    }
}
