package com.moneytree.app.ui.recharge

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityCommonBinding

class NSRechargeActivity : NSActivity() {
    private lateinit var binding: ActivityCommonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommonBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadInitialFragment(intent.extras!!)
    }

    /**
     * To initialize recharge fragment
     *
     */
    private fun loadInitialFragment(bundle: Bundle) {
        replaceCurrentFragment(NSRechargeFragment.newInstance(bundle), false, binding.commonContainer.id)
    }
}
