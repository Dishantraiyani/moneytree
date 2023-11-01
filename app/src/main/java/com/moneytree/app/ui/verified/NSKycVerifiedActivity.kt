package com.moneytree.app.ui.verified

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityCommonBinding

class NSKycVerifiedActivity : NSActivity() {
    private lateinit var binding: ActivityCommonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommonBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadInitialFragment()
    }

    /**
     * To initialize verified fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(NSKycVerifiedFragment.newInstance(), false, binding.commonContainer.id)
    }
}