package com.moneytree.app.ui.offers

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityOffersBinding

class OffersActivity : NSActivity() {
	private lateinit var offerBinding: ActivityOffersBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		offerBinding = ActivityOffersBinding.inflate(layoutInflater)
		setContentView(offerBinding.root)
		loadInitialFragment()
	}

	/**
	 * To initialize product category fragment
	 *
	 */
	private fun loadInitialFragment() {
		replaceCurrentFragment(NSOfferFragment.newInstance(), false, offerBinding.offerContainer.id)
	}
}
