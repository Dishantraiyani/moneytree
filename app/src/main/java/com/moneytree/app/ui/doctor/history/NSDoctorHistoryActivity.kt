package com.moneytree.app.ui.doctor.history

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityDoctorBinding

class NSDoctorHistoryActivity : NSActivity() {
    private lateinit var binding: ActivityDoctorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadInitialFragment()
    }

    /**
     * To initialize doctor fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(NSDoctorHistoryFragment.newInstance(), false, binding.doctorContainer.id)
    }
}
