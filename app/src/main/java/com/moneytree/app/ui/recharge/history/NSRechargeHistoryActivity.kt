package com.moneytree.app.ui.recharge.history

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityRechargeBinding
import com.moneytree.app.databinding.ActivityRechargeHistoryBinding
import com.moneytree.app.ui.recharge.NSRechargeFragment

class NSRechargeHistoryActivity : NSActivity() {
    private lateinit var rechargeBinding: ActivityRechargeHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rechargeBinding = ActivityRechargeHistoryBinding.inflate(layoutInflater)
        setContentView(rechargeBinding.root)
        loadInitialFragment(intent.extras)
    }

    /**
     * To initialize recharge fragment
     *
     */
    private fun loadInitialFragment(bundle: Bundle?) {
        replaceCurrentFragment(NSRechargeHistoryFragment.newInstance(bundle), false, rechargeBinding.rechargeHistoryContainer.id)
    }
}
