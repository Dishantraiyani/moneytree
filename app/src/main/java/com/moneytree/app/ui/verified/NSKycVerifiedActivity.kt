package com.moneytree.app.ui.verified

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.NsActivityKycVerifiedBinding

class NSKycVerifiedActivity : NSActivity() {
    private lateinit var binding: NsActivityKycVerifiedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NsActivityKycVerifiedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadInitialFragment()
    }

    /**
     * To initialize verified fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(NSKycVerifiedFragment.newInstance(), false, binding.vouchersContainer.id)
    }
}