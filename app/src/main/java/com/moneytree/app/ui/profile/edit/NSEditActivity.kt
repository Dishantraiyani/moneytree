package com.moneytree.app.ui.profile.edit

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityCommonBinding

class NSEditActivity : NSActivity() {
    private lateinit var binding: ActivityCommonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommonBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadInitialFragment()
    }

    /**
     * To initialize transfer fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(NSEditFragment.newInstance(), false, binding.commonContainer.id)
    }
}