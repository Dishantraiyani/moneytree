package com.moneytree.app.ui.vouchers

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.NsActivityVouchersBinding

class NSVouchersActivity : NSActivity() {
    private lateinit var reportsBinding: NsActivityVouchersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reportsBinding = NsActivityVouchersBinding.inflate(layoutInflater)
        setContentView(reportsBinding.root)
        loadInitialFragment()
    }

    /**
     * To initialize home fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(NSMainVoucherFragment.newInstance(), false, reportsBinding.vouchersContainer.id)
    }
}