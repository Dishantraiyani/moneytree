package com.moneytree.app.ui.wallets.transfer

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityTransferBinding

class NSTransferActivity : NSActivity() {
    private lateinit var transferBinding: ActivityTransferBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transferBinding = ActivityTransferBinding.inflate(layoutInflater)
        setContentView(transferBinding.root)
        loadInitialFragment()
    }

    /**
     * To initialize transfer fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(NSTransferFragment.newInstance(), false, transferBinding.transferContainer.id)
    }
}