package com.moneytree.app.ui.assessment

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityAssessmentBinding

class NSAssessmentActivity : NSActivity() {
    private lateinit var binding: ActivityAssessmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAssessmentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadInitialFragment()
    }

    /**
     * To initialize assessment fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(NSAssessmentFragment.newInstance(), false, binding.assessmentContainer.id)
    }
}
