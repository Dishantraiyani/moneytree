package com.moneytree.app.ui.vouchers.topup.list

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityCommonBinding
import com.moneytree.app.ui.vouchers.topup.TopUpVoucherFragment

class TopUpVoucherListActivity : NSActivity() {
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
     * To initialize transfer fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(TopupVoucherListFragment.newInstance(), false, binding.commonContainer.id)
    }
}
