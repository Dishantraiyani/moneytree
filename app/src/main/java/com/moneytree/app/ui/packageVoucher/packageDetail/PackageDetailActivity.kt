package com.moneytree.app.ui.packageVoucher.packageDetail

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityCommonBinding

class PackageDetailActivity : NSActivity() {
	private lateinit var packageDetailBinding: ActivityCommonBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		packageDetailBinding = ActivityCommonBinding.inflate(layoutInflater)
		setContentView(packageDetailBinding.root)
		loadInitialFragment(intent.extras)
	}

	/**
	 * To initialize add redeem fragment
	 *
	 */
	private fun loadInitialFragment(bundle: Bundle?) {
		replaceCurrentFragment(NSPackageDetailFragment.newInstance(bundle), false, packageDetailBinding.commonContainer.id)
	}
}
