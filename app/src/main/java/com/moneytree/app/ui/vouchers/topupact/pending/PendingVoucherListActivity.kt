package com.moneytree.app.ui.vouchers.topupact.pending

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityCommonBinding
import com.moneytree.app.ui.vouchers.topup.TopUpVoucherFragment
import com.moneytree.app.ui.vouchers.topup.list.TopupVoucherListFragment

class PendingVoucherListActivity : NSActivity() {
    private lateinit var binding: ActivityCommonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommonBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadInitialFragment()
    }

    /**
     * To initialize transfer fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(PendingVoucherListFragment.newInstance(), false, binding.commonContainer.id)
    }
}
