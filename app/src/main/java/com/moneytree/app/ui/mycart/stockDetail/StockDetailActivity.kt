package com.moneytree.app.ui.mycart.stockDetail

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityCommonBinding

class StockDetailActivity : NSActivity() {
	private lateinit var stockBinding: ActivityCommonBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		stockBinding = ActivityCommonBinding.inflate(layoutInflater)
		setContentView(stockBinding.root)
		initView(stockBinding.root)
		loadInitialFragment(intent.extras)
	}

	/**
	 * To initialize product fragment
	 *
	 */
	private fun loadInitialFragment(bundle: Bundle?) {
		replaceCurrentFragment(
			StockDetailFragment.newInstance(bundle),
			false,
			stockBinding.commonContainer.id
		)
	}
}
