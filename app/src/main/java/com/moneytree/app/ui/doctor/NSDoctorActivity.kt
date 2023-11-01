package com.moneytree.app.ui.doctor

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityCommonBinding

class NSDoctorActivity : NSActivity() {
    private lateinit var binding: ActivityCommonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommonBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadInitialFragment()
    }

    /**
     * To initialize doctor fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(NSDoctorFragment.newInstance(), false, binding.commonContainer.id)
    }
}
