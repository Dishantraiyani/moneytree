package com.moneytree.app.ui.invite

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.NsActivityInviteBinding

class NSInviteActivity : NSActivity() {
    private lateinit var activateBinding: NsActivityInviteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activateBinding = NsActivityInviteBinding.inflate(layoutInflater)
        setContentView(activateBinding.root)
        loadInitialFragment()
    }

    /**
     * To initialize product category fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(NSInviteFragment.newInstance(), false, activateBinding.activateInvite.id)
    }
}
