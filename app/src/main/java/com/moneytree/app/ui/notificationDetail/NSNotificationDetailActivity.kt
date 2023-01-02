package com.moneytree.app.ui.notificationDetail

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.NsActivityNotificationDetailBinding

class NSNotificationDetailActivity : NSActivity() {
    private lateinit var binding: NsActivityNotificationDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NsActivityNotificationDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadInitialFragment(intent.extras)
    }

    /**
     * To initialize product fragment
     *
     */
    private fun loadInitialFragment(bundle: Bundle?) {
        replaceCurrentFragment(NSNotificationDetailFragment.newInstance(bundle), false, binding.notificationDetailContainer.id)
    }
}
