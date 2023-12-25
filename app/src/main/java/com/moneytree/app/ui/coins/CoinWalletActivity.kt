package com.moneytree.app.ui.coins

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityCommonBinding

class CoinWalletActivity : NSActivity() {
	private lateinit var binding: ActivityCommonBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityCommonBinding.inflate(layoutInflater)
		setContentView(binding.root)
		loadInitialFragment()
	}

	/**
	 * To initialize add redeem fragment
	 *
	 */
	private fun loadInitialFragment() {
		replaceCurrentFragment(NSCoinWalletFragment.newInstance(), false, binding.commonContainer.id)
	}
}
