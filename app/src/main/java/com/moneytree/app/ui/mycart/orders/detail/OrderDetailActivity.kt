package com.moneytree.app.ui.mycart.orders.detail

import android.os.Bundle
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityCommonBinding

class OrderDetailActivity : NSActivity() {
	private lateinit var stockBinding: ActivityCommonBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		stockBinding = ActivityCommonBinding.inflate(layoutInflater)
		setContentView(stockBinding.root)
		loadInitialFragment(intent.extras)
	}

	/**
	 * To initialize product fragment
	 *
	 */
	private fun loadInitialFragment(bundle: Bundle?) {
		replaceCurrentFragment(
			OrderDetailInfoFragment.newInstance(bundle),
			false,
			stockBinding.commonContainer.id
		)
	}
}
