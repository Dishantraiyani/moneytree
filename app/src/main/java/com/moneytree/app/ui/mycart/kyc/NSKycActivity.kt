package com.moneytree.app.ui.mycart.kyc

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityCommonBinding
import com.moneytree.app.ui.mycart.kyc.main.KycBaseFragment

class NSKycActivity : NSActivity() {
    private lateinit var productsBinding: ActivityCommonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        productsBinding = ActivityCommonBinding.inflate(layoutInflater)
        setContentView(productsBinding.root)
        initView(productsBinding.root)
        loadInitialFragment()
    }

    /**
     * To initialize kyc fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(KycBaseFragment.newInstance(), false, productsBinding.commonContainer.id)
    }
}
