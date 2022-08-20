package com.moneytree.app.ui.packageVoucher.packageDetail

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityPackageDetailBinding
import com.moneytree.app.ui.wallets.verifyMember.VerifyMemberFragment

class PackageDetailActivity : NSActivity() {
	private lateinit var packageDetailBinding: ActivityPackageDetailBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		packageDetailBinding = ActivityPackageDetailBinding.inflate(layoutInflater)
		setContentView(packageDetailBinding.root)
		loadInitialFragment(intent.extras)
	}

	/**
	 * To initialize add redeem fragment
	 *
	 */
	private fun loadInitialFragment(bundle: Bundle?) {
		replaceCurrentFragment(NSPackageDetailFragment.newInstance(bundle), false, packageDetailBinding.packageDetailContainer.id)
	}
}
