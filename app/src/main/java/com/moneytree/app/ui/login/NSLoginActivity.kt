package com.moneytree.app.ui.login

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.NsActivityLoginBinding

class NSLoginActivity : NSActivity() {
    private lateinit var loginBinding: NsActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = NsActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)
        loadInitialFragment()
    }

    /**
     * To initialize login fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(NSLoginFragment.newInstance(), false, loginBinding.loginContainer.id)
    }
}