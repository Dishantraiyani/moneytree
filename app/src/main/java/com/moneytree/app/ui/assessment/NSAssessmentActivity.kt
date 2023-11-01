package com.moneytree.app.ui.assessment

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityCommonBinding

class NSAssessmentActivity : NSActivity() {
    private lateinit var binding: ActivityCommonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommonBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadInitialFragment()
    }

    /**
     * To initialize assessment fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(NSAssessmentFragment.newInstance(), false, binding.commonContainer.id)
    }
}
