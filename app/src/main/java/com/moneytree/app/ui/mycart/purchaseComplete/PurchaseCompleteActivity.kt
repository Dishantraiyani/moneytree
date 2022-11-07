package com.moneytree.app.ui.mycart.purchaseComplete

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityPurchaseCompleteBinding
import com.moneytree.app.ui.mycart.cart.NSCartFragment

class PurchaseCompleteActivity : NSActivity() {
	private lateinit var purchaseCompleteBinding: ActivityPurchaseCompleteBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		purchaseCompleteBinding = ActivityPurchaseCompleteBinding.inflate(layoutInflater)
		setContentView(purchaseCompleteBinding.root)
		loadInitialFragment()
	}

	/**
	 * To initialize product fragment
	 *
	 */
	private fun loadInitialFragment() {
		replaceCurrentFragment(NSPurchaseFragment.newInstance(), false, purchaseCompleteBinding.purchaseCompleteContainer.id)
	}
}
