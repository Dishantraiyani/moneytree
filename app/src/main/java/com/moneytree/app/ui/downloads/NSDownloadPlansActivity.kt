package com.moneytree.app.ui.downloads

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.NsActivityDownloadPlansBinding

class NSDownloadPlansActivity : NSActivity() {
    private lateinit var activateBinding: NsActivityDownloadPlansBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activateBinding = NsActivityDownloadPlansBinding.inflate(layoutInflater)
        setContentView(activateBinding.root)
        loadInitialFragment(intent.extras)
    }

    /**
     * To initialize product category fragment
     *
     */
    private fun loadInitialFragment(bundle: Bundle?) {
        replaceCurrentFragment(NSDownloadPlanFragment.newInstance(), false, activateBinding.downloadPlanContainer.id)
    }
}
