package com.moneytree.app.ui.wallets.redeemForm

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityAddRedeemBinding

class NSAddRedeemActivity : NSActivity() {
    private lateinit var addRedeemBinding: ActivityAddRedeemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addRedeemBinding = ActivityAddRedeemBinding.inflate(layoutInflater)
        setContentView(addRedeemBinding.root)
        loadInitialFragment()
    }

    /**
     * To initialize add redeem fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(NSAddRedeemFragment.newInstance(), false, addRedeemBinding.addRedeemContainer.id)
    }
}