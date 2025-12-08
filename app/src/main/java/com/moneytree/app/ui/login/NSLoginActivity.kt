package com.moneytree.app.ui.login

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityCommonBinding

class NSLoginActivity : NSActivity() {
    private lateinit var loginBinding: ActivityCommonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        loginBinding = ActivityCommonBinding.inflate(layoutInflater)
        initView(loginBinding.root)
        setContentView(loginBinding.root)
        loadInitialFragment()
    }

    /**
     * To initialize login fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(NSLoginFragment.newInstance(), false, loginBinding.commonContainer.id)
    }
}