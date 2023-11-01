package com.moneytree.app.ui.mycart.purchaseComplete

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityCommonBinding

class PurchaseCompleteActivity : NSActivity() {
	private lateinit var purchaseCompleteBinding: ActivityCommonBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		purchaseCompleteBinding = ActivityCommonBinding.inflate(layoutInflater)
		setContentView(purchaseCompleteBinding.root)
		loadInitialFragment()
	}

	/**
	 * To initialize product fragment
	 *
	 */
	private fun loadInitialFragment() {
		replaceCurrentFragment(NSPurchaseFragment.newInstance(), false, purchaseCompleteBinding.commonContainer.id)
	}
}
