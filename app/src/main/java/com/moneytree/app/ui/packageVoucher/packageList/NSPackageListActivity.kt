package com.moneytree.app.ui.packageVoucher.packageList

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityPackageListBinding
import com.moneytree.app.databinding.ActivityVerifyMemberBinding
import com.moneytree.app.ui.wallets.verifyMember.VerifyMemberFragment

class NSPackageListActivity : NSActivity() {
	private lateinit var verifyMemberBinding: ActivityPackageListBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		verifyMemberBinding = ActivityPackageListBinding.inflate(layoutInflater)
		setContentView(verifyMemberBinding.root)
		loadInitialFragment()
	}

	/**
	 * To initialize add redeem fragment
	 *
	 */
	private fun loadInitialFragment() {
		replaceCurrentFragment(NSPackageListFragment.newInstance(), false, verifyMemberBinding.packageListContainer.id)
	}
}
