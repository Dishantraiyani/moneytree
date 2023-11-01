package com.moneytree.app.ui.activate

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityCommonBinding

class NSActivateActivity : NSActivity() {
    private lateinit var binding: ActivityCommonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommonBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadInitialFragment()
    }

    /**
     * To initialize product category fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(NSActivationFragment.newInstance(), false, binding.commonContainer.id)
    }
}
