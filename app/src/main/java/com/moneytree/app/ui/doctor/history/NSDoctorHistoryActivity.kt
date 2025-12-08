package com.moneytree.app.ui.doctor.history

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityCommonBinding

class NSDoctorHistoryActivity : NSActivity() {
    private lateinit var binding: ActivityCommonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCommonBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView(binding.root)
        loadInitialFragment()
    }

    /**
     * To initialize doctor fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(NSDoctorHistoryFragment.newInstance(), false, binding.commonContainer.id)
    }
}
