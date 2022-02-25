package com.moneytree.app.ui.recharge

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityRechargeBinding

class NSRechargeActivity : NSActivity() {
    private lateinit var rechargeBinding: ActivityRechargeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rechargeBinding = ActivityRechargeBinding.inflate(layoutInflater)
        setContentView(rechargeBinding.root)
        loadInitialFragment()
    }

    /**
     * To initialize recharge fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(NSRechargeFragment.newInstance(), false, rechargeBinding.rechargeContainer.id)
    }
}