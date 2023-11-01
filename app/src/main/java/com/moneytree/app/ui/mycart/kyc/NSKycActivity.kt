package com.moneytree.app.ui.mycart.kyc

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityCommonBinding

class NSKycActivity : NSActivity() {
    private lateinit var productsBinding: ActivityCommonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productsBinding = ActivityCommonBinding.inflate(layoutInflater)
        setContentView(productsBinding.root)
        loadInitialFragment()
    }

    /**
     * To initialize product fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(NSKycFragment.newInstance(), false, productsBinding.commonContainer.id)
    }
}
