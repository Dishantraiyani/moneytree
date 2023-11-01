package com.moneytree.app.ui.doctor.historyDetail

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityCommonBinding

class NSDoctorHistoryDetailActivity : NSActivity() {
    private lateinit var binding: ActivityCommonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommonBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadInitialFragment(intent.extras)
    }

    /**
     * To initialize doctor fragment
     *
     */
    private fun loadInitialFragment(bundle: Bundle?) {
        replaceCurrentFragment(NSDoctorHistoryDetailFragment.newInstance(bundle), false, binding.commonContainer.id)
    }
}
