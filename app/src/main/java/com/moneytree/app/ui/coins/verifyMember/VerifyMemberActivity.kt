package com.moneytree.app.ui.coins.verifyMember

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityCommonBinding
import com.moneytree.app.ui.wallets.verifyMember.VerifyMemberFragment

class VerifyMemberActivity : NSActivity() {
	private lateinit var binding: ActivityCommonBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityCommonBinding.inflate(layoutInflater)
		setContentView(binding.root)
		loadInitialFragment(intent.extras)
	}

	/**
	 * To initialize add redeem fragment
	 *
	 */
	private fun loadInitialFragment(bundle: Bundle?) {
		replaceCurrentFragment(VerifyMemberFragment.newInstance(bundle), false, binding.commonContainer.id)
	}
}
