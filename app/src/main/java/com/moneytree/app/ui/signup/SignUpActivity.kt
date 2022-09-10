package com.moneytree.app.ui.signup

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivitySignUpBinding
import com.moneytree.app.ui.memberTree.MemberTreeFragment

class SignUpActivity : NSActivity() {
    private lateinit var signUpBinding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signUpBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(signUpBinding.root)
        loadInitialFragment()
    }

    /**
     * To initialize home fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(NSSignUpFragment.newInstance(), false, signUpBinding.signupContainer.id)
    }
}
