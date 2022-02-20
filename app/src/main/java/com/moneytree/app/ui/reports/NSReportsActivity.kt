package com.moneytree.app.ui.reports

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.NsActivityReportsBinding

class NSReportsActivity : NSActivity() {
    private lateinit var reportsBinding: NsActivityReportsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reportsBinding = NsActivityReportsBinding.inflate(layoutInflater)
        setContentView(reportsBinding.root)
        loadInitialFragment()
    }

    /**
     * To initialize home fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(NSReportsFragment.newInstance(), false, reportsBinding.reportsContainer.id)
    }
}