package com.moneytree.app.ui.profile.password

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityChangePasswordBinding

class NSChangePasswordActivity : NSActivity() {
    private lateinit var changePasswordBinding: ActivityChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changePasswordBinding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(changePasswordBinding.root)
        loadInitialFragment(intent.extras)
    }

    /**
     * To initialize transfer fragment
     *
     */
    private fun loadInitialFragment(bundle: Bundle?) {
        replaceCurrentFragment(NSChangePasswordFragment.newInstance(bundle), false, changePasswordBinding.changePasswordContainer.id)
    }
}