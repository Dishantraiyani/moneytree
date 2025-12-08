package com.moneytree.app.ui.offers

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityCommonBinding

class OffersActivity : NSActivity() {
	private lateinit var offerBinding: ActivityCommonBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		offerBinding = ActivityCommonBinding.inflate(layoutInflater)
		setContentView(offerBinding.root)
		initView(offerBinding.root)
		loadInitialFragment()
	}

	/**
	 * To initialize product category fragment
	 *
	 */
	private fun loadInitialFragment() {
		replaceCurrentFragment(NSOfferFragment.newInstance(), false, offerBinding.commonContainer.id)
	}
}
