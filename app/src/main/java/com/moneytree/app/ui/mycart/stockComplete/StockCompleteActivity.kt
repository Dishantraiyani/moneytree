package com.moneytree.app.ui.mycart.stockComplete

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.moneytree.app.common.NSActivity
import com.moneytree.app.databinding.ActivityCommonBinding

class StockCompleteActivity : NSActivity() {
	private lateinit var stockCompleteBinding: ActivityCommonBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		stockCompleteBinding = ActivityCommonBinding.inflate(layoutInflater)
		setContentView(stockCompleteBinding.root)
		initView(stockCompleteBinding.root)
		loadInitialFragment()
	}

	/**
	 * To initialize product fragment
	 *
	 */
	private fun loadInitialFragment() {
		replaceCurrentFragment(NSStockCompleteFragment.newInstance(), false, stockCompleteBinding.commonContainer.id)
	}
}
