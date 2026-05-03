package com.moneytree.app.ui.vouchers.topup8888

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityCommonBinding

class TopUpVoucher8888Activity : NSActivity() {
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
        replaceCurrentFragment(TopUpVoucher8888Fragment.newInstance(), false, binding.commonContainer.id)
    }
}
