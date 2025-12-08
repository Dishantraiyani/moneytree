package com.moneytree.app.ui.idCard

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityCommonBinding

class NSIdCardActivity : NSActivity() {
    private lateinit var homeBinding: ActivityCommonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        homeBinding = ActivityCommonBinding.inflate(layoutInflater)
        setContentView(homeBinding.root)
        initView(homeBinding.root)
        loadInitialFragment()
    }

    /**
     * To initialize home fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(NSIDCardFragment.newInstance(), false, homeBinding.commonContainer.id)
    }
}