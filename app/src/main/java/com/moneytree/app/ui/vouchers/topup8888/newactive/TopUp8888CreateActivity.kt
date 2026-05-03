package com.moneytree.app.ui.vouchers.topup8888.newactive

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityCommonBinding

class TopUp8888CreateActivity : NSActivity() {
    private lateinit var binding: ActivityCommonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCommonBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView(binding.root)
        loadInitialFragment(intent.extras)
    }

    /**
     * To initialize transfer fragment
     *
     */
    private fun loadInitialFragment(bundle: Bundle?) {
        replaceCurrentFragment(TopUp8888CreateFragment.newInstance(bundle), false, binding.commonContainer.id)
    }
}
