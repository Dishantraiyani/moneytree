package com.moneytree.app.ui.wallets.verifyMember

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityVerifyMemberBinding

class VerifyMemberActivity : NSActivity() {
	private lateinit var verifyMemberBinding: ActivityVerifyMemberBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		verifyMemberBinding = ActivityVerifyMemberBinding.inflate(layoutInflater)
		setContentView(verifyMemberBinding.root)
		loadInitialFragment(intent.extras)
	}

	/**
	 * To initialize add redeem fragment
	 *
	 */
	private fun loadInitialFragment(bundle: Bundle?) {
		replaceCurrentFragment(VerifyMemberFragment.newInstance(bundle), false, verifyMemberBinding.verifyMemberContainer.id)
	}
}
