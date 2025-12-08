package com.moneytree.app.ui.packageVoucher.packageList

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityCommonBinding

class NSPackageListActivity : NSActivity() {
	private lateinit var verifyMemberBinding: ActivityCommonBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		verifyMemberBinding = ActivityCommonBinding.inflate(layoutInflater)
		setContentView(verifyMemberBinding.root)
		initView(verifyMemberBinding.root)
		loadInitialFragment()
	}

	/**
	 * To initialize add redeem fragment
	 *
	 */
	private fun loadInitialFragment() {
		replaceCurrentFragment(NSPackageListFragment.newInstance(), false, verifyMemberBinding.commonContainer.id)
	}
}
