package com.moneytree.app.ui.vouchers.topupact

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityCommonBinding
import com.moneytree.app.ui.vouchers.topup.TopUpVoucherFragment
import com.moneytree.app.ui.wallets.transfer.NSTransferFragment

class TopUpVoucherActivationActivity : NSActivity() {
    private lateinit var binding: ActivityCommonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommonBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadInitialFragment(intent.extras)
    }

    /**
     * To initialize transfer fragment
     *
     */
    private fun loadInitialFragment(bundle: Bundle?) {
        replaceCurrentFragment(TopUpVoucherActivationFragment.Companion.newInstance(bundle), false, binding.commonContainer.id)
    }
}
