package com.moneytree.app.ui.mycart.history

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityCommonBinding

class NSRepuhaseOrStockHistoryActivity : NSActivity() {
	private lateinit var productsBinding: ActivityCommonBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		productsBinding = ActivityCommonBinding.inflate(layoutInflater)
		setContentView(productsBinding.root)
		initView(productsBinding.root)
		loadInitialFragment(intent.extras)
	}

	/**
	 * To initialize product fragment
	 *
	 */
	private fun loadInitialFragment(bundle: Bundle?) {
		replaceCurrentFragment(
			RSHistoryFragment.newInstance(bundle),
			false,
			productsBinding.commonContainer.id
		)
	}
}
