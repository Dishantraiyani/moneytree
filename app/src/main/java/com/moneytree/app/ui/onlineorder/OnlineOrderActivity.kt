package com.moneytree.app.ui.onlineorder

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityCommonBinding

class OnlineOrderActivity : NSActivity() {
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
     * To initialize product fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(OnlineOrderFragment.newInstance(), false, binding.commonContainer.id)
    }
}
